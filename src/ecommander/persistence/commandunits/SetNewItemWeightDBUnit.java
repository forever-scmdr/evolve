package ecommander.persistence.commandunits;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import ecommander.common.ServerLogger;
import ecommander.common.exceptions.EcommanderException;
import ecommander.model.Item;
import ecommander.persistence.mappers.DBConstants;
/**
 * Поменять порядок следования сабайтемов одного айтема
 * У каждого айтема есть определенный вес, кратный 64. Минимальный вес не может быть меньше 0, а максимальный больше чем 
 * (количество сабайтемов + 1) * 64
 * Когда айтем перемещается, он вставляется между двумя айтемами, первый айтем и последний айтем - виртуальные, их нет и их
 * нельзя перемещать. Их веса соответственно 0 и (количество сабайтемов + 1) * 64
 * Новый вес айтема становится равен среднему арифметическому весов айтемов, между которыми он вставляется.
 * Если новый вес айтема отличается от соседних весов на 1 единицу, то происходит нормализация айтемов (примерный запрос ниже)
 * 
 * SET @index = 0;
 * UPDATE Item SET weight = (select @index := @index + 1) * 64 WHERE parent_id = XXX ORDER BY weight;
 * 
 * @author EEEE
 *
 */
public class SetNewItemWeightDBUnit extends DBPersistenceCommandUnit {

	private long itemId;
	private int indexBefore;
	private int indexAfter;
	private long itemParentId;
	
	public SetNewItemWeightDBUnit(long itemId, long itemParentId, int indexBefore, int indexAfter) {
		this.itemId = itemId;
		this.indexAfter = indexAfter;
		this.indexBefore = indexBefore;
		this.itemParentId = itemParentId;
	}
	
	public void execute() throws Exception {
		Statement stmt = null;
		try {
			Connection conn = getTransactionContext().getConnection();
			stmt = conn.createStatement();
			int newIndex = (indexAfter + indexBefore) / 2;
			if (newIndex >= indexAfter || newIndex <= indexBefore) {
				if (tryToNormalize(stmt, newIndex))
					return;
				throw new EcommanderException("Setting new item weight fails. (before, after, new) - " 
						+ indexBefore + ", " + indexAfter + ", " + newIndex);
			} 
			// Изменение индекса самого айтема
			String sql 
				= "UPDATE "	+ DBConstants.Item.TABLE
				+ " SET " + DBConstants.Item.INDEX_WEIGHT + "=" + newIndex
				+ " WHERE "	+ DBConstants.Item.ID + "=" + itemId;
			ServerLogger.debug(sql);
			stmt.executeUpdate(sql);
			
			// Нормализация, в случае если она необходима
			tryToNormalize(stmt, newIndex);
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}
	/**
	 * Нормализация, в случае если она необходима
	 * @param stmt
	 * @param newIndex
	 * @throws SQLException 
	 */
	private boolean tryToNormalize(Statement stmt, int newIndex) throws SQLException {
		if (newIndex - indexBefore == 1 || indexAfter - newIndex == 1) {
			String sql 
				= "SET @index = 0;"
				+ "UPDATE " + DBConstants.Item.TABLE 
				+ " SET " + DBConstants.Item.INDEX_WEIGHT + " = (SELECT @index := @index + 1) * " + Item.WEIGHT_STEP
				+ " WHERE " + DBConstants.Item.DIRECT_PARENT_ID + " = " + itemParentId
				+ " ORDER BY " + DBConstants.Item.INDEX_WEIGHT;
			ServerLogger.debug(sql);
			stmt.executeUpdate(sql);
			return true;
		}
		return false;
	}

}
