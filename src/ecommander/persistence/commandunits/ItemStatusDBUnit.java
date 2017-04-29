package ecommander.persistence.commandunits;

import ecommander.model.Item;
import ecommander.model.ItemBasics;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;

import java.sql.PreparedStatement;

/**
 * Удаление или скрытие айтема
 * Created by E on 5/4/2017.
 */
public class ItemStatusDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemTbl, DBConstants.ItemParent {

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
		testPrivileges(item);
		// Сначала обновить сам айтем
		TemplateQuery updateItemStatus = new TemplateQuery("Update item status");
		updateItemStatus.UPDATE(I_TABLE).SET().col(I_STATUS).setByte(newStatus)
				.WHERE().col(I_ID).setLong(item.getId()).sql(";\r\n");
		// Потом обновить все сабайтемы
		updateItemStatus.UPDATE(I_TABLE).INNER_JOIN(IP_TABLE, I_ID, IP_CHILD_ID)
				.SET().col(I_STATUS).setByte(newStatus)
				.WHERE().col(IP_PARENT_ID).setLong(item.getId());
		try(PreparedStatement pstmt = updateItemStatus.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		}
	}
}
