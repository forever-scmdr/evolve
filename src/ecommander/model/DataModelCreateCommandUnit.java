package ecommander.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import ecommander.fwk.CodeGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.Attributes;

import com.sun.codemodel.JClassAlreadyExistsException;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.fwk.EcommanderException;
import ecommander.fwk.ValidationException;
import ecommander.controllers.AppContext;
import ecommander.pages.ValidationResults;
import ecommander.persistence.TransactionException;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.DataTypeMapper;

import static ecommander.output.RootItemMDWriter.GROUP_ATTRIBUTE;

/**
 * Разбор файла
 * @author EEEE
 * 
 */
public class DataModelCreateCommandUnit extends DBPersistenceCommandUnit implements DataModelXmlElementNames {

	private static class HashId {
		private final int hash;
		private final int id;
		public HashId(int hash, int id) {
			this.hash = hash;
			this.id = id;
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
	private HashSet<Integer> itemsToDelete = new HashSet<>();
	private HashSet<Integer> paramsToDelete = new HashSet<>();
	private HashSet<Integer> assocsToDelete = new HashSet<>();
	private HashSet<Integer> itemsToRefresh = new HashSet<>(); // айтемы, у которыз поменялись параметры и которые нао пересохранить
	private boolean dbChanged = false; // были ли изменения в БД
	
	public void execute() throws Exception {
		// Очистить реестр айтемов
		ItemTypeRegistry.clearRegistry();
		
		// Загрузить ID всех параметров и айтемов
		loadIds();

		// Создать парсер
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		CreateHandler handler = new CreateHandler();

		// Прасить основную модель model.xml (получить базовые определения айтемов и параметров)
		ArrayList<File> modelFiles = findModelFiles(new File(AppContext.getMainModelPath()), null);
		for (File modelFile : modelFiles) {
			parser.parse(modelFile, handler);			
		}
		
		// Парсить модели пользовательских айтемов
		if (Files.exists(Paths.get(AppContext.getUserModelPath())))
			parser.parse(AppContext.getUserModelPath(), handler);
		
		// Выбросить эксэпшен, если он случился
		if (handler.exception != null) {
			ValidationResults results = new ValidationResults();
			results.setException(handler.exception);
			throw new ValidationException("primary validation error", results);
		}
		
		// Создать иерархию айтемов
		ItemTypeRegistry.createHierarchy(extensionParentChildPairs, false);
		
		// Распределить параметры и сабайтемы по айтемам
		HashSet<String> processed = new HashSet<>(20);
		for (String itemName : ItemTypeRegistry.getItemNames()) {
			addParametersAndSubitems(ItemTypeRegistry.getItemType(itemName), processed);
		}
//			TypeHierarchy root = ItemTypeRegistry.getHierarchies(ItemTypeRegistry.getItemNames());
//			addParametersAndSubitems(root);
		
		// Удалить все ненужные записи в таблицах ID и в таблицах индексов по параметрам (делается автоматически через foreign key)
		mergeModel();
		
		// Удалить атрибуты name-old и type-old
		if (containsOldTags) {
			removeOldTags(AppContext.getMainModelPath());
			if (Files.exists(Paths.get(AppContext.getUserModelPath())))
				removeOldTags(AppContext.getUserModelPath());
		}
		
		// Создать java класс с текстовыми константами для всех айтемов
		createJavaConstants();
		
		// Если было исключение - выбросить его
		if(handler.exception != null)
			throw handler.exception;
	}

	private void parseFile(File file) throws IOException {
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
		readRoot(root);
	}

	/**
	 * Читает корневой айтем
	 * Сохраняет атрибуты коренвого атйема в столбцы таблицы обычного атйема:
	 * KEY_PARAMETER - group
	 * CAPTION - persistence
	 * @param attributes
	 * @return - название группы пользователей этого корня
	 * @throws Exception
	 */
	protected String readRoot(Attributes attributes) throws Exception {
		Statement stmt = null;
		try {
			String rootGroup = attributes.getValue(GROUP_ATTRIBUTE);
			if (rootGroup == null) rootGroup = User.USER_DEFAULT_GROUP;
			String rootName = RootItemType.createRootName(rootGroup);
			// Загрузить ID корней из БД
			stmt = getTransactionContext().getConnection().createStatement();
			String sql 
					= "SELECT " + DBConstants.Item.ID + " FROM " + DBConstants.Item.TABLE 
					+ " WHERE " + DBConstants.Item.REF_ID + "=0 AND " + DBConstants.Item.KEY + " = '" + rootName + "'";			
			ServerLogger.debug(sql);
			ResultSet rs = stmt.executeQuery(sql);
			long rootId = -1;
			if (rs.next())
				rootId = rs.getLong(1);
			// Сохранить рут в таблице конкретных айтемов, в случае если такого рута нет
			if (rootId == -1) {
				sql
					= "INSERT INTO "
					+ DBConstants.Item.TABLE
					+ " ("
					+ DBConstants.Item.INDEX_WEIGHT + ", "
					+ DBConstants.Item.DIRECT_PARENT_ID + ", "
					+ DBConstants.Item.REF_ID + ", "
					+ DBConstants.Item.OWNER_GROUP_ID + ", "
					+ DBConstants.Item.OWNER_USER_ID + ", "
					+ DBConstants.Item.TYPE_ID + ", "
					+ DBConstants.Item.PRED_ID_PATH + ", "
					+ DBConstants.Item.KEY 
					+ ") SELECT 0, " + ROOT_PARENT_ID + ", 0, 0, 0, 0, '', '" + rootName + "' "
					+ " FROM DUAL WHERE NOT EXISTS (SELECT * FROM " + DBConstants.Item.TABLE 
					+ " WHERE " + DBConstants.Item.REF_ID + "=0 AND " + DBConstants.Item.KEY + "='" + rootName + "')";
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
				rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
				rs.next();
				rootId = rs.getLong(1);
				rs.close();
				// Сохранить рут в таблице предков айтемов
				sql 
					= "INSERT INTO " + DBConstants.ItemParent.TABLE + " ("
					+ DBConstants.ItemParent.ITEM_ID + ", "
					+ DBConstants.ItemParent.REF_ID + ", "
					+ DBConstants.ItemParent.PARENT_ID + ", "
					+ DBConstants.ItemParent.ITEM_TYPE + ", "
					+ DBConstants.ItemParent.PARENT_LEVEL 
					+ ") VALUES (" 
					+ rootId + ", " + rootId + ", " + rootId + ", " + ItemType.SERVICE_ITEM_ID + ", 0)";
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
				dbChanged = true;
			}
			// Добавить рут в реестр айтемов
			ItemTypeRegistry.addRootDescription(new RootItemType(rootGroup, rootId));
			return rootGroup;
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	private String readAssoc(Element assoc) {
		return null;
	}
	/**
	 * Читает айтем
	 * @param itemNode
	 * @param conn
	 * @throws Exception
	 */
	private String readItem(Element item) throws Exception {
		String name = Strings.createXmlElementName(item.attr(NAME));
		int newHash = name.hashCode();
		int savedHash = NumberUtils.toInt(item.attr(AG_HASH));
		int savedId = NumberUtils.toInt(item.attr(AG_ID));
		String key = item.attr(KEY);
		String caption = item.attr(CAPTION);
		String description = item.attr(DESCRIPTION);
		String exts = item.attr(SUPER);
		boolean virtual = Boolean.parseBoolean(item.attr(VIRTUAL));
		boolean userDefined = Boolean.parseBoolean(item.attr(USER_DEF));
		boolean isInline = Boolean.parseBoolean(item.attr(INLINE));
		boolean isExt = Boolean.parseBoolean(item.attr(EXTENDABLE));
		boolean isKeyUnique = Boolean.parseBoolean(item.attr(KEY_UNIQUE));
		Integer itemId = null;
		// Если надо обновить название айтема
		if (nameUpdate) {
			itemId = itemIds.get(nameOld);
			if (itemId == null) {
				ValidationResults results = new ValidationResults();
				results.addError("Item '" + name + "'", "there was no item named '" + nameOld + "'");
				throw new ValidationException("Item update error", results);
			}
			// Обновить название айтема в таблице ID айтемов
			Statement stmt = getTransactionContext().getConnection().createStatement();
			try {
				String sql 
					= "UPDATE " + DBConstants.ItemIds.TABLE 
					+ " SET " + DBConstants.ItemIds.ITEM_NAME + "='" + name 
					+ "' WHERE " + DBConstants.ItemIds.ITEM_ID + "=" + itemId;
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
				dbChanged = true;
			} finally {
				stmt.close();
			}
			// Заменить название айтема в списке ID айтемов
			itemIds.remove(nameOld);
			itemIds.put(name, itemId);
			containsOldTags = true;
		} else {
			itemId = itemIds.get(name);
		}
		// Если появился новый айтем, которого не было раньше - получить ID для него
		if (itemId == null) {
			Statement stmt = getTransactionContext().getConnection().createStatement();
			try {
				String sql = "INSERT " + DBConstants.ItemIds.TABLE + " (" + DBConstants.ItemIds.ITEM_NAME + ") VALUES ('" + name + "')";
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
				ResultSet keys = stmt.getGeneratedKeys();
				keys.next();
				itemId = keys.getInt(1);
				itemIds.put(name, itemId);
				paramIds.put(itemId, new HashMap<String, Integer>());
				dbChanged = true;
			} finally {
				stmt.close();
			}
		}
		// Временно сохранить сведения о иерархии наследования
		if (!StringUtils.isBlank(exts)) {
			String[] parents = StringUtils.split(exts, ItemType.COMMON_DELIMITER + ItemType.ITEM_SELF);
			for (String parent : parents) {
				String[] pair = {parent, name};
				extensionParentChildPairs.add(pair);
			}
		}
		// Добавить описание айтема в реестр
		ItemType item = new ItemType(name, itemId, caption, description, key, exts, virtual, userDefined, isInline, isExt, isKeyUnique);
		ItemTypeRegistry.addItemDescription(item);
		// Добавить обработчик сохранения, если он есть
		if (!StringUtils.isBlank(extraHandler)) {
			try {
				ItemEventCommandFactory factory = (ItemEventCommandFactory) Class.forName(extraHandler).getConstructor().newInstance();
				item.addExtraHandler(factory);
			} catch (Exception e) {
				throw new EcommanderException(e);
			}
		}
		// Пометить этот айтем для неудаления
		itemsToDelete.remove(itemId);
		return name;
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
	protected void readParameter(Attributes attributes, String itemName) throws Exception {
		String name = attributes.getValue(NAME_ATTRIBUTE);
		String nameOld = attributes.getValue(NAME_OLD_ATTRIBUTE);
		boolean nameUpdate = !StringUtils.isBlank(nameOld);
		String caption = attributes.getValue(CAPTION_ATTRIBUTE);
		String description = attributes.getValue(DESCRIPTION_ATTRIBUTE);
		String domainName = attributes.getValue(DOMAIN_ATTRIBUTE);
		String quantifierStr = attributes.getValue(QUANTIFIER_ATTRIBUTE);
		Quantifier quantifier = Quantifier.single;
		if (!StringUtils.isEmpty(quantifierStr)) 
			quantifier = Quantifier.valueOf(Quantifier.class, quantifierStr);
		String dataTypeName = attributes.getValue(TYPE_ATTRIBUTE);
		String dataTypeNameOld = attributes.getValue(TYPE_OLD_ATTRIBUTE);
		boolean typeUpdate = !StringUtils.isBlank(dataTypeNameOld);
		String format = attributes.getValue(FORMAT_ATTRIBUTE);
		boolean isHidden = Boolean.valueOf(attributes.getValue(HIDDEN_ATTRIBUTE));
		boolean isVirtual = Boolean.parseBoolean(attributes.getValue(VIRTUAL_ATTRIBUTE));
		String textIndexStr = attributes.getValue(TEXT_INDEX);
		ParameterDescription.TextIndex textIndex = ParameterDescription.TextIndex.none;
		String textIndexParameter = attributes.getValue(TEXT_INDEX_PARAMETER);
		String textIndexParser = attributes.getValue(TEXT_INDEX_PARSER);
		String textIndexBoostStr = attributes.getValue(TEXT_INDEX_BOOST);
		float textIndexBoost = -1f;
		if (!StringUtils.isBlank(textIndexStr)) {
			try {
				textIndex = ParameterDescription.TextIndex.valueOf(ParameterDescription.TextIndex.class, textIndexStr);
				if (!StringUtils.isBlank(textIndexBoostStr))
					textIndexBoost = Float.parseFloat(textIndexBoostStr);
			} catch (Exception e) {
				ValidationResults results = new ValidationResults();
				results.addError("Item '" + itemName + "'", "fulltext search parameters provided are not correct");
				throw new ValidationException("Fulltext search setup incorrect", results);
			}
		}
		
		int itemId = itemIds.get(itemName);
		Integer paramId = null;
		// Если надо обновить название параметра
		if (nameUpdate) {
			paramId = paramIds.get(itemId).get(nameOld);
			if (paramId == null) {
				ValidationResults results = new ValidationResults();
				results.addError("Item '" + itemName + "'", "there was no parameter named '" + nameOld + "' in this item");
				throw new ValidationException("Item update error", results);
			}
			// Обновить название параметра в таблице ID параметров
			Statement stmt = getTransactionContext().getConnection().createStatement();
			try {
				String sql 
					= "UPDATE " + DBConstants.ParamIds.TABLE 
					+ " SET " + DBConstants.ParamIds.PARAM_NAME + "='" + name 
					+ "' WHERE " + DBConstants.ParamIds.PARAM_ID + "=" + paramId;
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
				dbChanged = true;
			} finally {
				stmt.close();
			}
			// Заменить название параметра в списке ID параметров
			paramIds.get(itemId).remove(nameOld);
			paramIds.get(itemId).put(name, paramId);
			containsOldTags = true;
		} else {
			paramId = paramIds.get(itemId).get(name);
		}
		// Если появился новый параметр, которого не было раньше - получить ID для него
		if (paramId == null) {
			Statement stmt = getTransactionContext().getConnection().createStatement();
			try {
				String sql 
					= "INSERT " + DBConstants.ParamIds.TABLE + " (" 
					+ DBConstants.ParamIds.ITEM_ID + ", " + DBConstants.ParamIds.PARAM_NAME 
					+ ") VALUES (" + itemId + ", '" + name + "')";
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
				ResultSet keys = stmt.getGeneratedKeys();
				keys.next();
				paramId = keys.getInt(1);
				paramIds.get(itemId).put(name, paramId);
				dbChanged = true;
			} finally {
				stmt.close();
			}
		} else {
			// Пометить этот айтем для неудаления
			if (typeUpdate) {
				containsOldTags = true;
				paramsToDeleteFromIndex.add(paramId);
			}
			paramsToDelete.remove(paramId);
		}
		ParameterDescription param = new ParameterDescription(name, paramId, dataTypeName, quantifier, itemId, domainName, caption,
				description, format, isVirtual, isHidden);
		if (textIndex != ParameterDescription.TextIndex.none)
			param.setFulltextSearch(textIndex, textIndexParameter, textIndexBoost, textIndexParser);
		// Добавление в реестр невозможно, т.к. неизвестны еще все потомки данного айтема по иерархии,
		// поэтому сначала добавление в специальную структуру
		ArrayList<ParameterDescription> itemParams = params.get(itemId);
		if (itemParams == null) {
			itemParams = new ArrayList<ParameterDescription>();
			params.put(itemId, itemParams);
		}
		itemParams.add(param);
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
			if (ext.equals(ItemType.ITEM_SELF)) {
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
				item.addAllSubitems(predecessor);
				// Установить extra-handler
				if (predecessor.hasExtraHandlers()) {
					for (ItemEventCommandFactory factory : predecessor.getExtraHandlers()) {
						item.addExtraHandler(factory);
					}
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
	protected void readSubitem(Attributes attributes, String itemName, String rootGroup) throws Exception {
		// По очереди все сабайтемы
		String subitemName = attributes.getValue(NAME_ATTRIBUTE);
		if (subitemName == null) subitemName = Strings.EMPTY;
		String quantifierStr = attributes.getValue(QUANTIFIER_ATTRIBUTE);
		if (quantifierStr == null) quantifierStr = Strings.EMPTY;
		boolean isSingle = !quantifierStr.equalsIgnoreCase(MULTIPLE_VALUE);
		boolean isVitrual = Boolean.valueOf(attributes.getValue(VIRTUAL_ATTRIBUTE));
		boolean isPersonal = PERSONAL_VALUE.equalsIgnoreCase(attributes.getValue(OWNER_ATTRIBUTE));
		String ownerGroup = attributes.getValue(OWNER_GROUP_ATTRIBUTE);
		// Не учитывать пренадлежность группе, которой атйем и так принадлежит
		if (ownerGroup != null && ownerGroup.equalsIgnoreCase(rootGroup))
			ownerGroup = null;
		ItemTypeContainer desc = ItemTypeRegistry.getItemTypeContainer(itemName);
		desc.addOwnSubitem(subitemName, isSingle, isVitrual, isPersonal, ownerGroup);
	}
	/**
	 * Очищает модель данных от удаленных айтемов и параметров.
	 * Изменяет старые параметры в соответствии с новой моделью (при перемещении параметров из одного атйема в другой)
	 * @throws SQLException 
	 */
	protected void mergeModel() throws TransactionException, SQLException {
		// Из удаляемых айтемов и параметров удаляются служебные
		itemsToDelete.remove(ParameterDescription.USER.getOwnerItemId());
		paramsToDelete.remove(ParameterDescription.USER.getId());
		paramsToDelete.remove(ParameterDescription.GROUP.getId());
		
		Statement stmt = getTransactionContext().getConnection().createStatement();
		try {
			// ********************** Очистить таблицы ******************************
			String sql;
			// Удаление из таблицы ID айтемов
			if (itemsToDelete.size() > 0) {
				sql = "DELETE FROM " + DBConstants.ItemIds.TABLE + " WHERE " + DBConstants.ItemIds.ITEM_ID + " IN " + createIn(itemsToDelete);
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
				dbChanged = true;
			}
			// Удаление параметров, если нужно удалять, 
			// изменение ID параметров, если параметр переместился в родительский класс
			if (paramsToDelete.size() > 0) {
				for (ParamToDelete pd : paramsToDelete.values()) {
					ParameterDescription newParamDesc = null; // новый ID параметра, на который надо поменять старый ID в таблицах индексов
					Set<String> predNames = ItemTypeRegistry.getItemPredecessors(pd.itemName);
					for (String predName : predNames) {
						newParamDesc = ItemTypeRegistry.getItemType(predName).getParameter(pd.paramName);
						if (newParamDesc != null)
							break;
					}
					// Поменять ID параметра в таблицах-индексах
					if (newParamDesc != null) {
						String table = DataTypeMapper.getTableName(newParamDesc.getType());
						sql = "UPDATE " + table + " SET " + DBConstants.ItemIndexes.ITEM_PARAM + "=" + newParamDesc.getId() + " WHERE "
								+ DBConstants.ItemIndexes.ITEM_PARAM + "=" + pd.paramId;
						ServerLogger.debug(sql);
						stmt.executeUpdate(sql);
					} else {
						paramsToDeleteFromIndex.add(pd.paramId);
					}
					// Удалить параметр из таблицы ID параметров
					sql = "DELETE FROM " + DBConstants.ParamIds.TABLE + " WHERE " + DBConstants.ParamIds.PARAM_ID + "=" + pd.paramId;
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
			if (itemsToDelete.size() > 0) {
				sql = "DELETE FROM " + DBConstants.Item.TABLE + " WHERE " + DBConstants.Item.TYPE_ID + " IN " + createIn(itemsToDelete);
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
			int assocId = rs.getInt(DBConstants.AssocIds.ASSOC_ID);
			String assocName = rs.getString(DBConstants.AssocIds.ASSOC_NAME);
			assocIds.put(assocName, new HashId(assocName.hashCode(), assocId));
			assocsToDelete.add(assocId);
		}
		rs.close();

		String selectItemIds = "SELECT * FROM " + DBConstants.ItemIds.TABLE;
		ServerLogger.debug(selectItemIds);
		rs = stmt.executeQuery(selectItemIds);
		HashMap<Integer, String> itemNames = new HashMap<>();
		while (rs.next()) {
			int itemId = rs.getInt(DBConstants.ItemIds.ITEM_ID);
			String itemName = rs.getString(DBConstants.ItemIds.ITEM_NAME);
			itemNames.put(itemId, itemName);
			itemIds.put(itemName, new HashId(itemName.hashCode(), itemId));
			itemsToDelete.add(itemId);
			paramIds.put(itemId, new HashMap<String, HashId>());
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
			paramsToDelete.add(paramId);
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
	/**
	 * Удалить тэги -old из файла
	 * @param fileName
	 * @throws IOException 
	 */
	private void removeOldTags(String fileName) throws IOException {
		Charset utf8 = Charset.forName("UTF-8");
		byte[] bytes = Files.readAllBytes(Paths.get(fileName));
		String xml = new String(bytes, utf8);
		xml = xml.replaceAll(" " + NAME_OLD_ATTRIBUTE + "=\"[^\"]*\"", "");
		xml = xml.replaceAll(" " + NAME_OLD_ATTRIBUTE + "='[^']*\"", "");
		xml = xml.replaceAll(" " + TYPE_OLD_ATTRIBUTE + "=\"[^\"]*\"", "");
		xml = xml.replaceAll(" " + TYPE_OLD_ATTRIBUTE + "='[^']*\"", "");
		Files.write(Paths.get(fileName), xml.getBytes(StandardCharsets.UTF_8));
	}
	
	private void createJavaConstants() {
		if (!dbChanged)
			return;
		CodeGenerator.createJavaConstants();
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
	
	public static void main(String[] args) throws JClassAlreadyExistsException, IOException {
//		String str = "<eeee cool='rrrr' mega=\"dddd\" ultra='ffff'/><vvvv cool='dfdf' mega=\"ccccccc\"/>";
//		str = str.replaceAll(" mega=\"[^\"]*\"", "");
//		System.out.println(str.replaceAll(" mega=\"[^\"]*\"", ""));
//        URL location = DataModelCreateCommandUnit.class.getProtectionDomain().getCodeSource().getLocation();
//        System.out.println(location.getFile());

	}
}
