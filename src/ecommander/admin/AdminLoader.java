package ecommander.admin;

import ecommander.fwk.MysqlConnector;
import ecommander.model.*;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;
import org.apache.commons.lang3.StringUtils;

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
		base.SELECT(I_ID, I_KEY, I_T_KEY, I_GROUP, I_USER, I_STATUS, I_TYPE_ID, I_PROTECTED, IP_TABLE + ".*")
				.FROM(I_TABLE).INNER_JOIN(IP_TABLE, I_ID, joinByChild ? IP_CHILD_ID : IP_PARENT_ID).WHERE();
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
	static ArrayList<ItemAccessor> loadClosestSubitems(long parentId, User user) throws Exception {
		ItemBasics parent;
		try (Connection conn = MysqlConnector.getConnection()) {
			parent = ItemMapper.loadItemBasics(parentId, conn);
		}
		if (parent == null)
			return new ArrayList<>(0);
		ItemType itemDesc = ItemTypeRegistry.getItemType(parent.getTypeId());
		Byte[] allAssocs = ItemTypeRegistry.getItemOwnAssocIds(itemDesc.getName()).toArray(new Byte[0]);
		HashSet<Byte> adminGroups = new HashSet<>();
		HashSet<Byte> simpleGroups = new HashSet<>();
		for (User.Group group : user.getGroups()) {
			if (group.role == User.ADMIN)
				adminGroups.add(group.id);
			else
				simpleGroups.add(group.id);
		}
		TemplateQuery base = createAccessorQueryBase("Load closest subitems part", true);
		base.col(IP_PARENT_ID).setLong(parentId).AND()
				.col(IP_PARENT_DIRECT).setByte((byte) 1).AND()
				.col(I_STATUS, " IN(").setByteArray(new Byte[] {Item.STATUS_NORMAL, Item.STATUS_NIDDEN}).sql(")").AND()
				.col(IP_ASSOC_ID, " IN(").setByteArray(allAssocs).sql(")").AND();

		TemplateQuery adminQuery = (TemplateQuery) base.createClone();
		TemplateQuery simpleQuery = (TemplateQuery) base.createClone();
		adminQuery.col(I_GROUP, " IN(").setByteArray(adminGroups.toArray(new Byte[0])).sql(")");
		simpleQuery.col(I_GROUP, " IN(").setByteArray(simpleGroups.toArray(new Byte[0])).sql(")").AND()
				.col(I_USER).setInt(user.getUserId());

		TemplateQuery select = new TemplateQuery("Load closest subitems union");
		if (adminGroups.size() > 0)
			select.subquery("ADMIN").replace(adminQuery);
		if (simpleGroups.size() > 0) {
			if (adminGroups.size() > 0)
				select.UNION_ALL();
			select.subquery("COMMON").replace(simpleQuery);
		}
		return loadAccessorsByQuery(select, true);
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
		base.SELECT(I_ID, I_KEY, I_T_KEY, I_GROUP, I_USER, I_STATUS, I_TYPE_ID, I_PROTECTED).FROM(I_TABLE)
				.WHERE().col(I_TYPE_ID, " IN(").setIntArray(allTypes.toArray(new Integer[0])).sql(")").AND()
				.col(I_STATUS, " IN(").setByteArray(new Byte[] {Item.STATUS_NORMAL, Item.STATUS_NIDDEN}).sql(")").AND();

		TemplateQuery adminQuery = (TemplateQuery) base.createClone();
		TemplateQuery simpleQuery = (TemplateQuery) base.createClone();
		adminQuery.col(I_GROUP, " IN(").setByteArray(adminGroups.toArray(new Byte[0])).sql(")");
		simpleQuery.col(I_GROUP, " IN(").setByteArray(simpleGroups.toArray(new Byte[0])).sql(")").AND()
				.col(I_USER).setInt(user.getUserId());

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
		query.col(IP_CHILD_ID).setLong(baseId).AND().col(IP_ASSOC_ID).setByte(assocId).AND()
				.col(I_STATUS, " IN(").setByteArray(new Byte[] {Item.STATUS_NORMAL, Item.STATUS_NIDDEN}).sql(")")
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
				.FROM(I_TABLE).WHERE().col(I_ID, " IN(").setLongArray(itemId).sql(")");
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
				.FROM(I_TABLE).WHERE().col(I_KEY, " LIKE ").setString("%" + key + "%");
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
		query.SELECT("*").FROM(I_TABLE).WHERE().col(I_ID).setLong(itemId);
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
		base.col(IP_CHILD_ID).setLong(itemId).AND()
				.col(IP_PARENT_DIRECT).setByte((byte) 1).AND()
				.col(I_STATUS, " IN(").setByteArray(new Byte[] {Item.STATUS_NORMAL, Item.STATUS_NIDDEN}).sql(")").AND()
				.col(IP_ASSOC_ID, " IN(").setByteArray(ItemTypeRegistry.getAllAssocIds()).sql(")").AND();

		TemplateQuery adminQuery = (TemplateQuery) base.createClone();
		TemplateQuery simpleQuery = (TemplateQuery) base.createClone();
		adminQuery.col(I_GROUP, " IN(").setByteArray(adminGroups.toArray(new Byte[0])).sql(")");
		simpleQuery.col(I_GROUP, " IN(").setByteArray(simpleGroups.toArray(new Byte[0])).sql(")").AND()
				.col(I_USER).setInt(user.getUserId());

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
