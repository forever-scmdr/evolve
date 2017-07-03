package ecommander.persistence.commandunits;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import ecommander.model.Item;
import ecommander.model.ItemBasics;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
/**
 * Установить новый порядковый номер айтема в списке сабайтемов родительского айтема
 * Позиция первого айтема - 0
 * @author EEEE
 *
 */
public class SetNewItemWeightByPositionDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemParent {

	private ItemBasics item;
	private int newPosition;
	private byte assocId;
	private long parentId;
	
	public SetNewItemWeightByPositionDBUnit(ItemBasics item, int newPosition, byte assocId, long parentId) {
		this.item = item;
		this.newPosition = newPosition;
		this.assocId = assocId;
		this.parentId = parentId;
	}
	
	public void execute() throws Exception {
		testPrivileges(item);
		TemplateQuery select = new TemplateQuery("Select weight by position");
		select.SELECT(IP_WEIGHT).FROM(ITEM_PARENT_TBL).WHERE()
				.col(IP_ASSOC_ID).byte_(assocId).AND()
				.col(IP_PARENT_ID).long_(parentId).AND()
				.col(IP_PARENT_DIRECT).byte_((byte) 1)
				.ORDER_BY(IP_WEIGHT);
		if (newPosition == 0) {
			select.LIMIT(1);
		} else {
			select.LIMIT(2, newPosition - 1);
		}
		int firstWeight = 0;
		int secondWeight = 0;
		try (PreparedStatement pstmt = select.prepareQuery(getTransactionContext().getConnection())) {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				firstWeight = rs.getInt(1);
			if (rs.next())
				secondWeight = rs.getInt(1);
		}
		int newItemWeight = 0;
		boolean normalizationNeeded = false;
		if (newPosition == 0) {
			newItemWeight = firstWeight / 2;
			normalizationNeeded = firstWeight < 2;
		} else if (secondWeight == 0) {
			newItemWeight = firstWeight + Item.WEIGHT_STEP;
		} else {
			newItemWeight = firstWeight + (secondWeight - firstWeight) / 2;
			normalizationNeeded = secondWeight - firstWeight < 2;
		}
		if (normalizationNeeded) {
			SetNewItemWeightDBUnit.normalizeWeights(assocId, parentId, getTransactionContext().getConnection());
			newItemWeight = newPosition * Item.WEIGHT_STEP + Item.WEIGHT_STEP / 2;
		}
		TemplateQuery update = new TemplateQuery("Update item weight");
		update.UPDATE(ITEM_PARENT_TBL).SET().col(IP_WEIGHT).int_(newItemWeight).WHERE()
				.col(IP_ASSOC_ID).byte_(assocId).AND()
				.col(IP_PARENT_ID).long_(parentId).AND()
				.col(IP_PARENT_DIRECT).byte_((byte) 1).AND()
				.col(IP_CHILD_ID).long_(item.getId());
		try (PreparedStatement pstmt = update.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		}
	}
}
