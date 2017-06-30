package ecommander.persistence.commandunits;

import ecommander.model.*;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;

import java.sql.PreparedStatement;

/**
 * Удаление или скрытие айтема
 * Created by E on 5/4/2017.
 */
public class ItemStatusDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemTbl, DBConstants.ItemParent, DBConstants.ComputedLog {

	private byte newStatus;
	private long itemId;
	private ItemBasics item;

	public ItemStatusDBUnit(byte status, long itemId, ItemBasics item) {
		this.newStatus = status;
		this.itemId = itemId;
		this.item = item;
	}

	public static ItemStatusDBUnit delete(long itemId) {
		return new ItemStatusDBUnit(Item.STATUS_DELETED, itemId, null);
	}

	public static ItemStatusDBUnit delete(ItemBasics item) {
		return new ItemStatusDBUnit(Item.STATUS_DELETED, -1, item);
	}

	public static ItemStatusDBUnit hide(long itemId) {
		return new ItemStatusDBUnit(Item.STATUS_NIDDEN, itemId, null);
	}

	public static ItemStatusDBUnit hide(ItemBasics item) {
		return new ItemStatusDBUnit(Item.STATUS_NIDDEN, -1, item);
	}

	public static ItemStatusDBUnit restore(long itemId) {
		return new ItemStatusDBUnit(Item.STATUS_NORMAL, itemId, null);
	}

	public static ItemStatusDBUnit restore(ItemBasics item) {
		return new ItemStatusDBUnit(Item.STATUS_NORMAL, -1, item);
	}

	@Override
	public void execute() throws Exception {
		if (item == null)
			item = ItemMapper.loadItemBasics(itemId, getTransactionContext().getConnection());
		if (item == null)
			return;
		if (item.getStatus() == newStatus)
			return;
		testPrivileges(item);

		byte primaryAssoc = ItemTypeRegistry.getPrimaryAssoc().getId();
		// Сначала обновить сам айтем
		TemplateQuery updateItemStatus = new TemplateQuery("Update item status");
		updateItemStatus.UPDATE(ITEM_TBL).SET().col(I_STATUS).setByte(newStatus)
				.WHERE().col(I_ID).setLong(item.getId()).sql(";\r\n");
		// Потом обновить все сабайтемы
		updateItemStatus.UPDATE(ITEM_TBL).INNER_JOIN(ITEM_PARENT_TBL, I_ID, IP_CHILD_ID)
				.SET().col(I_STATUS).setByte(newStatus)
				.WHERE().col(IP_PARENT_ID).setLong(item.getId())
				.AND().col(IP_ASSOC_ID).setByte(primaryAssoc);
		try(PreparedStatement pstmt = updateItemStatus.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		//         Включить в список обновления предшественников айтема (и его сабайтемов)      //
		//////////////////////////////////////////////////////////////////////////////////////////

		// Если айтем меняет свой статус с нормлаьного или на нормальный
		boolean insertComputedToUpdateLog = newStatus == Item.STATUS_NORMAL || item.getStatus() == Item.STATUS_NORMAL;
		// а также если в модели есть computed-параметры в принципе и если нет игнорирования computed-параметров
		insertComputedToUpdateLog &= processComputed && ItemTypeRegistry.hasComputedItems();
		if (insertComputedToUpdateLog) {
			TemplateQuery logInsert = new TemplateQuery("Insert into update log");
			final String I = "I.";
			final String P1 = "P1.";
			final String P2 = "P2.";
			logInsert
					.INSERT_INTO(COMPUTED_LOG_TBL, L_ITEM)
					.SELECT(I_ID).FROM(ITEM_TBL).INNER_JOIN(ITEM_PARENT_TBL, I_ID, IP_PARENT_ID)
					.WHERE().col(IP_CHILD_ID).setLong(item.getId())
					.AND().col(IP_ASSOC_ID).setByte(primaryAssoc)
					.AND().col(I_SUPERTYPE, " IN").intArrayIN(ItemTypeRegistry.getAllComputedSupertypes())
					.ON_DUPLICATE_KEY_UPDATE(L_ITEM).sql(L_ITEM + ";\r\n")

					.INSERT_INTO(COMPUTED_LOG_TBL, L_ITEM)
					.SELECT(I + I_ID)
					.FROM(ITEM_TBL + " AS I").INNER_JOIN(ITEM_PARENT_TBL + " AS P1", I + I_ID, P1 + IP_PARENT_ID)
					.INNER_JOIN(ITEM_PARENT_TBL + " AS P2", P2 + IP_CHILD_ID, P1 + IP_CHILD_ID)
					.WHERE().col(P2 + IP_PARENT_ID).setLong(item.getId())
					.AND().col(P1 + IP_ASSOC_ID, " IN").byteArrayIN(ItemTypeRegistry.getAllOtherAssocIds(primaryAssoc))
					.AND().col(I + I_SUPERTYPE, " IN").setIntArray(ItemTypeRegistry.getAllComputedSupertypes())
					.ON_DUPLICATE_KEY_UPDATE(L_ITEM).sql(L_ITEM);
			try(PreparedStatement pstmt = logInsert.prepareQuery(getTransactionContext().getConnection())) {
				pstmt.executeUpdate();
			}
		}
	}
}
