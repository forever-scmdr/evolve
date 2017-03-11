package ecommander.persistence.mappers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.EcommanderException;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.MultipleParameter;
import ecommander.model.Parameter;
import ecommander.model.ParameterDescription;
import ecommander.model.SingleParameter;
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
		+ DBConstants.ItemIndexes.ITEM_ID + ", "
		+ DBConstants.ItemIndexes.ITEM_PARAM + ", " 
		+ DBConstants.ItemIndexes.ITEM_TYPE + ", " 
		+ DBConstants.ItemIndexes.VALUE_IDX + ", "
		+ DBConstants.ItemIndexes.VALUE
		+ ") VALUES (";
	
	private static final String ON_DUPLICATE_KEY_UPDATE
		= ") ON DUPLICATE KEY UPDATE " + DBConstants.ItemIndexes.VALUE + " = ";

	/**
	 * Сохраняет все параметры айтема в индекс.
	 * Удаление старых параметров происходит только в том случе, если это надо (параметры были удалены).
	 * Обновление параметров также происходит только если это надо (параметры поменялись)
	 * @param item
	 * @param transaction
	 * @throws SQLException
	 * @throws EcommanderException
	 */
	public static void insertItemParametersToIndex(Item item, TransactionContext transaction) throws SQLException, EcommanderException {
		if (!item.hasChanged())
			return;
		TemplateQuery query;
		// Если айтем новый, то не нужно удалять старые значения и обновлять существующие
		if (item.isNew()) {
			query = new TemplateQuery("Item index insert new");
			for (Parameter param : item.getAllParameters()) {
				if (!param.isEmpty()) {
					if (param.isMultiple()) {
						byte i = (byte) 0;
						for (SingleParameter singleParam : ((MultipleParameter)param).getValues()) {
							createSingleValueInsert(query, item, singleParam, i++, false);
						}
					} else {
						createSingleValueInsert(query, item, (SingleParameter)param, (byte) 0, false);
					}
				}
			}
		}
		// Если айтем не новый, то нужно удалить значения которые были удалены, вставить или обновить остальные значения
		else {
			query = new TemplateQuery("Item index update");
			for (Parameter param : item.getAllParameters()) {
				// Пропустить все неизмененные параметры
				if (!param.hasChanged())
					continue;
				// Удалить старое значение (параметр раньше имел значение, сейчас не имеет)
				if (param.isEmpty()) {
					query.sql("DELETE FROM ").sql(DataTypeMapper.getTableName(param.getType()))
							.sql(" WHERE ").sql(DBConstants.ItemIndexes.ITEM_ID).sql("=").setLong(item.getId())
							.sql(" AND ").sql(DBConstants.ItemIndexes.ITEM_PARAM).sql("=").setInt(param.getParamId()).sql("; ");
				}
				// Непустые параметры (одиночные и множественные)
				else {
					if (param.isMultiple()) {
						// Обновить каждое значение в отдельности
						MultipleParameter mp = ((MultipleParameter) param);
						byte i = (byte) 0;
						for (SingleParameter sp : mp.getValues()) {
							createSingleValueInsert(query, item, sp, i++, true);
						}
						// Удалить лишние значения, если они были (если раньше у параметра было больше значений чем сейчас)
						if (mp.valCount() < mp.initialValCount()) {
							query.sql("DELETE FROM ").sql(DataTypeMapper.getTableName(param.getType()))
									.sql(" WHERE ").sql(DBConstants.ItemIndexes.ITEM_ID).sql("=").setLong(item.getId())
									.sql(" AND ").sql(DBConstants.ItemIndexes.ITEM_PARAM).sql("=").setInt(param.getParamId())
									.sql(" AND ").sql(DBConstants.ItemIndexes.VALUE_IDX).sql(">=").setByte(mp.valCount()).sql("; ");
						}
					} else {
						// Просто вставить значение парамета без удаления и с добавлением on duplicate key update
						createSingleValueInsert(query, item, (SingleParameter) param, (byte) 0, true);
					}
				}
			}
		}
		// Выполнения запроса
		PreparedStatement insertStmt = query.prepareQuery(transaction.getConnection());
		try {
			insertStmt.executeUpdate();
		} catch (Exception e) {
			throw new EcommanderException(query.getSimpleSql(), e);
		}
	}



	private static void createSingleValueInsert(TemplateQuery query, Item item, SingleParameter param, byte valIdx, boolean needUpdate) throws SQLException {
		query.sql("INSERT INTO ").sql(DataTypeMapper.getTableName(param.getType())).sql(PARAM_INSERT_PREPARED_START);
		query
				.setLong(item.getId()).sql(",")
				.setInt(param.getParamId()).sql(",")
				.setInt(item.getTypeId()).sql(",")
				.setByte(valIdx).sql(",");
		DataTypeMapper.appendPreparedStatementInsertValue(param.getType(), query, param.getValue());
		if (needUpdate) {
			query.sql(ON_DUPLICATE_KEY_UPDATE);
			DataTypeMapper.appendPreparedStatementInsertValue(param.getType(), query, param.getValue());
			query.sql("; ");
		} else {
			query.sql("); ");
		}
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
		int itemTypeId = rs.getInt(DBConstants.Item.TYPE_ID);
		int itemWeight= rs.getInt(DBConstants.Item.INDEX_WEIGHT);
		int userId = rs.getInt(DBConstants.ItemParent.USER);
		byte groupId = rs.getByte(DBConstants.ItemParent.GROUP);
		byte status = rs.getByte(DBConstants.ItemParent.SHOW);
		byte assocId = rs.getByte(DBConstants.ItemParent.ASSOC_ID);
		String key = rs.getString(DBConstants.Item.KEY);
		String keyUnique = rs.getString(DBConstants.Item.TRANSLIT_KEY);
		long parentId = rs.getLong(parentColName);
		Timestamp timeUpdated = rs.getTimestamp(DBConstants.Item.UPDATED);
		String params = rs.getString(DBConstants.Item.PARAMS);
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemTypeId);
		return Item.existingItem(itemDesc, itemId, ItemTypeRegistry.getAssoc(assocId), parentId, userId, groupId, status,
				itemWeight, key, params, keyUnique,	timeUpdated.getTime());
	}
}