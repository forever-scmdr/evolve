package ecommander.persistence.commandunits;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.model.Item;
import ecommander.model.ItemBasics;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;

import java.sql.PreparedStatement;

/**
 * Удаление или скрытие айтема
 * Created by E on 5/4/2017.
 */
public class ItemStatusDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemTbl, DBConstants.ItemParent, DBConstants.ComputedLog {

	private static final byte STATUS_TOGGLE = 100;

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
		return new ItemStatusDBUnit(Item.STATUS_HIDDEN, itemId, null);
	}

	public static ItemStatusDBUnit hide(ItemBasics item) {
		return new ItemStatusDBUnit(Item.STATUS_HIDDEN, -1, item);
	}

	public static ItemStatusDBUnit restore(long itemId) {
		return new ItemStatusDBUnit(Item.STATUS_NORMAL, itemId, null);
	}

	public static ItemStatusDBUnit restore(ItemBasics item) {
		return new ItemStatusDBUnit(Item.STATUS_NORMAL, -1, item);
	}

	public static ItemStatusDBUnit toggle(long itemId) {
		return new ItemStatusDBUnit(STATUS_TOGGLE, itemId, null);
	}

	public static ItemStatusDBUnit toggle(ItemBasics item) {
		return new ItemStatusDBUnit(STATUS_TOGGLE, -1, item);
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

		if (newStatus == STATUS_TOGGLE) {
			if (item.getStatus() == Item.STATUS_DELETED)
				return;
			newStatus = item.getStatus() == Item.STATUS_NORMAL ? Item.STATUS_HIDDEN : Item.STATUS_NORMAL;
		}

		byte primaryAssoc = ItemTypeRegistry.getPrimaryAssoc().getId();
		// Сначала обновить сам айтем
		TemplateQuery updateItemStatus = new TemplateQuery("Update item status");
		updateItemStatus.UPDATE(ITEM_TBL).SET().col(I_STATUS).byte_(newStatus)
				.WHERE().col(I_ID).long_(item.getId()).sql(";\r\n");
		// Потом обновить все сабайтемы
		updateItemStatus.UPDATE(ITEM_TBL).INNER_JOIN(ITEM_PARENT_TBL, I_ID, IP_CHILD_ID)
				.SET().col(I_STATUS).byte_(newStatus)
				.WHERE().col(IP_PARENT_ID).long_(item.getId())
				.AND().col(IP_ASSOC_ID).byte_(primaryAssoc);
		try(PreparedStatement pstmt = updateItemStatus.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		}

		// Дополнительная обработка для удаления
		if (triggerExtra && newStatus == Item.STATUS_DELETED) {
			Item itemFull = null;
			ItemType type = ItemTypeRegistry.getItemType(item.getTypeId());
			if (type.hasExtraHandlers(ItemType.Event.delete)) {
				for (ItemEventCommandFactory fac : type.getExtraHandlers(ItemType.Event.delete)) {
					if (itemFull == null) {
						itemFull = ItemQuery.loadById(item.getId(), getTransactionContext().getConnection());
					}
					PersistenceCommandUnit command = fac.createCommand(itemFull);
					executeCommandInherited(command);
				}
			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		//         Включить в список обновления предшественников айтема (и его сабайтемов)      //
		//////////////////////////////////////////////////////////////////////////////////////////

		// Если айтем меняет свой статус с нормального или на нормальный
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
					.WHERE().col(IP_CHILD_ID).long_(item.getId())
					.AND().col(IP_ASSOC_ID).byte_(primaryAssoc)
					.AND().col_IN(I_SUPERTYPE).intIN(ItemTypeRegistry.getAllComputedSupertypes())
					.ON_DUPLICATE_KEY_UPDATE(L_ITEM).sql(L_ITEM + ";\r\n")

					.INSERT_INTO(COMPUTED_LOG_TBL, L_ITEM)
					.SELECT(I + I_ID)
					.FROM(ITEM_TBL + " AS I").INNER_JOIN(ITEM_PARENT_TBL + " AS P1", I + I_ID, P1 + IP_PARENT_ID)
					.INNER_JOIN(ITEM_PARENT_TBL + " AS P2", P2 + IP_CHILD_ID, P1 + IP_CHILD_ID)
					.WHERE().col(P2 + IP_PARENT_ID).long_(item.getId())
					.AND().col_IN(P1 + IP_ASSOC_ID).byteIN(ItemTypeRegistry.getAllOtherAssocIds(primaryAssoc))
					.AND().col_IN(I + I_SUPERTYPE).intIN(ItemTypeRegistry.getAllComputedSupertypes())
					.ON_DUPLICATE_KEY_UPDATE(L_ITEM).sql(L_ITEM);
			try(PreparedStatement pstmt = logInsert.prepareQuery(getTransactionContext().getConnection())) {
				pstmt.executeUpdate();
			}
		}
	}
}
