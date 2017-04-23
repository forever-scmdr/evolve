package ecommander.model;

import com.sun.codemodel.JClassAlreadyExistsException;
import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.pages.ValidationResults;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.mappers.DBConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Разбор файла
 * @author EEEE
 * 
 */
class DataModelCreateCommandUnit extends DBPersistenceCommandUnit implements DataModelXmlElementNames, DBConstants {

	public enum Mode {
		safe_update, force_update, load
	}

	private static class HashId {
		private final int hash;
		private final int id;
		private HashId(int hash, int id) {
			this.hash = hash;
			this.id = id;
		}
	}

	private static class ItemParam {
		private final String item;
		private final String param;
		private ItemParam(String item, String param) {
			this.item = item;
			this.param = param;
		}
	}

	/**
	 * Поля класса
	 */
	private ArrayList<String[]> extensionParentChildPairs = new ArrayList<>();
	private HashMap<String, HashId> assocIds = new HashMap<>(); // Название ассоциации => ID ассоциации
	private HashMap<String, HashId> itemIds = new HashMap<>(); // Название айтема => ID айтема
	// Название айтема => (название параметра => ID параметра)
	private HashMap<String, HashMap<String, HashId>> paramIds = new HashMap<>();
	private HashMap<Integer, ArrayList<ParameterDescription>> params = new HashMap<>();
	private HashMap<Integer, String> itemsById = new HashMap<>();
	private HashMap<Integer, ItemParam> paramsById = new HashMap<>();
	private HashMap<Byte, String> assocsById = new HashMap<>();
	//private HashSet<Integer> itemsToRefresh = new HashSet<>(); // айтемы, у которых поменялись параметры и которые нао пересохранить

	private boolean dbChanged = false; // были ли изменения в БД

	private byte maxAssocId = (byte)0;
	private int maxItemId = 0;
	private int maxParamId = 0;
	private final Mode mode;
	private boolean noDeletion = true; // Нужно ли удалять айтемы или параметры из БД в связи с обновлением model.xml
	private HashMap<File, String> xmlFileContents = new HashMap<>();

	DataModelCreateCommandUnit(Mode mode) {
		this.mode = mode;
	}

	public void execute() throws Exception {
		// Очистить реестр айтемов
		ItemTypeRegistry.clearRegistry();
		
		// Загрузить ID всех параметров и айтемов
		loadIds();

		// Парсить загруженную из БД модель данных
		// Если модель не найдена в БД, поменять режим на загрузку из файлов
		if (mode == Mode.load) {
			Statement stmt = getTransactionContext().getConnection().createStatement();
			String selectXML = "SELECT * FROM " + ModelXML.XML_TABLE;
			ServerLogger.debug(selectXML);
			ResultSet rs = stmt.executeQuery(selectXML);
			ArrayList<String> xmls = new ArrayList<>();
			while (rs.next()) {
				xmls.add(rs.getString(ModelXML.XML_XML));
			}
			rs.close();
			for (String xml : xmls) {
				parseFile(xml);
			}
		}
		// Прасить основную модель model.xml (получить базовые определения айтемов и параметров)
		if (mode != Mode.load) {
			ArrayList<File> modelFiles = findModelFiles(new File(AppContext.getMainModelPath()), null);
			for (File modelFile : modelFiles) {
				String xml = FileUtils.readFileToString(modelFile, "UTF-8");
				parseFile(xml);
				xmlFileContents.put(modelFile, xml);
			}

			// Парсить модели пользовательских айтемов
			if (Files.exists(Paths.get(AppContext.getUserModelPath()))) {
				File userFile = new File(AppContext.getUserModelPath());
				String xml = FileUtils.readFileToString(userFile, "UTF-8");
				parseFile(xml);
				xmlFileContents.put(userFile, xml);
			}
		}

		// Создать иерархию айтемов
		ItemTypeRegistry.createHierarchy(extensionParentChildPairs, false);
		
		// Распределить параметры и сабайтемы по айтемам
		HashSet<String> processed = new HashSet<>(30);
		for (String itemName : ItemTypeRegistry.getItemNames()) {
			addParametersAndSubitems(ItemTypeRegistry.getItemType(itemName), processed);
		}
//			TypeHierarchy root = ItemTypeRegistry.getHierarchies(ItemTypeRegistry.getItemNames());
//			addParametersAndSubitems(root);
		
		// Удалить все ненужные записи в таблицах ID и в таблицах индексов по параметрам (делается автоматически через foreign key)
		noDeletion = itemsById.size() == 0 && paramsById.size() == 0;
		if (mode == Mode.force_update) {
			mergeModel();
			// Создать java класс с текстовыми константами для всех айтемов
			createJavaConstants();
			// Добавить сгенерированные ID в файлы модели
			for (File file : xmlFileContents.keySet()) {
				String content = xmlFileContents.get(file);
				fileInjectIds(file, content);
			}
		}
	}

	private Document parseFile(String xml) throws Exception {
		Document doc = Jsoup.parse(xml);
		Elements assocs = doc.getElementsByTag(ASSOC);
		for (Element assoc : assocs) {
			readAssoc(assoc);
		}
		Elements items = doc.getElementsByTag(ITEM);
		for (Element item : items) {
			readItem(item);
		}
		Element root = doc.getElementsByTag(ROOT).first();
		readRoot(root);
		return doc;
	}

	/**
	 * Читает корневой айтем
	 * Сохраняет атрибуты коренвого атйема в столбцы таблицы обычного атйема:
	 * @param rootEl
	 * @throws Exception
	 */
	private void readRoot(Element rootEl) throws Exception {
		// Вложенные айтемы
		Elements children = rootEl.getElementsByTag(CHILD);
		for (Element child : children) {
			readChild(ItemTypeRegistry.getDefaultRoot(), child);
		}
	}

	private void readAssoc(Element assocEl) throws ValidationException, SQLException {
		String name = Strings.createXmlElementName(assocEl.attr(NAME));
		//int newHash = name.hashCode();
		int savedHash = NumberUtils.toInt(assocEl.attr(AG_HASH), 0);
		byte savedId = NumberUtils.toByte(assocEl.attr(AG_ID), (byte)0);
		String caption = assocEl.attr(CAPTION);
		String description = assocEl.attr(DESCRIPTION);
		boolean isTransitive = Boolean.parseBoolean(assocEl.attr(TRANSITIVE));
		boolean nameUpdate = !assocIds.containsKey(name) && assocsById.containsKey(savedId);
		// Если надо обновить название ассоциации
		if (nameUpdate) {
			if (savedHash != assocsById.get(savedId).hashCode()) {
				throw createValidationException("Assoc force_update error",
						"Assoc '" + name + "'",
						"Unable to force_update assoc '" + itemsById.get(savedId) + "' to new name '" + name + "'. Saved hash code doesn't match");
			}
			// Обновить название айтема в таблице ID айтемов
			if (mode == Mode.force_update) {
				try (Statement stmt = getTransactionContext().getConnection().createStatement()) {
					String sql
							= "UPDATE " + AssocIds.AID_TABLE
							+ " SET " + AssocIds.AID_ASSOC_NAME + "='" + name
							+ "' WHERE " + AssocIds.AID_ASSOC_ID + "=" + savedId;
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql);
					dbChanged = true;
				}
			}
			// Заменить название айтема в списке ID айтемов
			assocIds.remove(assocsById.get(savedId));
			assocIds.put(name, new HashId(name.hashCode(), savedId));
		}
		// Если появилась новая ассоциация, которой не было раньше - получить ID для нее
		if (!assocIds.containsKey(name)) {
			byte newId = ++maxAssocId;
			if (mode == Mode.force_update) {
				try (Statement stmt = getTransactionContext().getConnection().createStatement()) {
					String sql = "INSERT " + AssocIds.AID_TABLE + " (" + AssocIds.AID_ASSOC_NAME + ") VALUES ('" + name + "')";
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
					ResultSet keys = stmt.getGeneratedKeys();
					keys.next();
					newId = keys.getByte(1);
					dbChanged = true;
				}
			}
			assocIds.put(name, new HashId(name.hashCode(), newId));
		}
		// Добавить описание айтема в реестр
		Assoc assoc = new Assoc((byte)assocIds.get(name).id, name, caption, description, isTransitive);
		ItemTypeRegistry.addAssoc(assoc);

		// Пометить эту ассоциацию для неудаления
		assocsById.remove((byte) assocIds.get(name).id);

		// Сохранить новые значения для автоматически сгенерированных параметров (ag-id и ag-hash)
		setElementAutoGenerated(assocEl, assocIds.get(name).id, name);
	}
	/**
	 * Читает айтем
	 * @param itemEl
	 * @throws Exception
	 */
	private void readItem(Element itemEl) throws Exception {
		String name = Strings.createXmlElementName(itemEl.attr(NAME));
		int newHash = name.hashCode();
		int savedHash = NumberUtils.toInt(itemEl.attr(AG_HASH), 0);
		int savedId = NumberUtils.toInt(itemEl.attr(AG_ID), 0);
		String key = itemEl.attr(KEY);
		String caption = itemEl.attr(CAPTION);
		String description = itemEl.attr(DESCRIPTION);
		String exts = itemEl.attr(SUPER);
		String defaultPage = itemEl.attr(DEFAULT_PAGE);
		boolean virtual = Boolean.parseBoolean(itemEl.attr(VIRTUAL));
		boolean userDefined = Boolean.parseBoolean(itemEl.attr(USER_DEF));
		boolean isExt = Boolean.parseBoolean(itemEl.attr(EXTENDABLE));
		boolean isKeyUnique = Boolean.parseBoolean(itemEl.attr(KEY_UNIQUE));
		boolean nameUpdate = !itemIds.containsKey(name) && itemsById.containsKey(savedId);
		// Если надо обновить название айтема
		if (nameUpdate) {
			if (savedHash != itemsById.get(savedId).hashCode()) {
				throw createValidationException("Item force_update error",
						"Item '" + name + "'",
						"Unable to force_update item '" + itemsById.get(savedId) + "' to new name '" + name + "'. Saved hash code doesn't match");
			}
			// Обновить название айтема в таблице ID айтемов
			if (mode == Mode.force_update) {
				try (Statement stmt = getTransactionContext().getConnection().createStatement()) {
					String sql
							= "UPDATE " + ItemIds.IID_TABLE
							+ " SET " + ItemIds.IID_ITEM_NAME + "='" + name
							+ "' WHERE " + ItemIds.IID_ITEM_ID + "=" + savedId;
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql);
					dbChanged = true;
				}
			}
			// Заменить название айтема в списке ID айтемов
			itemIds.remove(itemsById.get(savedId));
			itemIds.put(name, new HashId(name.hashCode(), savedId));
		}
		// Если появился новый айтем, которого не было раньше - получить ID для него
		if (!itemIds.containsKey(name)) {
			int newId = ++maxItemId;
			if (mode == Mode.force_update) {
				Statement stmt = getTransactionContext().getConnection().createStatement();
				try {
					String sql = "INSERT " + ItemIds.IID_TABLE + " (" + ItemIds.IID_ITEM_NAME + ") VALUES ('" + name + "')";
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
					ResultSet keys = stmt.getGeneratedKeys();
					keys.next();
					newId = keys.getInt(1);
					dbChanged = true;
				} finally {
					stmt.close();
				}
			}
			itemIds.put(name, new HashId(name.hashCode(), newId));
			paramIds.put(name, new HashMap<String, HashId>());
		}
		// Временно сохранить сведения о иерархии наследования
		if (!StringUtils.isBlank(exts)) {
			String[] parents = StringUtils.split(exts, ItemType.COMMON_DELIMITER + ItemType.ITEM_SELF_PARAMS);
			for (String parent : parents) {
				String[] pair = {parent, name};
				extensionParentChildPairs.add(pair);
			}
		}
		// Добавить описание айтема в реестр
		ItemType item = new ItemType(name, itemIds.get(name).id, caption, description, key,
				exts, defaultPage, virtual, userDefined, isExt, isKeyUnique);
		ItemTypeRegistry.addItemDescription(item);

		// Прочитать вложенные элементы (параметры, дочерние, обработчики)

		// Обработчики
		Elements handlers = itemEl.getElementsByTag(ON_CREATE);
		handlers.addAll(itemEl.getElementsByTag(ON_DELETE));
		handlers.addAll(itemEl.getElementsByTag(ON_UPDATE));
		for (Element handler : handlers) {
			ItemEventCommandFactory factory = null;
			String handlerClass = handler.attr(CLASS);
			try {
				factory = (ItemEventCommandFactory) Class.forName(handlerClass).getConstructor().newInstance();
			} catch (Exception e) {
				ServerLogger.warn("Unable to create class for name '" + handlerClass + "'", e);
				//throw new EcommanderException(e);
			}
			if (factory != null)
				item.addExtraHandler(ItemType.Event.get(handler.tagName()), factory);
		}

		// Вложенные айтемы
		Elements children = itemEl.getElementsByTag(CHILD);
		for (Element child : children) {
			readChild(item, child);
		}

		// Параметры
		Elements params = itemEl.getElementsByTag(PARAMETER);
		for (Element param : params) {
			readParameter(item, param);
		}

		// Пометить этот айтем для неудаления
		itemsById.remove(itemIds.get(name).id);

		// Сохранить новые значения для автоматически сгенерированных параметров (ag-id и ag-hash)
		setElementAutoGenerated(itemEl, itemIds.get(name).id, name);
	}
	/**
	 * Читает параметры
	 * Сначала параметры добавляются во временную структуру, 
	 * чтобы была возможность добавлять параметры во все потомки айтема сразу,
	 * а для этого надо сначала создать иерархию айтемов
	 * @param item
	 * @param paramEl
	 * @throws Exception
	 */
	private void readParameter(ItemType item, Element paramEl) throws Exception {
		String name = paramEl.attr(NAME);
		int newHash = name.hashCode();
		int savedHash = NumberUtils.toInt(paramEl.attr(AG_HASH), 0);
		int savedId = NumberUtils.toInt(paramEl.attr(AG_ID), 0);
		String caption = paramEl.attr(CAPTION);
		String description = paramEl.attr(DESCRIPTION);
		String domainName = paramEl.attr(DOMAIN);
		boolean isMultiple = StringUtils.equalsIgnoreCase(paramEl.attr(MULTIPLE), TRUE_VALUE);
		String dataTypeName = paramEl.attr(TYPE);
		String format = paramEl.attr(FORMAT);
		boolean isHidden = Boolean.valueOf(paramEl.attr(HIDDEN));
		boolean isVirtual = Boolean.parseBoolean(paramEl.attr(VIRTUAL));
		String textIndexStr = paramEl.attr(TEXT_INDEX);
		ParameterDescription.TextIndex textIndex = ParameterDescription.TextIndex.none;
		String textIndexParameter = paramEl.attr(TEXT_INDEX_PARAMETER);
		String textIndexParser = paramEl.attr(TEXT_INDEX_PARSER);
		String textIndexBoostStr = paramEl.attr(TEXT_INDEX_BOOST);
		String defaultValue = paramEl.attr(DEFAULT);
		ComputedDescription.Func func = ComputedDescription.Func.get(paramEl.attr(FUNCTION));
		float textIndexBoost = -1f;
		if (!StringUtils.isBlank(textIndexStr)) {
			try {
				textIndex = ParameterDescription.TextIndex.valueOf(ParameterDescription.TextIndex.class, textIndexStr);
				if (!StringUtils.isBlank(textIndexBoostStr))
					textIndexBoost = Float.parseFloat(textIndexBoostStr);
			} catch (Exception e) {
				throw createValidationException("Fulltext search setup incorrect", "Item '" + item.getName() + "'",
						"fulltext search parameters provided are not correct");
			}
		}

		boolean nameUpdate = !paramIds.get(item.getName()).containsKey(name) && paramsById.containsKey(savedId);
		// Если надо обновить название параметра
		if (nameUpdate) {
			if (savedHash != paramsById.get(savedId).param.hashCode()) {
				throw createValidationException("Parameter force_update error",
						"Item '" + item.getName() + "' parameter '" + name + "'",
						"Unable to force_update parameter '" + itemsById.get(savedId) + "' to new name '" + name + "'. Saved hash code doesn't match");
			}
			// Обновить название параметра в таблице ID параметров
			if (mode == Mode.force_update) {
				try (Statement stmt = getTransactionContext().getConnection().createStatement()) {
					String sql
							= "UPDATE " + ParamIds.PID_TABLE
							+ " SET " + ParamIds.PID_PARAM_NAME + "='" + name
							+ "' WHERE " + ParamIds.PID_PARAM_ID + "=" + savedId;
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql);
					dbChanged = true;
				}
			}
			// Заменить название параметра в списке ID параметров
			paramIds.get(item.getName()).remove(paramsById.get(savedId).param);
			paramIds.get(item.getName()).put(name, new HashId(name.hashCode(), savedId));
		}
		// Если появился новый параметр, которого не было раньше - получить ID для него
		if (!paramIds.get(item.getName()).containsKey(name)) {
			int newId = ++maxParamId;
			if (mode == Mode.force_update) {
				try (Statement stmt = getTransactionContext().getConnection().createStatement()) {
					String sql
							= "INSERT " + ParamIds.PID_TABLE + " ("
							+ ParamIds.PID_ITEM_ID + ", " + ParamIds.PID_PARAM_NAME
							+ ") VALUES (" + item.getTypeId() + ", '" + name + "')";
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
					ResultSet keys = stmt.getGeneratedKeys();
					keys.next();
					newId = keys.getInt(1);
					dbChanged = true;
				}
			}
			paramIds.get(item.getName()).put(name, new HashId(name.hashCode(), newId));
		} else {
			paramsById.remove(paramIds.get(item.getName()).get(name).id);
		}
		int paramId = paramIds.get(item.getName()).get(name).id;
		ParameterDescription param = new ParameterDescription(name, paramId, dataTypeName, isMultiple, item.getTypeId(),
				domainName, caption, description, format, isVirtual, isHidden, defaultValue, func);
		if (textIndex != ParameterDescription.TextIndex.none)
			param.setFulltextSearch(textIndex, textIndexParameter, textIndexBoost, textIndexParser);

		// Если есть функция - надо считать базовый параметр
		if (func != null) {
			Elements baseParams = paramEl.getElementsByTag(BASE_CHILD);
			baseParams.addAll(paramEl.getElementsByTag(BASE_PARENT));
			for (Element base : baseParams) {
				param.getComputed().addBasic(ComputedDescription.Type.get(base.tagName()), base.attr(ITEM),
						base.attr(PARAMETER), base.attr(ASSOC));
			}
		}

		// Добавление в реестр невозможно, т.к. неизвестны еще все потомки данного айтема по иерархии,
		// поэтому сначала добавление в специальную структуру
		ArrayList<ParameterDescription> itemParams = params.get(item.getTypeId());
		if (itemParams == null) {
			itemParams = new ArrayList<ParameterDescription>();
			params.put(item.getTypeId(), itemParams);
		}
		itemParams.add(param);

		// Сохранить новые значения для автоматически сгенерированных параметров (ag-id и ag-hash)
		setElementAutoGenerated(paramEl, paramIds.get(item.getName()).get(name).id, name);
	}
	/**
	 * Распределение параметров и сабайтемов по айтемам с учетом их иерархии
	 * @param item
	 * @param processedItems
	 */
	private void addParametersAndSubitems(ItemType item, HashSet<String> processedItems) {
		if (processedItems.contains(item.getName()))
			return;
		String[] exts = StringUtils.split(item.getExtendsString(), ItemType.COMMON_DELIMITER);
		for (String ext : exts) {
			if (ext.equals(ItemType.ITEM_SELF_PARAMS)) {
				ArrayList<ParameterDescription> itemParams = params.get(item.getTypeId());
				// Добавление всех параметров в сам айтем
				if (itemParams != null) {
					for (ParameterDescription param : itemParams) {
						item.putParameter(param);
					}
				}
			} else {
				// Предшественник
				ItemType predecessor = ItemTypeRegistry.getItemType(ext);
				addParametersAndSubitems(predecessor, processedItems);
				// Установить супертип для пользовательского айтема
				if (item.isUserDefined())
					item.setSuperType(predecessor.getSuperType());
				// Добавление всех параметров предшественника
				for (ParameterDescription param : predecessor.getParameterList()) {
					item.putParameter(param);
				}
				// Добавление ключевого параметра
				if (predecessor.hasKey()) {
					item.addKeyParameter(predecessor.getKey(), predecessor.isKeyUnique());
				}
				// Добавление сабайтемов
				item.addAllChildren(predecessor);
				// Установить extra-handler
				if (predecessor.hasExtraHandlers()) {
					predecessor.addExtraHandlersToItem(item);
				}
			}
		}
		// Айтем обработан - добавить в список обработанных
		processedItems.add(item.getName());
	}
	/**
	 * Читает сабайтемы
	 * @param parent
	 * @param childEl
	 * @throws Exception
	 */
	private void readChild(ItemTypeContainer parent, Element childEl) throws Exception {
		// По очереди все сабайтемы
		String childName = childEl.attr(NAME);
		String assocName = childEl.attr(ASSOC);
		boolean isSingle = StringUtils.equalsIgnoreCase(childEl.attr(SINGLE), TRUE_VALUE);
		boolean isVitrual = StringUtils.equalsIgnoreCase(childEl.attr(VIRTUAL), TRUE_VALUE);
		parent.addOwnChild(assocName, childName, isSingle, isVitrual);
	}

	/**
	 * Сохранить новые значения для автоматически сгенерированных параметров (ag-id и ag-hash)
	 * @param el
	 * @param id
	 * @param name
	 */
	private void setElementAutoGenerated(Element el, int id, String name) {
		if (!StringUtils.equals(el.attr(AG_ID), id + "") ||
				!StringUtils.equals(el.attr(AG_HASH), name.hashCode() + "")) {
			el.attr(AG_ID, id + "");
			el.attr(AG_HASH, name.hashCode() + "");
		}
	}
	/**
	 * Очищает модель данных от удаленных айтемов и параметров.
	 * Изменяет старые параметры в соответствии с новой моделью (при перемещении параметров из одного атйема в другой)
	 * @throws SQLException 
	 */
	private void mergeModel() throws SQLException {
		try (Statement stmt = getTransactionContext().getConnection().createStatement()) {
			// ********************** Очистить таблицы ******************************
			String sql;
			// Удаление из таблицы ID айтемов
			if (itemsById.size() > 0) {
				sql = "DELETE FROM " + ItemIds.IID_TABLE
						+ " WHERE " + ItemIds.IID_ITEM_ID + " IN " + createIn(itemsById.keySet());
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
				dbChanged = true;
			}
			// Удаление параметров, если нужно удалять,
			// изменение ID параметров, если параметр переместился в родительский класс
			HashSet<Integer> paramsToDeleteFromIndex = new HashSet<>();
			if (paramsById.size() > 0) {
				for (Map.Entry<Integer, ItemParam> par : paramsById.entrySet()) {
					ItemParam ip = par.getValue();
					int pid = par.getKey();
					int newParentItemId = itemIds.get(ip.item).id;
					ParameterDescription newParamDesc = null; // новый ID параметра, на который надо поменять старый ID в таблицах индексов
					Set<String> predNames = ItemTypeRegistry.getItemPredecessors(ip.item);
					for (String predName : predNames) {
						newParamDesc = ItemTypeRegistry.getItemType(predName).getParameter(ip.param);
						if (newParamDesc != null)
							break;
					}
					if (newParamDesc == null) {
						paramsToDeleteFromIndex.add(pid);
					}
					// Обновить таблицу параметров - установить нового предка параметра
					sql = "UPDATE " + ParamIds.PID_TABLE + " SET " + ParamIds.PID_ITEM_ID + "=" +
							newParentItemId + " WHERE " + ParamIds.PID_PARAM_ID + "=" + pid;
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql);
					dbChanged = true;
				}
			}
			// Удаление из таблиц индексов параметров
			if (paramsToDeleteFromIndex.size() > 0) {
				String[] tables = {ItemIndexes.INT_TABLE_NAME, ItemIndexes.DOUBLE_TABLE_NAME, ItemIndexes.STRING_TABLE_NAME};
				for (String table : tables) {
					sql = "DELETE FROM " + table + " WHERE " + ItemIndexes.II_PARAM + " IN " + createIn(paramsToDeleteFromIndex);
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql);
				}
				dbChanged = true;
			}
			// Удаление из главной таблицы айтемов
			if (itemsById.size() > 0) {
				sql = "DELETE FROM " + ItemTbl.I_TABLE + " WHERE " + ItemTbl.I_TYPE_ID + " IN " + createIn(itemsById.keySet());
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
				dbChanged = true;
			}
			// Сохранить в БД содержимое файлов модели данных
			for (Map.Entry<File, String> file : xmlFileContents.entrySet()) {
				try {
					sql
							= "INSERT " + ModelXML.XML_TABLE + " ("
							+ ModelXML.XML_NAME + ", " + ModelXML.XML_XML
							+ ") VALUES ('" + file.getKey().getName() + "', '" + file.getValue() + "') ON DUPLICATE KEY UPDATE "
							+ ModelXML.XML_XML + "='" + file.getValue() + "'";
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql);
				} finally {
					stmt.close();
				}
			}
		}
		//clearSql = "SET FOREIGN_KEY_CHECKS=0";
		//clearSql = "SET FOREIGN_KEY_CHECKS=1";
	}
	
	private void loadIds() throws SQLException {
		Statement stmt = getTransactionContext().getConnection().createStatement();

		String selectAssocIds = "SELECT * FROM " + AssocIds.AID_TABLE;
		ServerLogger.debug(selectAssocIds);
		ResultSet rs = stmt.executeQuery(selectAssocIds);
		while (rs.next()) {
			byte assocId = rs.getByte(AssocIds.AID_ASSOC_ID);
			String assocName = rs.getString(AssocIds.AID_ASSOC_NAME);
			assocIds.put(assocName, new HashId(assocName.hashCode(), assocId));
			assocsById.put(assocId, assocName);
			if (assocId > maxAssocId)
				maxAssocId = assocId;
		}
		rs.close();

		String selectItemIds = "SELECT * FROM " + ItemIds.IID_TABLE;
		ServerLogger.debug(selectItemIds);
		rs = stmt.executeQuery(selectItemIds);
		while (rs.next()) {
			int itemId = rs.getInt(ItemIds.IID_ITEM_ID);
			String itemName = rs.getString(ItemIds.IID_ITEM_NAME);
			itemIds.put(itemName, new HashId(itemName.hashCode(), itemId));
			itemsById.put(itemId, itemName);
			paramIds.put(itemName, new HashMap<String, HashId>());
			if (itemId > maxItemId)
				maxItemId = itemId;
		}
		rs.close();
		// удаление ненужных но почему-то присутствующих параметров
		String deleteParamIds 
			= "DELETE FROM " + ParamIds.PID_TABLE + " WHERE " + ParamIds.PID_ITEM_ID
			+ " NOT IN (SELECT " + ItemIds.IID_ITEM_ID + " FROM " + ItemIds.IID_TABLE + ")";
		ServerLogger.debug(deleteParamIds);
		stmt.executeUpdate(deleteParamIds);

		String selectParamIds = "SELECT * FROM " + ParamIds.PID_TABLE;
		ServerLogger.debug(selectParamIds);
		rs = stmt.executeQuery(selectParamIds);
		while (rs.next()) {
			int itemId = rs.getInt(ParamIds.PID_ITEM_ID);
			int paramId = rs.getInt(ParamIds.PID_PARAM_ID);
			String paramName = rs.getString(ParamIds.PID_PARAM_NAME);
			paramIds.get(itemsById.get(itemId)).put(paramName, new HashId(paramName.hashCode(), paramId));
			paramsById.put(paramId, new ItemParam(itemsById.get(itemId), paramName));
			if (paramId > maxParamId)
				maxParamId = paramId;
		}
		stmt.close();
	}
	/**
	 * Создать SQL критерий IN
	 * @param ids
	 * @return
	 */
	private String createIn(Collection<Integer> ids) {
		StringBuilder result = new StringBuilder("(");
		for (Integer id : ids) {
			result.append(id).append(',');
		}
		result.deleteCharAt(result.length() - 1);
		result.append(')');
		return result.toString();
	}

	private void createJavaConstants() {
		if (!dbChanged)
			return;
		CodeGenerator.createJavaConstants();
	}

	/**
	 * Должны би удаляться айтемы или параметры из БД в связи с обновлением model.xml
	 * @return
	 */
	boolean isNoDeletionNeeded() {
		return noDeletion;
	}

	/**
	 * Генерирует список удаляемых в обновлении model.xml айтемов и параметров
	 * @return
	 */
	ArrayList<String> getElementsToDelete() {
		ArrayList<String> result = new ArrayList<>();
		for (String itemName : itemsById.values()) {
			result.add("Item: " + itemName);
		}
		for (ItemParam param : paramsById.values()) {
			result.add("Parameter: " + param.item + "." + param.param);
		}
		return result;
	}
	/**
	 * Находит все файлы model.xml
	 * @param startFile
	 * @param files
	 * @return
	 */
	static ArrayList<File> findModelFiles(File startFile, ArrayList<File> files) {
		if (files == null) {
			files = new ArrayList<File>();
			if (startFile.isFile())
				files.add(startFile);
			else
				return findModelFiles(startFile, files);
		} else {
			if (startFile.isDirectory()) {
				File[] filesList = startFile.listFiles();
				if (filesList != null) {
					for (File file : filesList) {
						if (file.isFile())
							files.add(file);
						else if (file.isDirectory())
							findModelFiles(file, files);
					}
				}
			}
		}
		return files;
	}

	private static ValidationException createValidationException(String errorName, String originator, String message) {
		ValidationResults results = new ValidationResults();
		results.addError(originator, message);
		return new ValidationException(errorName, results);
	}

	/**
	 * Сохранить файл с добавленными атрибутами ag-hash и ag-id
	 * @param file
	 * @param xml
	 * @throws IOException
	 */
	private void fileInjectIds(File file, String xml) throws IOException {
		String oldXML = xml;
		// Найти все элементы, в которых должны быть сгенерированные атрибуты

		// Ассоциации
		for (String assoc : assocIds.keySet()) {
			xml = insertAgs(xml, ASSOC, assoc, assocIds.get(assoc).id, assoc.hashCode());
		}

		// Айтемы
		for (String item : itemIds.keySet()) {
			xml = insertAgs(xml, ITEM, item, itemIds.get(item).id, item.hashCode());
		}

		// Параметры (вначале найти айтемы, т.к. параметры уникальны только внутри одного айтема)
		for (String itemName : paramIds.keySet()) {
			HashMap<String, HashId> itemParams = paramIds.get(itemName);
			Pattern pattern = Pattern.compile(
					"<" + ITEM + "\\s+.*?" + NAME + "\\s*?=\\s*?\"" + itemName + "\".*?>(.|\\r\\n|\\n)+?</" + ITEM + ">");
			Matcher matcher = pattern.matcher(xml);
			if (matcher.find()) {
				String itemPart = matcher.group(0);
				String newItemPart = itemPart;
				for (String paramName : itemParams.keySet()) {
					int paramId = itemParams.get(paramName).id;
					newItemPart = insertAgs(newItemPart, PARAMETER, paramName, paramId, paramName.hashCode());
				}
				xml = StringUtils.replace(xml, itemPart, newItemPart);
			}
		}

		String absPath = file.getAbsolutePath();
		String filePath = absPath.substring(0, absPath.lastIndexOf(File.separator));
		String newName = "~" + file.getName();
		File backupFile = new File(filePath + File.separator + newName);
		FileUtils.writeStringToFile(backupFile, oldXML, "UTF-8");
		file.renameTo(backupFile);
		FileUtils.writeStringToFile(file, xml, "UTF-8");
	}

	private String insertAgs(String contents, String tag, String name, int agId, int agHash) {
		// Найти нужный элемент
		Pattern pattern = Pattern.compile("<" + tag + "\\s+.*?" + NAME + "\\s*?=\\s*?\"" + name + "\".*?>");
		Matcher matcher = pattern.matcher(contents);
		if (matcher.find()) {
			String oldPart = matcher.group(0);
			// Удалить старые ag-hash и ag-id
			String newPart = oldPart.replaceAll("\\s+" + AG_ID + "\\s*?=\\s*?\".*?\"", "");
			newPart = newPart.replaceAll("\\s+" + AG_HASH + "\\s*?=\\s*?\".*?\"", "");
			String newAg = " " + AG_ID + "=\"" + agId + "\" " + AG_HASH + "=\"" + agHash + "\"";
			int lastQuote = newPart.lastIndexOf('"');
			String newStart = newPart.substring(0, lastQuote + 1);
			String newEnd = newPart.substring(lastQuote + 1);
			newPart = newStart + newAg + newEnd;
			contents = matcher.replaceFirst(newPart);
		}
		return contents;
	}

	public static void main(String[] args) throws JClassAlreadyExistsException, IOException {
//		String str = "<eeee cool='rrrr' mega=\"dddd\" ultra='ffff'/><vvvv cool='dfdf' mega=\"ccccccc\"/>";
//		str = str.replaceAll(" mega=\"[^\"]*\"", "");
//		System.out.println(str.replaceAll(" mega=\"[^\"]*\"", ""));
//        URL location = DataModelCreateCommandUnit.class.getProtectionDomain().getCodeSource().getLocation();
//        System.out.println(location.getFile());

	}
}
