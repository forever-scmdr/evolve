package ecommander.admin;

import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ServerLogger;
import ecommander.model.*;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author EEEE
 *
 */
public class AdminLoader implements DBConstants.ItemTbl, DBConstants.ItemParent {

	private static AdminLoader loader;

	private AdminLoader() {

	}

	static AdminLoader getLoader() {
		if (loader == null)
			loader = new AdminLoader();
		return loader;
	}

	private TemplateQuery createAccessorQueryBase(String queryName) {
		TemplateQuery base = new TemplateQuery("Load closest subitems part");
		base.SELECT(I_ID, I_KEY, I_T_KEY, I_GROUP, I_USER, I_STATUS, I_TYPE_ID, I_PROTECTED, IP_TABLE + ".*")
				.FROM(I_TABLE).INNER_JOIN(IP_TABLE, I_ID, IP_CHILD_ID).WHERE();
	}

	private void readAccessorResultSet(ResultSet rs, Collection<ItemAccessor> result) throws SQLException {
		while (rs.next()) {
			result.add(new ItemAccessor(
					rs.getInt(I_TYPE_ID),
					rs.getLong(I_ID),
					rs.getString(I_KEY),
					rs.getByte(I_GROUP),
					rs.getInt(I_USER),
					rs.getByte(I_STATUS),
					rs.getByte(I_PROTECTED) == (byte) 1 ? true: false,
					rs.getInt(IP_WEIGHT),
					rs.getByte(IP_ASSOC_ID),
					true));
		}
	}
	/**
	 * Загружает всех сабайтемов определенного айтема (не сами айтемы, а их аксэсоры)
	 * Загружаются следующие айтемы:
	 * 		- неперсональные айтемы текущей группы (критерий - тип айтема и номер группы)
	 * 		- персональные айтемы текущей группы (критерий - тип айтема и номер юзера)
	 * 	    - загружаются потомки по всем ассоциациям
	 * @param itemDesc
	 * @param parentId
	 * @param user
	 * @return
	 * @throws Exception
	 */
	ArrayList<ItemAccessor> loadClosestSubitems(ItemTypeContainer itemDesc, long parentId, User user) throws Exception {
		ArrayList<ItemAccessor> result = new ArrayList<>();
		Byte[] allAssocs = ItemTypeRegistry.getItemOwnAssocIds(itemDesc.getName()).toArray(new Byte[0]);
		HashSet<Byte> adminGroups = new HashSet<>();
		HashSet<Byte> commonGroups = new HashSet<>();
		for (User.Group group : user.getGroups()) {
			if (group.role == User.ADMIN)
				adminGroups.add(group.id);
			else
				commonGroups.add(group.id);
		}
		TemplateQuery base = createAccessorQueryBase("Load closest subitems part");
		base.col(IP_PARENT_ID).setLong(parentId).AND()
				.col(IP_PARENT_DIRECT).setByte((byte) 1).AND()
				.col(I_STATUS, " IN(").setByteArray(new Byte[] {Item.STATUS_NORMAL, Item.STATUS_NIDDEN}).sql(")").AND()
				.col(IP_ASSOC_ID, " IN(").setByteArray(allAssocs).sql(")").AND();

		TemplateQuery adminQuery = (TemplateQuery) base.createClone();
		TemplateQuery commonQuery = (TemplateQuery) base.createClone();
		adminQuery.col(I_GROUP, " IN(").setByteArray(adminGroups.toArray(new Byte[0])).sql(")");
		commonQuery.col(I_GROUP, " IN(").setByteArray(commonGroups.toArray(new Byte[0])).sql(")").AND()
				.col(I_USER).setInt(user.getUserId());

		TemplateQuery select = new TemplateQuery("Load closest subitems union");
		select.getOrCreateSubquery("ADMIN").replace(adminQuery);
		select.UNION_ALL().getOrCreateSubquery("COMMON").replace(commonQuery);

		try (PreparedStatement pstmt = select.prepareQuery(MysqlConnector.getConnection())) {
			ResultSet rs = pstmt.executeQuery();
			readAccessorResultSet(rs, result);
		}
		return result;
	}
	/**
	 * Загружает айтем и всех его предков
	 * @param baseId
	 * @param assocId
	 * @return
	 * @throws Exception
	 */
	public ArrayList<ItemAccessor> loadWholeBranch(long baseId, byte assocId) throws Exception {
		ArrayList<ItemAccessor> result = new ArrayList<>();
		TemplateQuery query = createAccessorQueryBase("Load item branch");
		query.col(IP_CHILD_ID).setLong(baseId).AND().col(IP_ASSOC_ID).setByte(assocId)
				.col(I_STATUS, " IN(").setByteArray(new Byte[] {Item.STATUS_NORMAL, Item.STATUS_NIDDEN}).sql(")").AND()
				.ORDER_BY(IP_PARENT_DIRECT, IP_PARENT_ID);
		try (PreparedStatement pstmt = query.prepareQuery(MysqlConnector.getConnection())) {
			ResultSet rs = pstmt.executeQuery();
			readAccessorResultSet(rs, result);
		}
		return result;
	}
	/**
	 * Загружает несколько айтемов по их ID
	 * @param itemId
	 * @return
	 */
	public ArrayList<ItemAccessor> loadItemAccessors(Long... itemId) throws Exception {
		Connection conn = null;
		ArrayList<ItemAccessor> result = new ArrayList<ItemAccessor>();
		if (itemId.length == 0)
			return result;
		try {
			conn = MysqlConnector.getConnection();
			Statement stmt = conn.createStatement();
			String sql = "SELECT " + DBConstants.Item.TYPE_ID + ", " + DBConstants.Item.ID + ", " + DBConstants.Item.REF_ID 
					+ ", " + DBConstants.Item.KEY + ", " + DBConstants.Item.INDEX_WEIGHT
					+ " FROM " + DBConstants.Item.TABLE + " WHERE "
					+ DBConstants.Item.ID + " IN(" + StringUtils.join(itemId, ',') + ")";
			ServerLogger.debug(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				result.add(new ItemAccessor(rs.getInt(1), rs.getLong(2), rs.getLong(3), rs.getString(4), rs.getInt(5)));
			}
			rs.close();
			stmt.close();
			return result;
		} finally {
			if (conn != null && !conn.isClosed())
				conn.close();
		}
	}
	/**
	 * Загружает один айтем по его ID
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public ItemAccessor loadItemAccessor(long itemId) throws Exception {
		ArrayList<ItemAccessor> result = loadItemAccessors(itemId);
		if (result.size() == 0)
			return null;
		return result.get(0);
	}
	/**
	 * Возвращает все названия айтемов, которые можно добавлять к текущему
	 * @param itemName
	 * @param existingSubitems
	 * @return
	 */
	public ArrayList<String> getItemsToAdd(String itemName, ArrayList<ItemAccessor> existingSubitems, User user) {
		ArrayList<String> result = new ArrayList<String>();
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemName);
		result.addAll(itemDesc.getAllowedSubitemNames(user.getGroup()));
		for (ItemAccessor item : existingSubitems) {
			if (result.contains(item.getItemName()) && itemDesc.isChildSingle(item.getItemName()))
				result.remove(item.getItemName());
		}
		return result;
	}
	/**
	 * Возвращает все названия айтемов, которые можно добавлять в корневой айтем
	 * @param itemName
	 * @param existingSubitems
	 * @return
	 */
	public ArrayList<String> getRootItemsToAdd(User user, ArrayList<ItemAccessor> existingSubitems) {
		ArrayList<String> result = new ArrayList<String>();
		RootItemType root = ItemTypeRegistry.getGroupRoot(user.getGroup());
		for (ItemType subitemDesc : ItemTypeRegistry.getAllowedTopLevelItems(user.getGroup())) {
			result.add(subitemDesc.getName()) ;				
		}
		for (ItemAccessor item : existingSubitems) {
			if (result.contains(item.getItemName()) && root.isSubitemSingle(item.getItemName()))
				result.remove(item.getItemName());
		}
		return result;
	}
	/**
	 * Загрузить все ссылки, которые содержит текущий айтем (все айтемы, которые к нему прицеплены)
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public ArrayList<ItemAccessor> loadMountedItems(long itemId, long userId) throws Exception {
		ArrayList<ItemAccessor> result = new ArrayList<ItemAccessor>();
		Connection conn = null;
		try {
			conn = MysqlConnector.getConnection();
			Statement stmt = conn.createStatement();
			String sql 
				= "SELECT " + DBConstants.Item.TYPE_ID + ", " + DBConstants.Item.ID + ", " + DBConstants.Item.REF_ID 
				+ ", " + DBConstants.Item.KEY + ", " + DBConstants.Item.INDEX_WEIGHT
				+ " FROM " + DBConstants.Item.TABLE 
				+ " WHERE " + DBConstants.Item.DIRECT_PARENT_ID + " = " + itemId
				+ " AND " + DBConstants.Item.ID + " != " + DBConstants.Item.REF_ID
				+ " ORDER BY " + DBConstants.Item.TYPE_ID + ", " + DBConstants.Item.KEY;

			// Выполнение запроса
			ServerLogger.debug(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				result.add(new ItemAccessor(rs.getInt(1), rs.getLong(2), rs.getLong(3), rs.getString(4), rs.getInt(5)));
			}
			rs.close();
			stmt.close();
			return result;
		} finally {
			if (conn != null && !conn.isClosed())
				conn.close();
		}
	}
	/**
	 * Загрузить все айтемы, которые хранят ссылки на данный айтем (все айтемы, к которым прицеплен данный айтем)
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public ArrayList<ItemAccessor> loadMountedToItems(long itemId, long userId) throws Exception {
		ArrayList<ItemAccessor> result = new ArrayList<ItemAccessor>();
		Connection conn = null;
		try {
			conn = MysqlConnector.getConnection();
			Statement stmt = conn.createStatement();
			String sql 
				= "SELECT I." + DBConstants.Item.TYPE_ID + ", I." + DBConstants.Item.ID + ", I." + DBConstants.Item.REF_ID 
				+ ", P." + DBConstants.Item.KEY + ", P." + DBConstants.Item.INDEX_WEIGHT
				+ " FROM " + DBConstants.Item.TABLE + " AS I, " + DBConstants.Item.TABLE
				+ " AS P WHERE I." + DBConstants.Item.REF_ID + " = " + itemId
				+ " AND I." + DBConstants.Item.ID + " != I." + DBConstants.Item.REF_ID
				+ " AND I." + DBConstants.Item.DIRECT_PARENT_ID + " = P." + DBConstants.Item.ID
				+ " ORDER BY P." + DBConstants.Item.TYPE_ID + ", " + DBConstants.Item.KEY;

			// Выполнение запроса
			ServerLogger.debug(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				result.add(new ItemAccessor(rs.getInt(1), rs.getLong(2), rs.getLong(3), rs.getString(4), rs.getInt(5)));
			}
			rs.close();
			stmt.close();
			return result;
		} finally {
			if (conn != null && !conn.isClosed())
				conn.close();
		}
	}
}
