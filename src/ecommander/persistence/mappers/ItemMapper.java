package ecommander.persistence.mappers;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.ErrorCodes;
import ecommander.model.*;
import ecommander.persistence.common.TransactionContext;
import ecommander.persistence.common.TemplateQuery;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Выполняет различные операции с Item и БД
 * @author EEEE
 *
 */
public class ItemMapper implements DBConstants.ItemIndexes, DBConstants {

	private static final String PARAM_INSERT_PREPARED_START 
		= " ("
		+ ITEM_ID + ", "
		+ ITEM_PARAM + ", "
		+ ITEM_TYPE + ", "
		+ VALUE
		+ ") VALUES (";
	
	private static final String ON_DUPLICATE_KEY_UPDATE
		= ") ON DUPLICATE KEY UPDATE " + DBConstants.ItemIndexes.VALUE + " = ";

	/**
	 * Сохраняет все параметры айтема в индекс.
	 * Удаление старых параметров происходит только в том случе, если это надо (параметры были удалены).
	 * Обновление параметров также происходит только если это надо (параметры поменялись)
	 * @param item
	 * @param isNew
	 * @param transaction
	 * @throws SQLException
	 * @throws EcommanderException
	 */
	public static void insertItemParametersToIndex(Item item, boolean isNew, TransactionContext transaction) throws SQLException, EcommanderException {
		if (!item.hasChanged())
			return;
		TemplateQuery query;
		// Если айтем новый, то не нужно удалять старые значения и обновлять существующие
		if (isNew) {
			query = new TemplateQuery("Item index insert new");
			for (Parameter param : item.getAllParameters()) {
				if (!param.isEmpty()) {
					if (param.isMultiple()) {
						for (SingleParameter singleParam : ((MultipleParameter)param).getValues()) {
							createSingleValueInsert(query, item, singleParam, false);
						}
					} else {
						createSingleValueInsert(query, item, (SingleParameter)param, false);
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
							.sql(" WHERE ").sql(ITEM_ID).sql("=").setLong(item.getId())
							.sql(" AND ").sql(ITEM_PARAM).sql("=").setInt(param.getParamId()).sql("; ");
				}
				// Непустые параметры (одиночные и множественные)
				else {
					if (param.isMultiple()) {
						// Обновить каждое значение в отдельности
						MultipleParameter mp = ((MultipleParameter) param);
						ArrayList<String> mpVals = new ArrayList<>();
						for (SingleParameter sp : mp.getValues()) {
							createSingleValueInsert(query, item, sp, true);
							mpVals.add(sp.outputValue());
						}
						// Удалить лишние значения, если они были
						query.DELETE(DataTypeMapper.getTableName(param.getType()))
								.WHERE()
								.col(ITEM_ID).setLong(item.getId()).AND()
								.col(ITEM_PARAM).setInt(param.getParamId()).AND()
								.col(VALUE, " NOT IN (");
						DataTypeMapper.appendPreparedStatementRequestValues(param.getType(), query, mpVals);
						query.sql(");");
					} else {
						// Просто вставить значение парамета без удаления и с добавлением on duplicate key update
						createSingleValueInsert(query, item, (SingleParameter) param, true);
					}
				}
			}
		}
		// Выполнения запроса
		PreparedStatement insertStmt = query.prepareQuery(transaction.getConnection());
		try {
			insertStmt.executeUpdate();
		} catch (Exception e) {
			throw new EcommanderException(ErrorCodes.UNABLE_TO_SAVE_ITEM_INDEX, query.getSimpleSql(), e);
		}
	}



	private static void createSingleValueInsert(TemplateQuery query, Item item, SingleParameter param, boolean needUpdate) throws SQLException {
		query.sql("INSERT INTO ").sql(DataTypeMapper.getTableName(param.getType())).sql(PARAM_INSERT_PREPARED_START);
		query
				.setLong(item.getId()).sql(",")
				.setInt(param.getParamId()).sql(",")
				.setInt(item.getTypeId()).sql(",");
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
	 * @param contextAssocId
	 * @param contextParentId
	 * @param userId
	 * @param groupId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static Item buildItem(ResultSet rs, byte contextAssocId, long contextParentId, int userId, byte groupId) throws Exception {
		long itemId = rs.getLong(ItemTbl.ID);
		int itemTypeId = rs.getInt(ItemTbl.TYPE_ID);
		String key = rs.getString(ItemTbl.KEY);
		String keyUnique = rs.getString(ItemTbl.TRANSLIT_KEY);
		Timestamp timeUpdated = rs.getTimestamp(ItemTbl.UPDATED);
		byte status = rs.getByte(ItemTbl.DELETED);
		boolean filesProtected = rs.getBoolean(ItemTbl.PROTECTED);
		String params = rs.getString(ItemTbl.PARAMS);
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemTypeId);
		return Item.existingItem(itemDesc, itemId, ItemTypeRegistry.getAssoc(contextAssocId), contextParentId, userId, groupId, status,
				key, params, keyUnique, timeUpdated.getTime(), filesProtected);
	}
}