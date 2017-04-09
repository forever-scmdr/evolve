package ecommander.persistence.commandunits;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import ecommander.fwk.ErrorCodes;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.EcommanderException;
import ecommander.model.Item;
import ecommander.persistence.common.TemplateQuery;
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
class SetNewItemWeightDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemParent {

	private long itemId;
	private int indexBefore;
	private int indexAfter;
	private long itemParentId;
	private byte assocId;
	
	public SetNewItemWeightDBUnit(long itemId, long itemParentId, byte assocId, int indexBefore, int indexAfter) {
		this.itemId = itemId;
		this.indexAfter = indexAfter;
		this.indexBefore = indexBefore;
		this.itemParentId = itemParentId;
		this.assocId = assocId;
	}
	
	public void execute() throws Exception {
		TemplateQuery newWeight = new TemplateQuery("Set new item weight");
		int newIndex = (indexAfter + indexBefore) / 2;
		if (newIndex >= indexAfter || newIndex <= indexBefore) {
			if (tryToNormalize(newIndex))
				return;
			throw new EcommanderException(ErrorCodes.NO_SPECIAL_ERROR, "Setting new item weight fails. (before, after, new) - "
					+ indexBefore + ", " + indexAfter + ", " + newIndex);
		}
		// Изменение индекса самого айтема
		newWeight.UPDATE(IP_TABLE).SET().col(IP_WEIGHT).setInt(newIndex)
				.WHERE().col(IP_CHILD_ID).setLong(itemId)
				.AND().col(IP_ASSOC_ID).setByte(assocId)
				.AND().col(IP_PARENT_DIRECT).setByte((byte)1)
				.AND().col(IP_PARENT_ID).setLong(itemParentId);
		try (PreparedStatement pstmt = newWeight.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		}
		// Нормализация, в случае если она необходима
		tryToNormalize(newIndex);
	}
	/**
	 * Нормализация, в случае если она необходима
	 * @param newIndex
	 * @throws SQLException
	 * @return
	 */
	private boolean tryToNormalize(int newIndex) throws SQLException {
		if (newIndex - indexBefore == 1 || indexAfter - newIndex == 1) {
			TemplateQuery normalize = new TemplateQuery("Noramlize child weight");
			normalize.sql("SET @index = 0;")
					.UPDATE(IP_TABLE).SET().col(IP_WEIGHT).sql("(SELECT @index := @index + 1) * " + Item.WEIGHT_STEP)
					.WHERE().col(IP_ASSOC_ID).setByte(assocId)
					.AND().col(IP_PARENT_DIRECT).setByte((byte)1)
					.AND().col(IP_PARENT_ID).setLong(itemParentId)
					.ORDER_BY(IP_WEIGHT);
//			String sql
//				= "SET @index = 0;"
//				+ "UPDATE " + DBConstants.Item.TABLE
//				+ " SET " + DBConstants.Item.INDEX_WEIGHT + " = (SELECT @index := @index + 1) * " + Item.WEIGHT_STEP
//				+ " WHERE " + DBConstants.Item.DIRECT_PARENT_ID + " = " + itemParentId
//				+ " ORDER BY " + DBConstants.Item.INDEX_WEIGHT;
			try (PreparedStatement pstmt = normalize.prepareQuery(getTransactionContext().getConnection())) {
				pstmt.executeUpdate();
			}
			return true;
		}
		return false;
	}

}
