package ecommander.persistence.mappers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import ecommander.common.ServerLogger;
import ecommander.common.exceptions.EcommanderException;
import ecommander.model.item.Item;
import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.model.item.MultipleParameter;
import ecommander.model.item.Parameter;
import ecommander.model.item.ParameterDescription;
import ecommander.model.item.SingleParameter;
import ecommander.persistence.TransactionContext;
import ecommander.persistence.common.TemplateQuery;

/**
 * Выполняет различные операции с Item и БД
 * @author EEEE
 *
 */
public class ItemMapper {

	private static final String PARAM_INSERT_PREPARED_START 
		= " ("
		+ DBConstants.ItemIndexes.REF_ID + ", " 
		+ DBConstants.ItemIndexes.ITEM_PARAM + ", " 
		+ DBConstants.ItemIndexes.ITEM_TYPE + ", " 
		+ DBConstants.ItemIndexes.ITEM_PARENT + ", " 
		+ DBConstants.ItemIndexes.VALUE
		+ ") VALUES (";
	
	private static final String PARAM_INSERT_PREPARED_END
		= ") ON DUPLICATE KEY UPDATE " + DBConstants.ItemIndexes.VALUE + " = ";
	
//	private static final String PARAM_INSERT_PREPARED_END
//		= ") ON DUPLICATE KEY UPDATE " + DBConstants.ItemIndexes.REF_ID + " = " + DBConstants.ItemIndexes.REF_ID + ";";
//	private static final String DELETE_PREDECESSOR_PARAMS_ITEM
//		= "UPDATE " + DatabaseConstants.Item.TABLE 
//		+ " SET " + DatabaseConstants.Item.PARAMS + "='' WHERE " + DatabaseConstants.Item.TYPE_ID + "=?";
	
	private static final String DELETE_PREDECESSOR_PARAMS_INDEX
		= " WHERE " + DBConstants.ItemIndexes.ITEM_PARAM + "=? AND " + DBConstants.ItemIndexes.ITEM_TYPE + "=?";
	/**
	 * Сохраняет все параметры айтема в индекс, предварительно удалив старые значения если это нужно
	 * @param item
	 * @param deleteNeeded
	 * @param transaction
	 * @throws SQLException
	 * @throws EcommanderException 
	 */
	public static void insertItemParametersToIndex(Item item, boolean deleteNeeded, TransactionContext transaction) throws SQLException, EcommanderException {
		// Подсчет количества непустых параметров для подготовки препаред стэйтмента
		if (deleteNeeded) {
			String deleteSql 
				= "DELETE FROM " + DBConstants.ItemIndexes.INT_TABLE_NAME 
				+ " WHERE " + DBConstants.ItemIndexes.REF_ID + "=" + item.getRefId()
				+ "; DELETE FROM " + DBConstants.ItemIndexes.STRING_TABLE_NAME 
				+ " WHERE " + DBConstants.ItemIndexes.REF_ID + "=" + item.getRefId()
				+ "; DELETE FROM " + DBConstants.ItemIndexes.DOUBLE_TABLE_NAME 
				+ " WHERE " + DBConstants.ItemIndexes.REF_ID + "=" + item.getRefId()
				+ "; DELETE FROM " + DBConstants.ItemIndexes.ASSOCIATED_TABLE_NAME 
				+ " WHERE " + DBConstants.ItemIndexes.REF_ID + "=" + item.getRefId();
			ServerLogger.debug(deleteSql);
			PreparedStatement deleteStmt = transaction.getConnection().prepareStatement(deleteSql);
			deleteStmt.executeUpdate();
		}
		
		TemplateQuery query = new TemplateQuery("Item index insert");
		for (ParameterDescription paramDesc : item.getItemType().getParameterList()) {
			Parameter param = item.getParameter(paramDesc.getId());
			if (!param.needsDBIndex()) // не сохранять в БД виртуальные параметры
				continue;
			if (param.isMultiple() && !param.isEmpty()) {
				for (SingleParameter singleParam : ((MultipleParameter)param).getValues())
					createSingleValueInsert(query, item, singleParam);
			} else {
				if (!param.isEmpty())
					createSingleValueInsert(query, item, (SingleParameter)param);
			}
		}
		// Добавить параметры пользователя и группы
		SingleParameter userParam = ParameterDescription.USER.createSingleParameter();
		userParam.setValue(new Long(item.getOwnerUserId()));
		createSingleValueInsert(query, item, userParam);
		SingleParameter groupParam = ParameterDescription.GROUP.createSingleParameter();
		groupParam.setValue(new Integer(item.getOwnerGroupId()));
		createSingleValueInsert(query, item, groupParam);
		// Выполнения запроса
		PreparedStatement insertStmt = query.prepareQuery(transaction.getConnection());
		try {
			insertStmt.executeUpdate();
		} catch (Exception e) {
			throw new EcommanderException(query.getSimpleSql(), e);
		}
	}
	
	private static void createSingleValueInsert(TemplateQuery query, Item item, SingleParameter param) throws SQLException {
		query.sql("INSERT INTO ").sql(DataTypeMapper.getTableName(param.getType())).sql(PARAM_INSERT_PREPARED_START);
		query
			.setLong(item.getRefId()).sql(",")
			.setInt(param.getParamId()).sql(",")
			.setInt(item.getTypeId()).sql(",")
			.setLong(item.getDirectParentId()).sql(",");
		DataTypeMapper.appendPreparedStatementInsertValue(param.getType(), query, param.getValue());
		query.sql(PARAM_INSERT_PREPARED_END);
		DataTypeMapper.appendPreparedStatementInsertValue(param.getType(), query, param.getValue());
		query.sql("; ");
	}
	/**
	 * Произвести изменения, которые связаны с удалением предка айтема по иерархии типов, а именно
	 * - удалить кешированные параметры айтемов из таблицы айтемов
	 * - удалить параметры айтемов из индекса
	 * 
	 * TODO <performance> можно сделать удаление в виде IN списка
	 * @param item
	 * @param predecessorName
	 * @param transaction
	 * @throws SQLException
	 */
	public static void deleteItemPredecessor(ItemType item, String predecessorName, TransactionContext transaction) throws SQLException {
		ItemType predecessor = ItemTypeRegistry.getItemType(predecessorName);
		PreparedStatement deleteIndex = null;
		try {
			for (ParameterDescription param : predecessor.getParameterList()) {
				String query = "DELETE FROM " + DataTypeMapper.getTableName(param.getType()) + DELETE_PREDECESSOR_PARAMS_INDEX;
				deleteIndex = transaction.getConnection().prepareStatement(query);
				deleteIndex.setInt(1, param.getId());
				deleteIndex.setInt(2, item.getTypeId());
				deleteIndex.executeUpdate();
			}
		} finally {
			if (deleteIndex != null) deleteIndex.close();
		}
	}
	/**
	 * Удаляет параметр айтема из таблицы индекса
	 * @param item
	 * @param param
	 * @param pstmt
	 * @throws SQLException 
	 */
	public static void deleteItemParameter(ItemType item, String paramName, TransactionContext transaction) throws SQLException {
		PreparedStatement deleteIndex = null;
		try {
			String query = "DELETE FROM " + DataTypeMapper.getTableName(item.getParameter(paramName).getType()) + DELETE_PREDECESSOR_PARAMS_INDEX;
			deleteIndex = transaction.getConnection().prepareStatement(query);
			deleteIndex.setInt(1, item.getParameter(paramName).getId());
			deleteIndex.setInt(2, item.getTypeId());
			deleteIndex.executeUpdate();
		} finally {
			if (deleteIndex != null) deleteIndex.close();
		}
	}
	/**
	 * Возвращает запрос для получения всех предшественников айтема
	 * @param itemId
	 * @return
	 */
	public static String getItemParentIdsSQL(long itemId) {
		return 
			"SELECT " + DBConstants.Item.PRED_ID_PATH 
			+ " FROM " + DBConstants.Item.TABLE 
			+ " WHERE " + DBConstants.Item.ID + "=" + itemId;
	}
	/**
	 * Создать айтем из резалт сета
	 * @param rs
	 * @param parentColName
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static Item buildItem(ResultSet rs, String parentColName) throws SQLException, Exception {
		long itemId = rs.getLong(DBConstants.Item.ID);
		long refId = rs.getLong(DBConstants.Item.REF_ID);
		int itemTypeId = rs.getInt(DBConstants.Item.TYPE_ID);
		int itemWeight= rs.getInt(DBConstants.Item.INDEX_WEIGHT);
		long userId = rs.getLong(DBConstants.Item.OWNER_USER_ID);
		int groupId = rs.getInt(DBConstants.Item.OWNER_GROUP_ID);
		String key = rs.getString(DBConstants.Item.KEY);
		String keyUnique = rs.getString(DBConstants.Item.TRANSLIT_KEY);
		String predIdPath = rs.getString(DBConstants.Item.PRED_ID_PATH);
		long parentId = rs.getLong(parentColName);
		Timestamp timeUpdated = rs.getTimestamp(DBConstants.Item.UPDATED);
		String params = rs.getString(DBConstants.Item.PARAMS);
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemTypeId);
		return Item.existingItem(itemDesc, itemId, parentId, predIdPath, refId, userId, groupId, itemWeight, key, params, keyUnique,
				timeUpdated.getTime());
	}
}