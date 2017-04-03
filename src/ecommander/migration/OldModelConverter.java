package ecommander.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.controllers.AppContext;
import ecommander.output.ItemTypeMDWriter;
import ecommander.output.ParameterDescriptionMDWriter;
import ecommander.output.RootItemMDWriter;
import ecommander.output.XmlDocumentBuilder;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeContainer;
import ecommander.model.ParameterDescription;
import ecommander.model.ParameterDescription.Quantifier;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.commandunits.ItemModelFilePersistenceCommandUnit;
import ecommander.persistence.mappers.DBConstants;

/**
 * Считывает описания всех айтемов и загружает ItemDescriptionRegistry
 * @author EEEE
 *
 */
public class OldModelConverter extends DBPersistenceCommandUnit {
	
	public static final long ROOT_PARENT_ID = 0;
	private String oldDb;
	private String newDb;
	
	
	private static class ItemsWriter extends ItemModelFilePersistenceCommandUnit {
		
		private ArrayList<ItemTypeContainer> items;
		private ArrayList<ItemTypeContainer> roots;
		
		private ItemsWriter(ArrayList<ItemTypeContainer> items, ArrayList<ItemTypeContainer> roots) {
			super();
			this.items = items;
			this.roots = roots;
		}
		
		@Override
		protected void executeInt() throws Exception {
			XmlDocumentBuilder xml = XmlDocumentBuilder.newDoc();
			xml.startElement(ROOT_ELEMENT);
			for (ItemTypeContainer element : items) {
				ItemType item = (ItemType) element;
				ItemTypeMDWriter writer = new ItemTypeMDWriter(item, ITEM_ELEMENT);
				for (ParameterDescription param : item.getParameterList()) {
					writer.addSubwriter(new ParameterDescriptionMDWriter(param));
				}
				writeEntity(xml, writer, item.getName());
			}
			for (ItemTypeContainer element : roots) {
				RootItemMDWriter writer = new RootItemMDWriter((RootItemType)element);
				writer.write(xml);
			}
			xml.addComment(EXPAND_MARK);
			xml.endElement();
			setFileContents(xml.toString());
			saveFile();
		}
		
	}
	
	public OldModelConverter(String oldDbName, String newDbName) {
		this.oldDb = oldDbName;
		this.newDb = newDbName;
	}
	
	public void execute() throws Exception {
		transferDB();
		
		Statement stmt = getTransactionContext().getConnection().createStatement();
		HashMap<String, HashSet<String>> hchy = loadHierarchies();
		
		stmt.execute("USE " + oldDb);
		
		// Сначала загружаются ID корневых айтемов
		HashMap<String, Long> rootIds = new HashMap<String, Long>();
		String sql 
				= "SELECT " + DBConstantsOld.Item.ID + ", " + DBConstantsOld.Item.TYPE_NAME + " FROM " + DBConstantsOld.Item.TABLE 
				+ " WHERE " + DBConstantsOld.Item.DIRECT_PARENT_ID + " = " + ROOT_PARENT_ID;			
		ServerLogger.debug(sql);
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next())
			rootIds.put(rs.getString(DBConstantsOld.Item.TYPE_NAME), rs.getLong(DBConstantsOld.Item.ID));
		
		// Потом загружаются все возможные айтемы (типы айтемов)
		sql = "SELECT * FROM " + DBConstantsOld.ItemAbstract.TABLE + " ORDER BY " + DBConstantsOld.ItemAbstract.ITEM_TYPE_ID;
		ServerLogger.debug(sql);
		rs = stmt.executeQuery(sql);
		LinkedHashMap<String, ItemTypeContainer> items = new LinkedHashMap<String, ItemTypeContainer>();
		HashMap<Integer, String> itemNames = new HashMap<Integer, String>();
		HashMap<Integer, LinkedHashMap<Integer, ParameterDescription>> paramsOrder = new HashMap<Integer, LinkedHashMap<Integer,ParameterDescription>>();
		while (rs.next()) {
			// Создание нового айтема происходит только тут, и нигде больше
			String name = rs.getString(DBConstantsOld.ItemAbstract.ITEM_TYPE_NAME);
			if (!name.startsWith(DBConstantsOld.ROOT_PREFIX))
				name = createName(name);
			int id = rs.getInt(DBConstantsOld.ItemAbstract.ITEM_TYPE_ID);
			String caption = rs.getString(DBConstantsOld.ItemAbstract.CAPTION);
			String description = rs.getString(DBConstantsOld.ItemAbstract.DESCRIPTION);
			String key = rs.getString(DBConstantsOld.ItemAbstract.KEY_PARAMETER);
			key = StringUtils.replaceChars(key, ',', ' ');
			byte flags = rs.getByte(DBConstantsOld.ItemAbstract.FLAGS);
			String order = rs.getString(DBConstantsOld.ItemAbstract.PARAMETER_ORDER);
			if (!StringUtils.isBlank(order)) {
				String[] paramIds = StringUtils.split(order, ',');
				for (String paramId : paramIds) {
					LinkedHashMap<Integer, ParameterDescription> po = paramsOrder.get(id);
					if (po == null) {
						po = new LinkedHashMap<Integer, ParameterDescription>();
						paramsOrder.put(id, po);
					}
					try {
						po.put(Integer.parseInt(paramId), null);
					} catch (Exception e) {/* просто пропустить */}
				}
			}
			boolean virtual = flags % 2 == 1;
			boolean userDefined = (flags >> 1) % 2 == 1;
			HashSet<String> parents = hchy.get(name);
			String extendsStr = StringUtils.join(parents, ItemType.COMMON_DELIMITER);
			// добавление айтема в модель
			if (name.startsWith(DBConstantsOld.ROOT_PREFIX))
				items.put(name, new RootItemType(key, rootIds.get(name)));
			else
				items.put(name, new ItemType(name, id, caption, description, key, extendsStr, virtual, userDefined, false, true, false));
			itemNames.put(id, name);
		}
		rs.close();

		// Теперь устанавливаются сабайтемы
		sql = new String();
		sql += "SELECT * FROM " + DBConstantsOld.ItemParentAbstract.TABLE;
		ServerLogger.debug(sql);
		rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String itemTypeName = rs.getString(DBConstantsOld.ItemParentAbstract.ITEM_TYPE_NAME);
			String parentTypeName = rs.getString(DBConstantsOld.ItemParentAbstract.PARENT_TYPE_NAME);
			byte flags = rs.getByte(DBConstantsOld.ItemParentAbstract.SUBITEM_FLAGS);
			boolean isSingle = (flags & DBConstantsOld.QUANTIFIER_MASK) == DBConstantsOld.QUANTIFIER_SINGLE;
			boolean isVirtual = (flags & DBConstantsOld.VIRTUALITY_MASK) == DBConstantsOld.VIRTUALITY_VIRTUAL;
			ItemTypeContainer parent = items.get(parentTypeName);
			// Добавление сабайтема в айтем
			parent.addOwnChild(itemTypeName, isSingle, isVirtual, false, null);
		}
		rs.close();

		// теперь добавляются парамтеры
		sql = new String();
		sql += "SELECT * FROM " + DBConstantsOld.ItemParameterAbstract.TABLE;
		ServerLogger.debug(sql);
		rs = stmt.executeQuery(sql);
		while (rs.next()) {
			// Создание нового парамтера происходит только тут, и нигде больше
			String caption = rs.getString(DBConstantsOld.ItemParameterAbstract.PARAMETER_CAPTION);
			String description = rs.getString(DBConstantsOld.ItemParameterAbstract.PARAMETER_DESCRIPTION);
			String domainName = rs.getString(DBConstantsOld.ItemParameterAbstract.PARAMETER_DOMAIN);
			String name = createName(rs.getString(DBConstantsOld.ItemParameterAbstract.PARAMETER_NAME));
			int id = rs.getInt(DBConstantsOld.ItemParameterAbstract.PARAMETER_ID);
			Quantifier quantifier = rs.getByte(DBConstantsOld.ItemParameterAbstract.PARAMETER_QUANTIFIER) 
					== DBConstantsOld.QUANTIFIER_SINGLE ? Quantifier.single : Quantifier.multiple;
			String dataTypeName = rs.getString(DBConstantsOld.ItemParameterAbstract.PARAMETER_TYPE);
			String format = rs.getString(DBConstantsOld.ItemParameterAbstract.PARAMETER_FORMAT);
			int itemTypeId = rs.getInt(DBConstantsOld.ItemParameterAbstract.ITEM_TYPE_ID);
			ParameterDescription param = new ParameterDescription(name, id, dataTypeName, quantifier, itemTypeId, domainName, caption,
					description, format, false, false);
			// Для сохранения порядка следования параметров сначала они добавляются в спец структуру, только потом в айтем
			LinkedHashMap<Integer, ParameterDescription> po = paramsOrder.get(itemTypeId);
			if (po == null) {
				po = new LinkedHashMap<Integer, ParameterDescription>();
				paramsOrder.put(itemTypeId, po);
			}
			po.put(id, param);
		}
		rs.close();
		// Непосредственное добавление параметров в айтем
		for (Integer itemTypeId : paramsOrder.keySet()) {
			LinkedHashMap<Integer, ParameterDescription> po = paramsOrder.get(itemTypeId);
			ItemType item = (ItemType) items.get(itemNames.get(itemTypeId));
			for (ParameterDescription param : po.values()) {
				if (param != null)
					item.putParameter(param);
			}
		}
		
		
		// Последняя операция - запись всех айтемов в соответствующие файлы (главный и пользовательский)
		ArrayList<ItemTypeContainer> mainItems = new ArrayList<ItemTypeContainer>();
		ArrayList<ItemTypeContainer> mainRoots = new ArrayList<ItemTypeContainer>();
		ArrayList<ItemTypeContainer> userItems = new ArrayList<ItemTypeContainer>();
		ArrayList<ItemTypeContainer> userRoots = new ArrayList<ItemTypeContainer>();
		for (ItemTypeContainer element : items.values()) {
			if (element instanceof ItemType) {
				if (((ItemType)element).isUserDefined())
					userItems.add(element);
				else
					mainItems.add(element);
			} else {
				mainRoots.add(element);
			}
		}
		ItemsWriter mainWriter = new ItemsWriter(mainItems, mainRoots);
		ItemsWriter userWriter = new ItemsWriter(userItems, userRoots);
		mainWriter.setFileName(AppContext.getMainModelPath());
		userWriter.setFileName(AppContext.getUserModelPath());
		executeCommand(mainWriter);
		executeCommand(userWriter);
		
		stmt.execute("USE " + newDb);
		stmt.close();
	}
	/**
	 * Для каждого айтема получить список всех его предков
	 * @return
	 * @throws SQLException
	 */
	private HashMap<String, HashSet<String>> loadHierarchies() throws SQLException {
		Statement stmt = getTransactionContext().getConnection().createStatement();
		stmt.execute("USE " + oldDb);
		String sql = "SELECT * FROM " + DBConstantsOld.ItemExtensions.TABLE + " ORDER BY " + DBConstantsOld.ItemExtensions.ITEM_TYPE_NAME
				+ ", " + DBConstantsOld.ItemExtensions.EXTENSION_TYPE_NAME;
		ServerLogger.debug(sql);
		ResultSet rs = stmt.executeQuery(sql);
		HashMap<String, HashSet<String>> hierarchy = new HashMap<String, HashSet<String>>();
		while (rs.next()) {
			String parent = createName(rs.getString(DBConstantsOld.ItemExtensions.ITEM_TYPE_NAME));
			String child = createName(rs.getString(DBConstantsOld.ItemExtensions.EXTENSION_TYPE_NAME));
			HashSet<String> parents = hierarchy.get(child);
			if (parents == null) {
				parents = new HashSet<String>();
				hierarchy.put(child, parents);
			}
			parents.add(parent);
		}
		rs.close();
		stmt.close();
		return hierarchy;
	}

	private final String USER_GROUP_COPY = 
		"INSERT INTO <<db_dest>>.UserGroup (UG_NAME) SELECT UG_NAME FROM <<db_src>>.UserGroup";

	private final String USER_COPY = 
		"INSERT INTO <<db_dest>>.Users (U_ID, U_GROUP, U_LOGIN, U_PASSWORD, U_DESCRIPTION) " +
		"SELECT U_ID, U_GROUP, U_LOGIN, U_PASSWORD, U_DESCRIPTION FROM <<db_src>>.Users";

	private final String SELECT_ITEM_TYPES = "SELECT IA_TYPE_ID, IA_TYPE_NAME FROM <<db_src>>.ItemAbstract";
	
	private final String INSERT_ITEM_TYPES = "INSERT INTO <<db_dest>>.ItemIds (IID_ID, IID_NAME) VALUES (?, ?)";

	private final String SELECT_PARAMS = "SELECT IRA_PARAM_ID, IRA_TYPE_ID, IRA_PARAM_NAME FROM <<db_src>>.ItemParameterAbstract";
	
	private final String INSERT_PARAMS = "INSERT INTO <<db_dest>>.ParamIds (PID_PARAM_ID, PID_ITEM_ID, PID_PARAM_NAME) VALUES (?, ?, ?)";

	private final String ITEMS_COPY = 
		"INSERT INTO <<db_dest>>.Item " +
		"(I_ID, I_TYPE_ID, I_KEY, I_T_KEY, I_PARENT_ID, I_PRED_ID_PATH, I_REF_ID, I_WEIGHT, I_OWNER_GROUP_ID, I_OWNER_USER_ID, I_PARAMS) " +
		"SELECT I_ID, I_TYPE_ID, I_KEY, I_T_KEY, I_PARENT_ID, I_PRED_ID_PATH, I_REF_ID, I_WEIGHT, I_OWNER_GROUP_ID, I_OWNER_USER_ID, I_PARAMS " +
		"FROM <<db_src>>.Item";
	
	private final String ROOT_NAMES_UPDATE = 
		"UPDATE <<db_dest>>.Item AS D, <<db_src>>.Item AS S SET D.I_KEY = REPLACE(S.I_TYPE_NAME, '_PERSISTENT', '') WHERE S.I_TYPE_ID = 0 AND S.I_ID = D.I_ID";
	
	private final String DELETE_ODD = 
		"DELETE FROM <<db_dest>>.Item WHERE I_REF_ID NOT IN (SELECT I_ID FROM <<db_src>>.Item) AND I_TYPE_ID != 0";
	
	private final String UPDATE_OWNERS = 
		"UPDATE <<db_dest>>.Item SET I_OWNER_USER_ID = 0 WHERE I_OWNER_USER_ID = 1";
	
	private final String PARENTS_COPY_SAME = 
		"INSERT INTO <<db_dest>>.ItemParent (IP_ITEM_ID, IP_PARENT_ID, IP_LEVEL, IP_REF_ID, IP_TYPE) " +
		"SELECT IP_ITEM_ID, IP_PARENT_ID, IP_LEVEL, IP_REF_ID, I_TYPE_ID FROM <<db_src>>.ItemParent, <<db_src>>.Item " +
		"WHERE I_ID = IP_ITEM_ID";
			
	private final String PARENTS_INSERT_SELF = 
		"INSERT INTO <<db_dest>>.ItemParent (IP_ITEM_ID, IP_PARENT_ID, IP_LEVEL, IP_REF_ID, IP_TYPE) " +
		"SELECT I_ID, I_ID, 0, I_REF_ID, I_TYPE_ID FROM <<db_dest>>.Item WHERE I_PARENT_ID != 0";

	private final String DOUBLE_INDEX_COPY = 
		"REPLACE INTO <<db_dest>>.DoubleIndex (II_REF_ID, II_PARAM, II_TYPE, II_VAL, II_PARENT) " +
		"SELECT II_REF_ID, II_PARAM, II_TYPE, II_VAL, I_PARENT_ID FROM <<db_src>>.DoubleIndex, <<db_src>>.Item " +
		"WHERE II_REF_ID = I_ID";
	
	private final String INT_INDEX_COPY = 
		"REPLACE INTO <<db_dest>>.IntIndex (II_REF_ID, II_PARAM, II_TYPE, II_VAL, II_PARENT) " +
		"SELECT II_REF_ID, II_PARAM, II_TYPE, II_VAL, I_PARENT_ID FROM <<db_src>>.IntIndex, <<db_src>>.Item " +
		"WHERE II_REF_ID = I_ID";

	private final String STRING_INDEX_COPY = 
		"REPLACE INTO <<db_dest>>.StringIndex (II_REF_ID, II_PARAM, II_TYPE, II_VAL, II_PARENT) " +
		"SELECT II_REF_ID, II_PARAM, II_TYPE, II_VAL, I_PARENT_ID FROM <<db_src>>.StringIndex, <<db_src>>.Item " +
		"WHERE II_REF_ID = I_ID";
	
	private void transferDB() throws Exception {
		Connection conn = getTransactionContext().getConnection();
		try {
			// удаление данных
			truncateData(conn);
			
			// Группы пользователей
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(applyTableNames(USER_GROUP_COPY));
			ServerLogger.debug("Groups transfered");
			
			// Пользователи
			stmt.executeUpdate(applyTableNames(USER_COPY));
			ServerLogger.debug("Users transfered");
			
			// ID типов айтемов и их новые названия (валидные имена XML элементов)
			ResultSet rs = stmt.executeQuery(applyTableNames(SELECT_ITEM_TYPES));
			LinkedHashMap<Integer, String> itemTypes = new LinkedHashMap<Integer, String>();
			while(rs.next()) {
				itemTypes.put(rs.getInt(1), rs.getString(2));
			}
			rs.close();
			PreparedStatement pstmt = conn.prepareStatement(applyTableNames(INSERT_ITEM_TYPES));
			for (Entry<Integer, String> type : itemTypes.entrySet()) {
				pstmt.setInt(1, type.getKey());
				String typeName = createName(type.getValue());
				ServerLogger.debug("type: " + typeName + "     original: " + type.getValue());
				pstmt.setString(2, typeName);
				pstmt.executeUpdate();
			}
			
			// Служебный айтем с ID = -1
			pstmt.setInt(1, -1);
			pstmt.setString(2, "_SERVICE_");
			pstmt.executeUpdate();
			
			pstmt.close();
			ServerLogger.debug("Item ids transfered");
			
			// ID параметров и их новые названия (валидные имена XML элементов)
			rs = stmt.executeQuery(applyTableNames(SELECT_PARAMS));
			ArrayList<Object[]> params = new ArrayList<Object[]>();
			while(rs.next()) {
				params.add(new Object[]{rs.getInt(1), rs.getInt(2), rs.getString(3)});
			}
			rs.close();
			pstmt = conn.prepareStatement(applyTableNames(INSERT_PARAMS));
			for (Object[] param : params) {
				pstmt.setInt(1, (Integer)param[0]);
				pstmt.setInt(2, (Integer)param[1]);
				String paramName = createName((String)param[2]);
				ServerLogger.debug("param: " + paramName + "     original: " + param[2]);
				pstmt.setString(3, paramName);
				pstmt.executeUpdate();
			}
			
			// Служебные параметры - пользователь (-1 -1) и группа (-2 -1)
			pstmt.setInt(1, -1);
			pstmt.setInt(2, -1);
			pstmt.setString(3, "user");
			pstmt.executeUpdate();
			pstmt.setInt(1, -2);
			pstmt.setInt(2, -1);
			pstmt.setString(3, "group");
			pstmt.executeUpdate();
			pstmt.close();
			ServerLogger.debug("Parameter ids transfered");
			
			// Айтемы
			stmt.executeUpdate(applyTableNames(ITEMS_COPY));
			ServerLogger.debug("Items transfered");
			
			// Изменение имен корневый айтемов
			stmt.executeUpdate(applyTableNames(ROOT_NAMES_UPDATE));
			ServerLogger.debug("Root items renamed");
			
			// Удаление случайных айтемов (которые появились непонятно откуда)
			stmt.executeUpdate(applyTableNames(DELETE_ODD));
			ServerLogger.debug("Odd items deleted");
			
			// Изменение владельцев айтемов (не персональные айтемы должны иметь владельца 0)
			stmt.executeUpdate(applyTableNames(UPDATE_OWNERS));
			ServerLogger.debug("Item owners updated");
			
			// Копирование родительских отношений
			stmt.executeUpdate(applyTableNames(PARENTS_COPY_SAME));
			ServerLogger.debug("Parents copied");
			
			// Дописывание самого себя в свои родители (айтемы)
			stmt.executeUpdate(applyTableNames(PARENTS_INSERT_SELF));
			ServerLogger.debug("Items as self parents inserted");
			
			// Копирование индексов
			stmt.executeUpdate(applyTableNames(DOUBLE_INDEX_COPY));
			ServerLogger.debug("Double index copied");
			stmt.executeUpdate(applyTableNames(INT_INDEX_COPY));
			ServerLogger.debug("Integer index copied");
			stmt.executeUpdate(applyTableNames(STRING_INDEX_COPY));
			ServerLogger.debug("String index copied");
			
			// Изменение фильтров (ID параметра меняется на название)
//			executeCommand(new FilterEntityIdToNameConverter());
			
			ServerLogger.debug("CONVERSION FINISHED");
		} finally {
			//
		}
	}
	
	private void truncateData(Connection conn) throws SQLException {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
			ServerLogger.debug("TRUNCATE " + DBConstants.UniqueItemKeys.UK_TABLE);
			stmt.executeUpdate("TRUNCATE " + DBConstants.UniqueItemKeys.UK_TABLE);
			ServerLogger.debug("TRUNCATE " + DBConstants.ItemIndexes.ASSOCIATED_TABLE_NAME);
			stmt.executeUpdate("TRUNCATE " + DBConstants.ItemIndexes.ASSOCIATED_TABLE_NAME);
			ServerLogger.debug("TRUNCATE " + DBConstants.ItemIndexes.DOUBLE_TABLE_NAME);
			stmt.executeUpdate("TRUNCATE " + DBConstants.ItemIndexes.DOUBLE_TABLE_NAME);
			ServerLogger.debug("TRUNCATE " + DBConstants.ItemIndexes.INT_TABLE_NAME);
			stmt.executeUpdate("TRUNCATE " + DBConstants.ItemIndexes.INT_TABLE_NAME);
			ServerLogger.debug("TRUNCATE " + DBConstants.ItemIndexes.STRING_TABLE_NAME);
			stmt.executeUpdate("TRUNCATE " + DBConstants.ItemIndexes.STRING_TABLE_NAME);
			ServerLogger.debug("TRUNCATE " + DBConstants.ItemParent.IP_TABLE);
			stmt.executeUpdate("TRUNCATE " + DBConstants.ItemParent.IP_TABLE);
			ServerLogger.debug("TRUNCATE " + DBConstants.Item.TABLE);
			stmt.executeUpdate("TRUNCATE " + DBConstants.Item.TABLE);
			ServerLogger.debug("TRUNCATE " + DBConstants.ParamIds.PID_TABLE);
			stmt.executeUpdate("TRUNCATE " + DBConstants.ParamIds.PID_TABLE);
			ServerLogger.debug("TRUNCATE " + DBConstants.ItemIds.IID_TABLE);
			stmt.executeUpdate("TRUNCATE " + DBConstants.ItemIds.IID_TABLE);
			ServerLogger.debug("TRUNCATE " + DBConstants.Users.TABLE);
			stmt.executeUpdate("TRUNCATE " + DBConstants.Users.TABLE);
			ServerLogger.debug("TRUNCATE " + DBConstants.UserGroup.TABLE);
			stmt.executeUpdate("TRUNCATE " + DBConstants.UserGroup.TABLE);
			stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
		} finally {
			MysqlConnector.closeStatement(stmt);
		}
	}
	
	private String createName(String base) {
		return StringUtils.left(Strings.createXmlElementName(base), 50);
	}
		
	private String applyTableNames(String sql) {
		return StringUtils.replaceEach(sql, new String[] {"<<db_dest>>", "<<db_src>>"}, new String[] {newDb, oldDb});
	}
	
}
