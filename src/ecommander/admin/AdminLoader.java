package ecommander.admin;

import ecommander.fwk.MysqlConnector;
import ecommander.model.*;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author EEEE
 *
 */
class AdminLoader implements DBConstants.ItemTbl, DBConstants.ItemParent {

	private static TemplateQuery createAccessorQueryBase(String queryName, boolean joinByChild) {
		TemplateQuery base = new TemplateQuery(queryName);
		base.SELECT(I_ID, I_KEY, I_T_KEY, I_GROUP, I_USER, I_STATUS, I_TYPE_ID, I_PROTECTED, ITEM_PARENT_TBL + ".*")
				.FROM(ITEM_TBL).INNER_JOIN(ITEM_PARENT_TBL, I_ID, joinByChild ? IP_CHILD_ID : IP_PARENT_ID).WHERE();
		return base;
	}

	private static ArrayList<ItemAccessor> loadAccessorsByQuery(TemplateQuery query, boolean hasParentTableJoin) throws SQLException, NamingException {
		ArrayList<ItemAccessor> result = new ArrayList<>();
		try (Connection conn = MysqlConnector.getConnection();
		     PreparedStatement pstmt = query.prepareQuery(conn);
		) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int weight = 0;
				byte assoc = ItemTypeRegistry.getPrimaryAssoc().getId();
				if (hasParentTableJoin) {
					weight = rs.getInt(IP_WEIGHT);
					assoc = rs.getByte(IP_ASSOC_ID);
				}
				result.add(new ItemAccessor(
						rs.getInt(I_TYPE_ID),
						rs.getLong(I_ID),
						rs.getString(I_KEY),
						rs.getByte(I_GROUP),
						rs.getInt(I_USER),
						rs.getByte(I_STATUS),
						rs.getByte(I_PROTECTED) == (byte) 1,
						weight,
						assoc,
						true));
			}
		}
		return result;
	}
	/**
	 * Загружает всех сабайтемов определенного айтема (не сами айтемы, а их аксэсоры)
	 * Загружаются следующие айтемы:
	 * 		- неперсональные айтемы текущей группы (критерий - тип айтема и номер группы)
	 * 		- персональные айтемы текущей группы (критерий - тип айтема и номер юзера)
	 * 	    - загружаются потомки по всем ассоциациям
	 * @param parentId
	 * @param user
	 * @return
	 * @throws Exception
	 */
	static ArrayList<ItemAccessor> loadClosestSubitems(long parentId, User user, int page) throws Exception {
		TemplateQuery query = createSubitemsQuery(parentId, user, page, false);
		if (query == null)
			return new ArrayList<>(0);
		return loadAccessorsByQuery(query, true);
	}

	/**
	 * Загружает общее количество всех сабайтемов определенного айтема
	 * Аналогично loadClosestSubitems, только загружаются не айтемы, а их количество
	 * @param parentId
	 * @param user
	 * @return
	 * @throws Exception
	 */
	static int loadClosestSubitemsCount(long parentId, User user) throws Exception {
		TemplateQuery query = createSubitemsQuery(parentId, user, 0, true);
		if (query == null)
			return 0;
		try (Connection conn = MysqlConnector.getConnection();
		     PreparedStatement pstmt = query.prepareQuery(conn);
		) {
			ResultSet rs = pstmt.executeQuery();
			int count = 0;
			while (rs.next()) {
				count += rs.getInt(1);
			}
			return count;
		}
	}

	/**
	 * Подготовка запроса на загружку сабайтемов айтема или количества сабайтемов айтема
	 * @param parentId
	 * @param user
	 * @param page
	 * @param needJustCount
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	private static TemplateQuery createSubitemsQuery(long parentId, User user, int page, boolean needJustCount) throws SQLException, NamingException {
		ItemBasics parent;
		try (Connection conn = MysqlConnector.getConnection()) {
			parent = ItemMapper.loadItemBasics(parentId, conn);
		}
		if (parent == null)
			return null;
		ItemType parentType = ItemTypeRegistry.getItemType(parent.getTypeId());
		Byte[] allAssocs = ItemTypeRegistry.getItemOwnAssocIds(parentType.getName()).toArray(new Byte[0]);
		HashSet<Byte> adminGroups = new HashSet<>();
		HashSet<Byte> simpleGroups = new HashSet<>();
		for (User.Group group : user.getGroups()) {
			if (group.role == User.ADMIN)
				adminGroups.add(group.id);
			else
				simpleGroups.add(group.id);
		}
		TemplateQuery base;
		if (needJustCount) {
			base = new TemplateQuery("Subitems count");
			base.SELECT("count(" + I_ID + ")").FROM(ITEM_TBL).INNER_JOIN(ITEM_PARENT_TBL, I_ID, IP_CHILD_ID).WHERE();
		} else {
			base = createAccessorQueryBase("Load closest subitems part", true);
		}

		base.col(IP_PARENT_ID).long_(parentId).AND()
				.col(IP_PARENT_DIRECT).byte_((byte) 1).AND()
				.col_IN(I_STATUS).byteIN(Item.STATUS_NORMAL, Item.STATUS_NIDDEN).AND()
				.col_IN(IP_ASSOC_ID).byteIN(allAssocs).AND().subquery("<<USER>>");
		if (!needJustCount) {
			base.ORDER_BY(IP_ASSOC_ID + " ASC", IP_WEIGHT + " " + parentType.getChildrenSorting());
			if (parentType.hasChildrenLimit()) {
				page = NumberUtils.max(page, 1);
				if (page > 1) {
					int rowsToSkip = (page - 1) * parentType.getChildrenLimit();
					base.LIMIT(parentType.getChildrenLimit(), rowsToSkip);
				} else {
					base.LIMIT(parentType.getChildrenLimit());
				}
			}
		}

		TemplateQuery adminQuery = (TemplateQuery) base.createClone();
		TemplateQuery simpleQuery = (TemplateQuery) base.createClone();
		adminQuery.getSubquery("<<USER>>").col_IN(I_GROUP).byteIN(adminGroups.toArray(new Byte[0]));
		simpleQuery.getSubquery("<<USER>>").col_IN(I_GROUP).byteIN(simpleGroups.toArray(new Byte[0])).AND()
				.col(I_USER).int_(user.getUserId());

		TemplateQuery select = new TemplateQuery("Load closest subitems union");
		if (adminGroups.size() > 0)
			select.subquery("ADMIN").replace(adminQuery);
		if (simpleGroups.size() > 0) {
			if (adminGroups.size() > 0)
				select.UNION_ALL();
			select.subquery("COMMON").replace(simpleQuery);
		}
		return select;
	}

	/**
	 * Загрузить корневые айтемы для главного админа
	 * @param user
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	static ArrayList<ItemAccessor> loadSuperUserRootItems(User user) throws SQLException, NamingException {
		Collection<ItemTypeContainer.ChildDesc> rootChildren = ItemTypeRegistry.getPrimaryRoot().getAllChildren();
		HashSet<Integer> allTypes = new HashSet<>();
		for (ItemTypeContainer.ChildDesc rootChild : rootChildren) {
			allTypes.add(ItemTypeRegistry.getItemTypeId(rootChild.itemName));
		}
		HashSet<Byte> adminGroups = new HashSet<>();
		HashSet<Byte> simpleGroups = new HashSet<>();
		for (User.Group group : user.getGroups()) {
			if (group.role == User.ADMIN)
				adminGroups.add(group.id);
			else
				simpleGroups.add(group.id);
		}
		TemplateQuery base = new TemplateQuery("Load root subitems part");
		base.SELECT(I_ID, I_KEY, I_T_KEY, I_GROUP, I_USER, I_STATUS, I_TYPE_ID, I_PROTECTED).FROM(ITEM_TBL)
				.WHERE().col_IN(I_SUPERTYPE).intIN(allTypes.toArray(new Integer[0])).AND()
				.col_IN(I_STATUS).byteIN(Item.STATUS_NORMAL, Item.STATUS_NIDDEN).AND();

		TemplateQuery adminQuery = (TemplateQuery) base.createClone();
		TemplateQuery simpleQuery = (TemplateQuery) base.createClone();
		adminQuery.col_IN(I_GROUP).byteIN(adminGroups.toArray(new Byte[0]));
		simpleQuery.col_IN(I_GROUP).byteIN(simpleGroups.toArray(new Byte[0])).AND()
				.col_IN(I_USER).int_(user.getUserId());

		TemplateQuery select = new TemplateQuery("Load root subitems union");
		if (adminGroups.size() > 0)
			select.subquery("ADMIN").replace(adminQuery);
		if (simpleGroups.size() > 0) {
			if (adminGroups.size() > 0)
				select.UNION_ALL();
			select.subquery("COMMON").replace(simpleQuery);
		}
		ArrayList<ItemAccessor> result = new ArrayList<>();
		try (Connection conn = MysqlConnector.getConnection();
		     PreparedStatement pstmt = select.prepareQuery(conn);
		) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				result.add(new ItemAccessor(
						rs.getInt(I_TYPE_ID),
						rs.getLong(I_ID),
						rs.getString(I_KEY),
						rs.getByte(I_GROUP),
						rs.getInt(I_USER),
						rs.getByte(I_STATUS),
						rs.getByte(I_PROTECTED) == (byte) 1,
						0,
						ItemTypeRegistry.getPrimaryAssoc().getId(),
						true));
			}
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
	static ArrayList<ItemAccessor> loadWholeBranch(long baseId, byte assocId) throws Exception {
		TemplateQuery query = createAccessorQueryBase("Load item branch", false);
		query.col(IP_CHILD_ID).long_(baseId).AND().col(IP_ASSOC_ID).byte_(assocId).AND()
				.col_IN(I_STATUS).byteIN(Item.STATUS_NORMAL, Item.STATUS_NIDDEN)
				.ORDER_BY(IP_PARENT_DIRECT, IP_PARENT_ID);
		return loadAccessorsByQuery(query, true);
	}
	/**
	 * Загружает несколько айтемов по их ID
	 * @param itemId
	 * @return
	 */
	static ArrayList<ItemAccessor> loadItemAccessors(Long... itemId) throws Exception {
		if (itemId.length == 0)
			return new ArrayList<>(0);
		TemplateQuery query = new TemplateQuery("Load accessors by ids");
		query.SELECT(I_ID, I_KEY, I_T_KEY, I_GROUP, I_USER, I_STATUS, I_TYPE_ID, I_PROTECTED)
				.FROM(ITEM_TBL).WHERE().col_IN(I_ID).longIN(itemId);
		return loadAccessorsByQuery(query, false);
	}

	/**
	 * Загружает айетмы по их ключу (Антоновский фикс)
	 * @param key
	 * @return
	 * @throws Exception
	 */
	static ArrayList<ItemAccessor> loadItemAccessorsByKey(String key) throws Exception {
		if (StringUtils.isBlank(key))
			return new ArrayList<>(0);
		TemplateQuery query = new TemplateQuery("Load accessors by ids");
		query.SELECT(I_ID, I_KEY, I_T_KEY, I_GROUP, I_USER, I_STATUS, I_TYPE_ID, I_PROTECTED)
				.FROM(ITEM_TBL).WHERE().col(I_KEY, " LIKE ").string("%" + key + "%");
		return loadAccessorsByQuery(query, false);
	}
	/**
	 * Загружает один айтем по его ID
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	static ItemAccessor loadItemAccessor(long itemId) throws Exception {
		ArrayList<ItemAccessor> result = loadItemAccessors(itemId);
		if (result.size() == 0)
			return null;
		return result.get(0);
	}

	/**
	 * Загрузить айтем (не аксэсор)
	 * @param itemId
	 * @param user
	 * @return
	 * @throws Exception
	 */
	static Item loadItem(long itemId, User user) throws Exception {
		TemplateQuery query = new TemplateQuery("Admin load item");
		query.SELECT("*").FROM(ITEM_TBL).WHERE().col(I_ID).long_(itemId);
		Item item = null;
		try (Connection conn = MysqlConnector.getConnection();
			PreparedStatement pstmt = query.prepareQuery(conn)) {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				item = ItemMapper.buildItem(rs, ItemTypeRegistry.getPrimaryAssoc().getId(), Item.DEFAULT_ID);
		}
		if (item != null)
			Security.testPrivileges(user, item);
		return item;
	}

	/**
	 * Загрузить все айтемы, которые хранят ссылки на данный айтем (все айтемы, к которым прицеплен данный айтем)
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	static ArrayList<ItemAccessor> loadDirectParents(long itemId, User user) throws Exception {
		HashSet<Byte> adminGroups = new HashSet<>();
		HashSet<Byte> simpleGroups = new HashSet<>();
		for (User.Group group : user.getGroups()) {
			if (group.role == User.ADMIN)
				adminGroups.add(group.id);
			else
				simpleGroups.add(group.id);
		}
		TemplateQuery base = createAccessorQueryBase("Load direct parents part", false);
		base.col(IP_CHILD_ID).long_(itemId).AND()
				.col(IP_PARENT_DIRECT).byte_((byte) 1).AND()
				.col_IN(I_STATUS).byteIN(Item.STATUS_NORMAL, Item.STATUS_NIDDEN).AND()
				.col_IN(IP_ASSOC_ID).byteIN(ItemTypeRegistry.getAllAssocIds()).AND();

		TemplateQuery adminQuery = (TemplateQuery) base.createClone();
		TemplateQuery simpleQuery = (TemplateQuery) base.createClone();
		adminQuery.col_IN(I_GROUP).byteIN(adminGroups.toArray(new Byte[0]));
		simpleQuery.col_IN(I_GROUP).byteIN(simpleGroups.toArray(new Byte[0])).AND()
				.col(I_USER).int_(user.getUserId());

		TemplateQuery select = new TemplateQuery("Load direct parents union");
		if (adminGroups.size() > 0)
			select.getOrCreateSubquery("ADMIN").replace(adminQuery);
		if (simpleGroups.size() > 0) {
			if (adminGroups.size() > 0)
				select.UNION_ALL();
			select.getOrCreateSubquery("COMMON").replace(simpleQuery);
		}
		return loadAccessorsByQuery(select, true);
	}
}
