package ecommander.persistence.mappers;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.ErrorCodes;
import ecommander.fwk.MysqlConnector;
import ecommander.model.*;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.common.TransactionContext;

import java.sql.*;
import java.util.ArrayList;

/**
 * Выполняет различные операции с Item и БД
 * @author EEEE
 *
 */
public class ItemMapper implements DBConstants.ItemTbl, DBConstants {

	public enum Mode {
		INSERT, // вставка в таблицу (без изменения и удаления)
		UPDATE, // изменение существующих данных (удаление и вставка)
		FORCE_UPDATE // принудительное изменение (даже если параметры не менялись)
	}

	private static final String PARAM_INSERT_PREPARED_START 
		= " ("
		+ ItemIndexes.II_ITEM_ID + ", "
		+ ItemIndexes.II_PARAM + ", "
		+ ItemIndexes.II_ITEM_TYPE + ", "
		+ ItemIndexes.II_VALUE
		+ ") VALUES (";
	
	private static final String ON_DUPLICATE_KEY_UPDATE
		= ") ON DUPLICATE KEY UPDATE " + DBConstants.ItemIndexes.II_VALUE + " = ";

	/**
	 * Сохраняет все параметры айтема в индекс.
	 * Удаление старых параметров происходит только в том случе, если это надо (параметры были удалены).
	 * Обновление параметров также происходит только если это надо (параметры поменялись)
	 * @param item
	 * @param mode
	 * @param transaction
	 * @throws SQLException
	 * @throws EcommanderException
	 */
	public static void insertItemParametersToIndex(Item item, Mode mode, TransactionContext transaction) throws SQLException, EcommanderException {
		if (!item.hasChanged() && mode != Mode.FORCE_UPDATE)
			return;
		TemplateQuery query;
		// Если айтем новый, то не нужно удалять старые значения и обновлять существующие
		if (mode == Mode.INSERT) {
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
				if (!param.hasChanged() && mode == Mode.UPDATE)
					continue;
				// Удалить старое значение
				query.DELETE_FROM_WHERE(DataTypeMapper.getTableName(param.getType()))
						.col(ItemIndexes.II_ITEM_ID).long_(item.getId()).AND()
						.col(ItemIndexes.II_PARAM).int_(param.getParamId()).sql("; ");
				// Непустые параметры (одиночные и множественные)
				if (!param.isEmpty()) {
					if (param.isMultiple()) {
						// Обновить каждое значение в отдельности
						MultipleParameter mp = ((MultipleParameter) param);
						for (SingleParameter sp : mp.getValues()) {
							createSingleValueInsert(query, item, sp, false);
						}
					} else {
						// Просто вставить значение парамета без удаления и с добавлением on duplicate key update
						createSingleValueInsert(query, item, (SingleParameter) param, false);
					}
				}
			}
		}
		// Выполнения запроса
		if (!query.isEmpty()) {
			try (PreparedStatement insertStmt = query.prepareQuery(transaction.getConnection())) {
				insertStmt.executeUpdate();
			} catch (Exception e) {
				throw new EcommanderException(ErrorCodes.UNABLE_TO_SAVE_ITEM_INDEX, query.getSimpleSql(), e);
			}
		}
	}



	private static void createSingleValueInsert(TemplateQuery query, Item item, SingleParameter param, boolean needUpdate) throws SQLException {
		query.INSERT_INTO(DataTypeMapper.getTableName(param.getType())).sql(PARAM_INSERT_PREPARED_START);
		query
				.long_(item.getId()).sql(",")
				.int_(param.getParamId()).sql(",")
				.int_(item.getTypeId()).sql(",");
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
	 * Загрузить сведения об айтеме по его ID
	 * @param itemId
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static ItemBasics loadItemBasics(long itemId, Connection conn) throws SQLException {
		TemplateQuery query = new TemplateQuery("Select item basics");
		query.SELECT(I_ID, I_TYPE_ID, I_KEY, I_GROUP, I_USER, I_STATUS, I_PROTECTED)
				.FROM(ITEM_TBL).WHERE().col(I_ID).long_(itemId);
		try (PreparedStatement pstmt = query.prepareQuery(conn)) {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return new DefaultItemBasics(
						rs.getLong(1), rs.getInt(2), rs.getString(3),
						rs.getByte(4), rs.getInt(5), rs.getByte(6),
						rs.getBoolean(7));
			} else {
				return null;
			}
		}
	}

	/**
	 * Создать айтем из резалт сета
	 * @param rs
	 * @param contextAssocId
	 * @param contextParentId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static Item buildItem(ResultSet rs, byte contextAssocId, long contextParentId) throws Exception {
		long itemId = rs.getLong(I_ID);
		int itemTypeId = rs.getInt(I_TYPE_ID);
		String key = rs.getString(I_KEY);
		String keyUnique = rs.getString(I_T_KEY);
		Timestamp timeUpdated = rs.getTimestamp(I_UPDATED);
		byte status = rs.getByte(I_STATUS);
		byte groupId = rs.getByte(I_GROUP);
		int userId = rs.getByte(I_USER);
		boolean filesProtected = rs.getBoolean(I_PROTECTED);
		String params = rs.getString(I_PARAMS);
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemTypeId);
		return Item.existingItem(itemDesc, itemId, ItemTypeRegistry.getAssoc(contextAssocId), contextParentId, userId, groupId, status,
				key, params, keyUnique, timeUpdated.getTime(), filesProtected);
	}

	/**
	 * Создать айтем из резалт сета
	 * @param rs
	 * @param contextAssocId
	 * @param contextParentIdColName
	 * @return
	 * @throws Exception
	 */
	public static Item buildItem(ResultSet rs, byte contextAssocId, String contextParentIdColName) throws Exception {
		return buildItem(rs, contextAssocId, rs.getLong(contextParentIdColName));
	}

	/**
	 * Загрузать определенное количесвто айтемов определенного типа.
	 * Метод нужен для загрузки большого массива данных последовательно небольшими порциями, поэтому айтемы
	 * загружаются в порядке следования их ID, и в метод передается ID последнего загруженного айтема
	 * @param itemName
	 * @param limit
	 * @param startFromId
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<Item> loadByName(String itemName, int limit, long startFromId) throws Exception {
		return loadByTypeId(ItemTypeRegistry.getItemType(itemName).getTypeId(), limit, startFromId);
	}

	/**
	 * Загрузать определенное количесвто айтемов определенного типа.
	 * Метод нужен для загрузки большого массива данных последовательно небольшими порциями, поэтому айтемы
	 * загружаются в порядке следования их ID, и в метод передается ID последнего загруженного айтема
	 * @param itemId
	 * @param limit
	 * @param moreThanId
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<Item> loadByTypeId(int itemId, int limit, long moreThanId) throws Exception {
		ArrayList<Item> result = new ArrayList<>();
		// Полиморфная загрузка
		TemplateQuery select = new TemplateQuery("Select items for indexing");
		Integer[] extenders = ItemTypeRegistry.getBasicItemExtendersIds(itemId);
		select.SELECT("*").FROM(ITEM_TBL).WHERE().col_IN(I_TYPE_ID).intIN(extenders).AND()
				.col(I_ID, ">").long_(moreThanId).ORDER_BY(I_ID).LIMIT(limit);
		try (Connection conn = MysqlConnector.getConnection();
			 PreparedStatement pstmt = select.prepareQuery(conn)) {
			ResultSet rs = pstmt.executeQuery();
			// Создание айтемов
			while (rs.next()) {
				result.add(ItemMapper.buildItem(rs, ItemTypeRegistry.getPrimaryAssocId(), 0L));
			}
		}
		return result;
	}
}