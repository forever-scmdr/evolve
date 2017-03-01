package ecommander.model;

import com.sun.codemodel.JClassAlreadyExistsException;
import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.pages.ValidationResults;
import ecommander.persistence.TransactionException;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.DataTypeMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Разбор файла
 * @author EEEE
 * 
 */
class DataModelCreateCommandUnit extends DBPersistenceCommandUnit implements DataModelXmlElementNames {

	private static class HashId {
		private final int hash;
		private final int id;
		public HashId(int hash, int id) {
			this.hash = hash;
			this.id = id;
		}
	}

	private static class ItemParam {
		private final String item;
		private final String param;
		public ItemParam(String item, String param) {
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
	// ID айтема => (название параметра => ID параметра)
	private HashMap<Integer, HashMap<String, HashId>> paramIds = new HashMap<>();
	private HashMap<Integer, ArrayList<ParameterDescription>> params = new HashMap<>();
	private HashMap<Integer, String> itemsById = new HashMap<>();
	private HashMap<Integer, ItemParam> paramsById = new HashMap<>();
	private HashMap<Byte, String> assocsById = new HashMap<>();
	//private HashSet<Integer> itemsToRefresh = new HashSet<>(); // айтемы, у которых поменялись параметры и которые нао пересохранить
	private UUID siteId = null; // уникальный идентификатор сайта, чтобы не перепутать с
	private int modelVersion = 0;

	private boolean dbChanged = false; // были ли изменения в БД
	private boolean fileChanged = false;

	private byte maxAssocId = (byte)0;
	private int maxItemId = 0;
	private int maxParamId = 0;
	private final boolean isTestMode;
	private boolean noDeletion = true; // Нужно ли удалять айтемы или параметры из БД в связи с обновлением model.xml

	public DataModelCreateCommandUnit(boolean isTestMode) {
		this.isTestMode = isTestMode;
	}

	public void execute() throws Exception {
		// Очистить реестр айтемов
		ItemTypeRegistry.clearRegistry();
		
		// Загрузить ID всех параметров и айтемов
		loadIds();

		// Прасить основную модель model.xml (получить базовые определения айтемов и параметров)
		ArrayList<File> modelFiles = findModelFiles(new File(AppContext.getMainModelPath()), null);
		for (File modelFile : modelFiles) {
			parseFile(modelFile);
		}
		
		// Парсить модели пользовательских айтемов
		if (Files.exists(Paths.get(AppContext.getUserModelPath())))
			parseFile(new File(AppContext.getUserModelPath()));

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
		if (!isTestMode) {
			mergeModel();
			// Создать java класс с текстовыми константами для всех айтемов
			createJavaConstants();
		}
	}

	private void parseFile(File file) throws Exception {
		Document doc = Jsoup.parse(file, "UTF-8");
		Elements assocs = doc.getElementsByTag(ASSOC);
		for (Element assoc : assocs) {
			readAssoc(assoc);
		}
		Elements items = doc.getElementsByTag(ITEM);
		for (Element item : items) {
			readItem(item);
		}
		Element root = doc.getElementsByTag(ROOT).first();
		readRoot();
	}

	/**
	 * Читает корневой айтем
	 * Сохраняет атрибуты коренвого атйема в столбцы таблицы обычного атйема:
	 * @param attributes
	 * @throws Exception
	 */
	protected void readRoot() throws Exception {
		Statement stmt = null;
		try {
			// Загрузить ID корней из БД
			stmt = getTransactionContext().getConnection().createStatement();
			String sql 
					= "SELECT " + DBConstants.Item.ID + " FROM " + DBConstants.Item.TABLE 
					+ " WHERE " + DBConstants.Item.ID + "=0";
			ServerLogger.debug(sql);
			ResultSet rs = stmt.executeQuery(sql);
			boolean hasRoot = rs.next();
			rs.close();
			// Сохранить рут в таблице конкретных айтемов, в случае если такого рута нет
			if (!hasRoot) {
				sql
					= "INSERT INTO "
						+ DBConstants.Item.TABLE
						+ " ("
						+ DBConstants.Item.ID + ", "
						+ DBConstants.Item.TYPE_ID + ", "
						+ DBConstants.Item.KEY + ", "
						+ DBConstants.Item.TRANSLIT_KEY + ", "
						+ DBConstants.Item.INDEX_WEIGHT + ", "
						+ DBConstants.Item.PARAMS
						+ ") VALUES (0, 0, 'root', 'root', 0, '')";
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
				dbChanged = true;
			}
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	private void readAssoc(Element assocEl) throws ValidationException, SQLException {
		String name = Strings.createXmlElementName(assocEl.attr(NAME));
		int newHash = name.hashCode();
		int savedHash = NumberUtils.toInt(assocEl.attr(AG_HASH), 0);
		byte savedId = NumberUtils.toByte(assocEl.attr(AG_ID), (byte)0);
		String caption = assocEl.attr(CAPTION);
		String description = assocEl.attr(DESCRIPTION);
		boolean isTransitive = Boolean.parseBoolean(assocEl.attr(TRANSITIVE));
		boolean nameUpdate = !assocIds.containsKey(name) && assocsById.containsKey(savedId);
		// Если надо обновить название ассоциации
		if (nameUpdate) {
			if (savedHash != assocsById.get(savedId).hashCode()) {
				throw createValidationException("Assoc update error",
						"Assoc '" + name + "'",
						"Unable to update assoc '" + itemsById.get(savedId) + "' to new name '" + name + "'. Saved hash code doesn't match");
			}
			// Обновить название айтема в таблице ID айтемов
			if (!isTestMode) {
				Statement stmt = getTransactionContext().getConnection().createStatement();
				try {
					String sql
							= "UPDATE " + DBConstants.AssocIds.TABLE
							+ " SET " + DBConstants.AssocIds.ASSOC_NAME + "='" + name
							+ "' WHERE " + DBConstants.AssocIds.ASSOC_ID + "=" + savedId;
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql);
					dbChanged = true;
				} finally {
					stmt.close();
				}
			}
			// Заменить название айтема в списке ID айтемов
			assocIds.remove(assocsById.get(savedId));
			assocIds.put(name, new HashId(name.hashCode(), savedId));
		}
		// Если появилась новая ассоциация, которой не было раньше - получить ID для нее
		if (!assocIds.containsKey(name)) {
			byte newId = ++maxAssocId;
			if (!isTestMode) {
				Statement stmt = getTransactionContext().getConnection().createStatement();
				try {
					String sql = "INSERT " + DBConstants.AssocIds.TABLE + " (" + DBConstants.AssocIds.ASSOC_NAME + ") VALUES ('" + name + "')";
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
					ResultSet keys = stmt.getGeneratedKeys();
					keys.next();
					newId = keys.getByte(1);
					dbChanged = true;
				} finally {
					stmt.close();
				}
			}
			assocIds.put(name, new HashId(name.hashCode(), newId));
		}
		// Добавить описание айтема в реестр
		Assoc assoc = new Assoc((byte)itemIds.get(name).id, name, caption, description, isTransitive);
		ItemTypeRegistry.addAssoc(assoc);

		// Пометить эту ассоциацию для неудаления
		assocsById.remove((byte) assocIds.get(name).id);

		// Сохранить новые значения для автоматически сгенерированных параметров (ag-id и ag-hash)
		setElementAutoGenerated(assocEl, assocIds.get(name).id, name);
	}
	/**
	 * Читает айтем
	 * @param itemNode
	 * @param conn
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
		boolean isInline = Boolean.parseBoolean(itemEl.attr(INLINE));
		boolean isExt = Boolean.parseBoolean(itemEl.attr(EXTENDABLE));
		boolean isKeyUnique = Boolean.parseBoolean(itemEl.attr(KEY_UNIQUE));
		boolean nameUpdate = !itemIds.containsKey(name) && itemsById.containsKey(savedId);
		// Если надо обновить название айтема
		if (nameUpdate) {
			if (savedHash != itemsById.get(savedId).hashCode()) {
				throw createValidationException("Item update error",
						"Item '" + name + "'",
						"Unable to update item '" + itemsById.get(savedId) + "' to new name '" + name + "'. Saved hash code doesn't match");
			}
			// Обновить название айтема в таблице ID айтемов
			if (!isTestMode) {
				Statement stmt = getTransactionContext().getConnection().createStatement();
				try {
					String sql
							= "UPDATE " + DBConstants.ItemIds.TABLE
							+ " SET " + DBConstants.ItemIds.ITEM_NAME + "='" + name
							+ "' WHERE " + DBConstants.ItemIds.ITEM_ID + "=" + savedId;
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql);
					dbChanged = true;
				} finally {
					stmt.close();
				}
			}
			// Заменить название айтема в списке ID айтемов
			itemIds.remove(itemsById.get(savedId));
			itemIds.put(name, new HashId(name.hashCode(), savedId));
		}
		// Если появился новый айтем, которого не было раньше - получить ID для него
		if (!itemIds.containsKey(name)) {
			int newId = ++maxItemId;
			if (!isTestMode) {
				Statement stmt = getTransactionContext().getConnection().createStatement();
				try {
					String sql = "INSERT " + DBConstants.ItemIds.TABLE + " (" + DBConstants.ItemIds.ITEM_NAME + ") VALUES ('" + name + "')";
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
			paramIds.put(newId, new HashMap<String, HashId>());
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
				exts, defaultPage, virtual, userDefined, isInline, isExt, isKeyUnique);
		ItemTypeRegistry.addItemDescription(item);

		// Прочитать вложенные элементы (параметры, дочерние, обработчики)

		// Обработчики
		Elements handlers = itemEl.getElementsByTag(ON_CREATE);
		handlers.addAll(itemEl.getElementsByTag(ON_DELETE));
		handlers.addAll(itemEl.getElementsByTag(ON_UPDATE));
		for (Element handler : handlers) {
			try {
				String handlerClass = handler.attr(CLASS);
				ItemEventCommandFactory factory = (ItemEventCommandFactory) Class.forName(handlerClass).getConstructor().newInstance();
				item.addExtraHandler(ItemType.Event.get(handler.tagName()), factory);
			} catch (Exception e) {
				throw new EcommanderException(e);
			}
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
	 * @param attributes
	 * @param itemName
	 * @throws Exception
	 */
	protected void readParameter(ItemType item, Element paramEl) throws Exception {
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

		boolean nameUpdate = !paramIds.containsKey(name) && paramsById.containsKey(savedId);
		// Если надо обновить название параметра
		if (nameUpdate) {
			if (savedHash != paramsById.get(savedId).param.hashCode()) {
				throw createValidationException("Parameter update error",
						"Item '" + item.getName() + "' parameter '" + name + "'",
						"Unable to update parameter '" + itemsById.get(savedId) + "' to new name '" + name + "'. Saved hash code doesn't match");
			}
			// Обновить название параметра в таблице ID параметров
			if (!isTestMode) {
				Statement stmt = getTransactionContext().getConnection().createStatement();
				try {
					String sql
							= "UPDATE " + DBConstants.ParamIds.TABLE
							+ " SET " + DBConstants.ParamIds.PARAM_NAME + "='" + name
							+ "' WHERE " + DBConstants.ParamIds.PARAM_ID + "=" + savedId;
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql);
					dbChanged = true;
				} finally {
					stmt.close();
				}
			}
			// Заменить название параметра в списке ID параметров
			paramIds.get(item.getTypeId()).remove(paramsById.get(savedId).param);
			paramIds.get(item.getTypeId()).put(name, new HashId(name.hashCode(), savedId));
		}
		// Если появился новый параметр, которого не было раньше - получить ID для него
		if (!paramIds.get(item.getTypeId()).containsKey(name)) {
			int newId = ++maxParamId;
			if (!isTestMode) {
				Statement stmt = getTransactionContext().getConnection().createStatement();
				try {
					String sql
							= "INSERT " + DBConstants.ParamIds.TABLE + " ("
							+ DBConstants.ParamIds.ITEM_ID + ", " + DBConstants.ParamIds.PARAM_NAME
							+ ") VALUES (" + item.getTypeId() + ", '" + name + "')";
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
			paramIds.get(item.getTypeId()).put(name, new HashId(name.hashCode(), newId));
		} else {
			paramsById.remove(paramIds.get(item.getTypeId()).get(name).id);
		}
		int paramId = paramIds.get(item.getTypeId()).get(name).id;
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
		setElementAutoGenerated(paramEl, paramIds.get(item.getTypeId()).get(name).id, name);
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
	 * @param subitemNode
	 * @param conn
	 * @param itemName
	 * @throws Exception
	 */
	protected void readChild(ItemTypeContainer parent, Element childEl) throws Exception {
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
			fileChanged = true;
		}
	}
	/**
	 * Очищает модель данных от удаленных айтемов и параметров.
	 * Изменяет старые параметры в соответствии с новой моделью (при перемещении параметров из одного атйема в другой)
	 * @throws SQLException 
	 */
	protected void mergeModel() throws TransactionException, SQLException {
		Statement stmt = getTransactionContext().getConnection().createStatement();
		try {
			// ********************** Очистить таблицы ******************************
			String sql;
			// Удаление из таблицы ID айтемов
			if (itemsById.size() > 0) {
				sql = "DELETE FROM " + DBConstants.ItemIds.TABLE
						+ " WHERE " + DBConstants.ItemIds.ITEM_ID + " IN " + createIn(itemsById.keySet());
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
					sql = "UPDATE " + DBConstants.ParamIds.TABLE + " SET " + DBConstants.ParamIds.ITEM_ID + "=" +
							newParentItemId + " WHERE " + DBConstants.ParamIds.PARAM_ID + "=" + pid;
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql);
					dbChanged = true;
				}
			}
			// Удаление из таблиц индексов параметров
			if (paramsToDeleteFromIndex.size() > 0) {
				String[] tables 
					= {DBConstants.ItemIndexes.INT_TABLE_NAME, DBConstants.ItemIndexes.DOUBLE_TABLE_NAME, DBConstants.ItemIndexes.STRING_TABLE_NAME};
				for (String table : tables) {
					sql = "DELETE FROM " + table + " WHERE " + DBConstants.ItemIndexes.ITEM_PARAM + " IN " + createIn(paramsToDeleteFromIndex);
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql);
				}
				dbChanged = true;
			}
			// Удаление из главной таблицы айтемов
			if (itemsById.size() > 0) {
				sql = "DELETE FROM " + DBConstants.Item.TABLE + " WHERE " + DBConstants.Item.TYPE_ID + " IN " + createIn(itemsById.keySet());
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
				dbChanged = true;
			}
		} finally {
			stmt.close();
		}
		//clearSql = "SET FOREIGN_KEY_CHECKS=0";
		//clearSql = "SET FOREIGN_KEY_CHECKS=1";
	}
	
	private void loadIds() throws SQLException {
		Statement stmt = getTransactionContext().getConnection().createStatement();

		String selectAssocIds = "SELECT * FROM " + DBConstants.AssocIds.TABLE;
		ServerLogger.debug(selectAssocIds);
		ResultSet rs = stmt.executeQuery(selectAssocIds);
		while (rs.next()) {
			byte assocId = rs.getByte(DBConstants.AssocIds.ASSOC_ID);
			String assocName = rs.getString(DBConstants.AssocIds.ASSOC_NAME);
			assocIds.put(assocName, new HashId(assocName.hashCode(), assocId));
			assocsById.put(assocId, assocName);
			if (assocId > maxAssocId)
				maxAssocId = assocId;
		}
		rs.close();

		String selectItemIds = "SELECT * FROM " + DBConstants.ItemIds.TABLE;
		ServerLogger.debug(selectItemIds);
		rs = stmt.executeQuery(selectItemIds);
		while (rs.next()) {
			int itemId = rs.getInt(DBConstants.ItemIds.ITEM_ID);
			String itemName = rs.getString(DBConstants.ItemIds.ITEM_NAME);
			itemIds.put(itemName, new HashId(itemName.hashCode(), itemId));
			itemsById.put(itemId, itemName);
			paramIds.put(itemId, new HashMap<String, HashId>());
			if (itemId > maxItemId)
				maxItemId = itemId;
		}
		rs.close();
		// удаление ненужных но почему-то присутствующих параметров
		String deleteParamIds 
			= "DELETE FROM " + DBConstants.ParamIds.TABLE + " WHERE " + DBConstants.ParamIds.ITEM_ID 
			+ " NOT IN (SELECT " + DBConstants.ItemIds.ITEM_ID + " FROM " + DBConstants.ItemIds.TABLE + ")";
		ServerLogger.debug(deleteParamIds);
		stmt.executeUpdate(deleteParamIds);

		String selectParamIds = "SELECT * FROM " + DBConstants.ParamIds.TABLE;
		ServerLogger.debug(selectParamIds);
		rs = stmt.executeQuery(selectParamIds);
		while (rs.next()) {
			int itemId = rs.getInt(DBConstants.ParamIds.ITEM_ID);
			int paramId = rs.getInt(DBConstants.ParamIds.PARAM_ID);
			String paramName = rs.getString(DBConstants.ParamIds.PARAM_NAME);
			paramIds.get(itemId).put(paramName, new HashId(paramName.hashCode(), paramId));
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
	 * Находит все файлы pages.xml
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
				for (File file : filesList) {
					if (file.isFile())
						files.add(file);
					else if (file.isDirectory())
						findModelFiles(file, files);
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

	public static void main(String[] args) throws JClassAlreadyExistsException, IOException {
//		String str = "<eeee cool='rrrr' mega=\"dddd\" ultra='ffff'/><vvvv cool='dfdf' mega=\"ccccccc\"/>";
//		str = str.replaceAll(" mega=\"[^\"]*\"", "");
//		System.out.println(str.replaceAll(" mega=\"[^\"]*\"", ""));
//        URL location = DataModelCreateCommandUnit.class.getProtectionDomain().getCodeSource().getLocation();
//        System.out.println(location.getFile());

	}
}
