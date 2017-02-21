package ecommander.controllers.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.MysqlConnector;
import ecommander.common.ServerLogger;
import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeContainer;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.users.User;

/**
 * @author EEEE
 *
 */
public class AdminLoader {
	private static ThreadLocal<AdminLoader> threadLocalInstance = new ThreadLocal<AdminLoader>() {
		@Override
		protected AdminLoader initialValue() {
			return new AdminLoader();
		}
	};
	
	public static AdminLoader getLoader() {
		return threadLocalInstance.get();
	}
	/**
	 * Загружает всех сабайтемов определенного айтема (не сами айтемы, а их аксэсоры)
	 * Загружаются следующие айтемы:
	 * 		- неперсональные айтемы текущей группы (критерий - тип айтема и номер группы)
	 * 		- персональные айтемы текущей группы (критерий - тип айтема и номер юзера)
	 * 		- айтемы не принадлежащие текущей группе, но которые могут иметь сабайтемы текущей группы (критерий - тип айтема)
	 * @param itemDesc
	 * @param parentId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, ArrayList<ItemAccessor>> loadClosestSubitems(ItemTypeContainer itemDesc, long parentId, User user) throws Exception {
		HashMap<String, ArrayList<ItemAccessor>> result = new HashMap<String, ArrayList<ItemAccessor>>();
		Connection conn = null;
		try {
			conn = MysqlConnector.getConnection();
		
			// Общий шаблон
			String template 
				= "<<UNION>>SELECT " + DBConstants.Item.TYPE_ID + ", " + DBConstants.Item.ID + ", " + DBConstants.Item.REF_ID 
				+ ", " + DBConstants.Item.KEY	+ ", " + DBConstants.Item.INDEX_WEIGHT
				+ " FROM " + DBConstants.Item.TABLE + " WHERE " + DBConstants.Item.DIRECT_PARENT_ID + " = " + parentId 
				+ " AND " + DBConstants.Item.TYPE_ID + " IN (<<TYPE>>) <<WHERE>> <<NEXT_QUERY>>";
			
			// Общие (неперсональные) айтемы текущей группы
			Collection<String> subitemNames = ItemTypeRegistry.getUserGroupAllowedSubitems(itemDesc.getName(), user.getGroup(), false);
			Collection<Integer> subitemIds = ItemTypeRegistry.getItemTypeIds(subitemNames);
			TemplateQuery query = TemplateQuery.createFromString(template, "Public subitems SELECT");
			query.getSubquery("<<TYPE>>").setIntArray(subitemIds.toArray(new Integer[0]));
			query.getSubquery("<<WHERE>>")
				.sql(" AND " + DBConstants.Item.OWNER_GROUP_ID + " = " + user.getGroupId())
				.sql(" AND " + DBConstants.Item.OWNER_USER_ID + " = 0");
			
			// Персональные айтемы текущей группы
			subitemNames = ItemTypeRegistry.getUserGroupAllowedSubitems(itemDesc.getName(), user.getGroup(), true);
			subitemIds = ItemTypeRegistry.getItemTypeIds(subitemNames);
			TemplateQuery personalQuery = query.getSubquery("<<NEXT_QUERY>>").createFromTemplate(template);
			personalQuery.getSubquery("<<UNION>>").sql(" UNION ");
			personalQuery.getSubquery("<<TYPE>>").setIntArray(subitemIds.toArray(new Integer[0]));
			personalQuery.getSubquery("<<WHERE>>")
				.sql(" AND " + DBConstants.Item.OWNER_USER_ID + " = " + user.getUserId());
			
			// Транзитные айтемы (принадлежащие другим группам, но могущие содержать айтемы текущей группы пользователей)
			// Нужны для того, чтобы юзер мог перейти к нужным айтемам в CMS
			subitemNames = ItemTypeRegistry.getUserGroupAllowedTransitionalSubitems(itemDesc.getName(), user.getGroup());
			subitemIds = ItemTypeRegistry.getItemTypeIds(subitemNames);
			TemplateQuery transitionalQuery = personalQuery.getSubquery("<<NEXT_QUERY>>").createFromTemplate(template);
			transitionalQuery.getSubquery("<<UNION>>").sql(" UNION ");
			transitionalQuery
				.getSubquery("<<TYPE>>").setIntArray(subitemIds.toArray(new Integer[0]));
			
			// Сортировка по весу
			transitionalQuery.sql(" ORDER BY " + DBConstants.Item.INDEX_WEIGHT);
			
			// Выполнение запроса
			PreparedStatement pstmt = query.prepareQuery(conn);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int itemTypeId = rs.getInt(1);
				String itemName = ItemTypeRegistry.getItemType(itemTypeId).getName();
				String baseName = ItemTypeRegistry.findItemPredecessor(itemDesc.getAllSubitems(), itemName);
				ArrayList<ItemAccessor> sameItems = result.get(baseName);
				if (sameItems == null) {
					sameItems = new ArrayList<ItemAccessor>();
					result.put(baseName, sameItems);
				}
				sameItems.add(new ItemAccessor(itemTypeId, rs.getLong(2), rs.getLong(3), rs.getString(4), rs.getInt(5)));
			}
			rs.close();
			pstmt.close();
			return result;
		} finally {
			if (conn != null && !conn.isClosed())
				conn.close();
		}
	}
	/**
	 * Загружает всех сабайтемов определенного айтема, в которые можно перемещать заданный
	SELECT
	I_TYPE_NAME, I_ID, I_KEY, I_ID NOT IN
	(
	  SELECT DISTINCT IP_ITEM_ID
	  FROM ItemParent
	  WHERE IP_PARENT_ID = itemToMoveId
	) AND I_ID != itemToMoveId AMD I_ID != itemToMoveParentId AS M
	FROM Item, ItemParent
	WHERE I_ID = IP_ITEM_ID
	AND I_ID = I_REF_ID
	AND IP_PARENT_ID = parentMoveToId
	AND IP_LEVEL = 1
	ORDER BY I_CHILD_INDEX

	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public ArrayList<ItemAccessor> loadItemsToMoveTo(long parentMoveToId, long itemToMoveId, long itemToMoveParentId) throws Exception {
		ArrayList<ItemAccessor> result = new ArrayList<ItemAccessor>();
		Connection conn = null;
		try {
			conn = MysqlConnector.getConnection();
			Statement stmt = conn.createStatement();
			String sql 
				= "SELECT " + DBConstants.Item.TYPE_ID + ", " + DBConstants.Item.ID + ", " + DBConstants.Item.REF_ID 
				+ ", " + DBConstants.Item.KEY + ", " + DBConstants.Item.INDEX_WEIGHT + ", "
				+ DBConstants.Item.ID + " NOT IN (SELECT DISTINCT " + DBConstants.ItemParent.REF_ID 
				+ " FROM " + DBConstants.ItemParent.TABLE + " WHERE " + DBConstants.ItemParent.PARENT_ID + " = " + itemToMoveId 
				+ " ) AND " + DBConstants.Item.ID + " != " + itemToMoveId	
				+ " AND " + DBConstants.Item.ID + " != " + itemToMoveParentId	
				+ " AS M FROM " + DBConstants.Item.TABLE + ", " + DBConstants.ItemParent.TABLE 
				+ " WHERE " + DBConstants.Item.ID + " = " + DBConstants.ItemParent.ITEM_ID
				+ " AND " + DBConstants.Item.ID + " = " + DBConstants.Item.REF_ID
				+ " AND " + DBConstants.ItemParent.PARENT_ID + " = " + parentMoveToId
				+ " AND " + DBConstants.ItemParent.PARENT_LEVEL + " = 1 ORDER BY " 
				+ DBConstants.Item.TYPE_ID + ", " + DBConstants.Item.INDEX_WEIGHT;

			// Выполнение запроса
			ServerLogger.debug(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				result.add(new ItemAccessor(rs.getInt(1), rs.getLong(2), rs.getLong(3), rs.getString(4), rs.getInt(5), rs.getBoolean(6)));
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
	 * Загружает всех сабайтемов определенного айтема, в которых можно создавать ссылки на заданный

	SELECT
	I_TYPE_NAME, I_ID, I_KEY, I_ID NOT IN
	(
	  SELECT DISTINCT IP_PARENT_ID
	  FROM ItemParent
	  WHERE IP_REF_ID = 22
	  UNION
	  SELECT DISTINCT IP_REF_ID
	  FROM ItemParent
	  WHERE IP_PARENT_ID = 22
	  UNION
	  SELECT I_PARENT_ID
	  FROM Item
	  WHERE I_REF_ID = 22
	) AS M
	FROM Item, ItemParent
	WHERE I_ID = IP_REF_ID
	AND I_ID = I_REF_ID
	AND IP_PARENT_ID = 25
	AND IP_LEVEL = 1
	AND IP_REF_ID NOT IN
	(
	  SELECT DISTINCT IP_REF_ID
	  FROM ItemParent
	  WHERE IP_PARENT_ID = 22 OR IP_REF_ID = 22
	)
	ORDER BY I_CHILD_INDEX

	Сейчас не так, как написано выше. Сейчас можно создавать все ссылки, кроме ссылки на самого себя и ссылки на непосредственного потомка
	
	SELECT
	I_TYPE_NAME, I_ID, I_KEY, I_ID NOT IN
	(
	  SELECT DISTINCT IP_PARENT_ID
	  FROM ItemParent
	  WHERE IP_REF_ID = itemToMountId
	  UNION
	  SELECT DISTINCT IP_REF_ID
	  FROM ItemParent
	  WHERE IP_PARENT_ID = itemToMountId
	) AND I_ID != itemToMountId AS M
	FROM Item, ItemParent
	WHERE I_ID = IP_REF_ID
	AND I_ID = I_REF_ID
	AND IP_PARENT_ID = parentMountToId
	AND IP_LEVEL = 1
	ORDER BY I_CHILD_INDEX

	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public ArrayList<ItemAccessor> loadItemsToMountTo(long parentMountToId, long itemToMountId) throws Exception {
		ArrayList<ItemAccessor> result = new ArrayList<ItemAccessor>();
		Connection conn = null;
		try {
			conn = MysqlConnector.getConnection();
			Statement stmt = conn.createStatement();
			String sql 
				= "SELECT " + DBConstants.Item.TYPE_ID + ", " + DBConstants.Item.ID + ", " + DBConstants.Item.REF_ID 
				+ ", " + DBConstants.Item.KEY + ", " + DBConstants.Item.INDEX_WEIGHT + ", " + DBConstants.Item.ID 
				+ " NOT IN (SELECT DISTINCT " + DBConstants.ItemParent.PARENT_ID + " FROM " + DBConstants.ItemParent.TABLE 
				+ " WHERE " + DBConstants.ItemParent.REF_ID  + " = " + itemToMountId + " AND " + DBConstants.ItemParent.PARENT_LEVEL + "=1"
				+ " UNION SELECT DISTINCT " + DBConstants.ItemParent.REF_ID + " FROM " + DBConstants.ItemParent.TABLE
				+ " WHERE " + DBConstants.ItemParent.PARENT_ID + " = " + itemToMountId + " AND " + DBConstants.ItemParent.PARENT_LEVEL + "=1"
				+ " ) AND " + DBConstants.Item.ID + " != " + itemToMountId	
				+ " AS M FROM " + DBConstants.Item.TABLE + ", " + DBConstants.ItemParent.TABLE 
				+ " WHERE " + DBConstants.Item.ID + " = " + DBConstants.ItemParent.ITEM_ID
				+ " AND " + DBConstants.Item.ID + " = " + DBConstants.Item.REF_ID
				+ " AND " + DBConstants.ItemParent.PARENT_ID + " = " + parentMountToId
				+ " AND " + DBConstants.ItemParent.PARENT_LEVEL + " = 1 ORDER BY " 
				+ DBConstants.Item.TYPE_ID + ", " + DBConstants.Item.INDEX_WEIGHT;

			// Выполнение запроса
			ServerLogger.debug(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				result.add(new ItemAccessor(rs.getInt(1), rs.getLong(2), rs.getLong(3), rs.getString(4), rs.getInt(5), rs.getBoolean(6)));
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
	 * Загружает всех сабайтемов определенного айтема, в которые можно переместить заданный

	SELECT
	I_TYPE_NAME, I_ID, I_KEY, I_ID NOT IN
	(
	  SELECT DISTINCT IP_PARENT_ID
	  FROM ItemParent
	  WHERE IP_REF_ID = itemToMoveToId
	) AND I_ID != itemToMoveToId AND I_PARENT_ID != itemToMoveToId AS M
	FROM Item, ItemParent
	WHERE I_ID = IP_REF_ID
	AND I_ID = I_REF_ID
	AND IP_PARENT_ID = parentMoveToId
	AND IP_LEVEL = 1
	ORDER BY I_CHILD_INDEX
		
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public ArrayList<ItemAccessor> loadItemsToMove(long parentToMoveId, long itemToMoveToId) throws Exception {
		ArrayList<ItemAccessor> result = new ArrayList<ItemAccessor>();
		Connection conn = null;
		try {
			conn = MysqlConnector.getConnection();
			Statement stmt = conn.createStatement();
			String sql 
				= "SELECT " + DBConstants.Item.TYPE_ID + ", " + DBConstants.Item.ID + ", " + DBConstants.Item.REF_ID + ", "
				+ DBConstants.Item.KEY + ", " + DBConstants.Item.INDEX_WEIGHT + ", "
				+ DBConstants.Item.ID + " NOT IN (SELECT DISTINCT " + DBConstants.ItemParent.PARENT_ID 
				+ " FROM " + DBConstants.ItemParent.TABLE + " WHERE " + DBConstants.ItemParent.REF_ID  + " = " + itemToMoveToId
				+ " ) AND " + DBConstants.Item.ID + " != " + itemToMoveToId 
				+ " AND " + DBConstants.Item.DIRECT_PARENT_ID + " != " + itemToMoveToId 
				+ " AS M FROM " + DBConstants.Item.TABLE + ", " + DBConstants.ItemParent.TABLE 
				+ " WHERE " + DBConstants.Item.ID + " = " + DBConstants.ItemParent.ITEM_ID
				+ " AND " + DBConstants.Item.ID + " = " + DBConstants.Item.REF_ID
				+ " AND " + DBConstants.ItemParent.PARENT_ID + " = " + parentToMoveId
				+ " AND " + DBConstants.ItemParent.PARENT_LEVEL + " = 1 ORDER BY " 
				+ DBConstants.Item.TYPE_ID + ", " + DBConstants.Item.INDEX_WEIGHT;

			// Выполнение запроса
			ServerLogger.debug(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				result.add(new ItemAccessor(rs.getInt(1), rs.getLong(2), rs.getLong(3), rs.getString(4), rs.getInt(5), rs.getBoolean(6)));
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
	 * Загружает всех сабайтемов определенного айтема, в которых можно создавать ссылки на заданный
		
	SELECT I_TYPE_NAME, I_ID, I_KEY, I_ID NOT IN
	(
	  SELECT DISTINCT IP_PARENT_ID
	  FROM ItemParent
	  WHERE IP_ITEM_ID = 22
	  UNION
	  SELECT DISTINCT IP_ITEM_ID
	  FROM ItemParent
	  WHERE IP_PARENT_ID = 22
	  UNION
	  SELECT I_REF_ID  // Отличие от mount to
	  FROM Item
	  WHERE I_PARENT_ID = 22
	)
	AS M
	FROM Item, ItemParent
	WHERE I_ID = IP_ITEM_ID
	AND IP_PARENT_ID = 5
	AND IP_LEVEL = 1
	AND I_ID != I_PARENT_ID
	AND IP_ITEM_ID NOT IN
	(
	  SELECT DISTINCT IP_ITEM_ID
	  FROM ItemParent
	  WHERE IP_PARENT_ID = 22 OR IP_ITEM_ID = 22
	)
	ORDER BY I_CHILD_INDEX

	Сейчас не так, как написано выше. Сейчас можно создавать все ссылки, кроме ссылки на самого себя и ссылки на непосредственного потомка
	
	SELECT
	I_TYPE_NAME, I_ID, I_KEY, I_ID NOT IN
	(
	  SELECT DISTINCT IP_REF_ID
	  FROM ItemParent
	  WHERE IP_PARENT_ID = itemToMountToId
	  UNION
	  SELECT DISTINCT IP_PARENT_ID
	  FROM ItemParent
	  WHERE IP_REF_ID = itemToMountToId
	) AND I_ID != itemToMountToId AS M
	FROM Item, ItemParent
	WHERE I_ID = IP_REF_ID
	AND I_ID = I_REF_ID
	AND IP_PARENT_ID = parentMountToId
	AND IP_LEVEL = 1
	ORDER BY I_CHILD_INDEX
	
		
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public ArrayList<ItemAccessor> loadItemsToMount(long parentToMountId, long itemToMountToId) throws Exception {
		ArrayList<ItemAccessor> result = new ArrayList<ItemAccessor>();
		Connection conn = null;
		try {
			conn = MysqlConnector.getConnection();
			Statement stmt = conn.createStatement();
			String sql 
				= "SELECT " + DBConstants.Item.TYPE_ID + ", " + DBConstants.Item.ID + ", " + DBConstants.Item.REF_ID + ", " 
				+ DBConstants.Item.KEY + ", " + DBConstants.Item.INDEX_WEIGHT + ", "
				+ DBConstants.Item.ID + " NOT IN (SELECT DISTINCT " + DBConstants.ItemParent.REF_ID 
				+ " FROM " + DBConstants.ItemParent.TABLE + " WHERE " + DBConstants.ItemParent.PARENT_ID  + " = " + itemToMountToId
				+ " UNION SELECT DISTINCT " + DBConstants.ItemParent.PARENT_ID 
				+ " FROM " + DBConstants.ItemParent.TABLE + " WHERE " + DBConstants.ItemParent.REF_ID  + " = " + itemToMountToId
				+ " ) AND " + DBConstants.Item.ID + " != " + itemToMountToId 
				+ " AS M FROM " + DBConstants.Item.TABLE + ", " + DBConstants.ItemParent.TABLE 
				+ " WHERE " + DBConstants.Item.ID + " = " + DBConstants.ItemParent.ITEM_ID
				+ " AND " + DBConstants.Item.ID + " = " + DBConstants.Item.REF_ID
				+ " AND " + DBConstants.ItemParent.PARENT_ID + " = " + parentToMountId
				+ " AND " + DBConstants.ItemParent.PARENT_LEVEL + " = 1 ORDER BY " 
				+ DBConstants.Item.TYPE_ID + ", " + DBConstants.Item.INDEX_WEIGHT;

			// Выполнение запроса
			ServerLogger.debug(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				result.add(new ItemAccessor(rs.getInt(1), rs.getLong(2), rs.getLong(3), rs.getString(4), rs.getInt(5), rs.getBoolean(6)));
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
	 * Загружает айтем и всех его предков
	 * @param itemId
	 * @return
	 */
	public ArrayList<ItemAccessor> loadWholeBranch(long baseId, User user) throws Exception {
		ArrayList<ItemAccessor> result = new ArrayList<ItemAccessor>();
		Connection conn = null;
		try {
			conn = MysqlConnector.getConnection();
			Statement stmt = conn.createStatement();
			// Чтобы не загружать корневые айтемы, добавляется критерий DIRECT_PARENT != 0
			String sql 
					= "SELECT DISTINCT " 
					+ DBConstants.Item.TYPE_ID + ", " + DBConstants.Item.ID + ", " + DBConstants.Item.REF_ID  + ", "
					+ DBConstants.Item.KEY  + ", " + DBConstants.Item.INDEX_WEIGHT
					+ " FROM " + DBConstants.Item.TABLE + ", " + DBConstants.ItemParent.TABLE
					+ " WHERE " + DBConstants.Item.ID + " = " + DBConstants.ItemParent.PARENT_ID 
					+ " AND " + DBConstants.ItemParent.ITEM_ID + " = " + baseId 
					+ " AND " + DBConstants.Item.DIRECT_PARENT_ID + " != 0" 
					+ " ORDER BY " + DBConstants.ItemParent.PARENT_LEVEL + " DESC";
			ServerLogger.debug(sql);
			ResultSet rs = stmt.executeQuery(sql);
			// Добавить корень
			RootItemType rootDesc = ItemTypeRegistry.getGroupRoot(user.getGroup());
			result.add(new ItemAccessor(0, rootDesc.getItemId(), rootDesc.getItemId(), "Корень", 0));
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
			if (result.contains(item.getItemName()) && itemDesc.isSubitemSingle(item.getItemName()))
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
