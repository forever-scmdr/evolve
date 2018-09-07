package ecommander.persistence.commandunits;

import ecommander.model.Item;
import ecommander.persistence.itemquery.ItemQuery;

/**
 * Перемещение айтема (прикрепление айтема к другому родителю)
 * - нельзя перемещать айтемы в собственные сабайтемы (также в самого себя и в прямого родителя)
 * - нельзя перемещать айтемы в айтемы, не содержашие совместимых по типу сабайтемов
 * @author EEEE
 *
 */
public class MoveItemDBUnit extends DBPersistenceCommandUnit {

	private Item item;
	private Item newParent;
	private long newParentId;
	private long itemToMoveId;
	
	public MoveItemDBUnit(Item itemToMove, long newParentId) {
		this.item = itemToMove;
		this.newParentId = newParentId;
	}
	
	public MoveItemDBUnit(Item itemToMove, Item newParent) {
		this.item = itemToMove;
		this.newParent = newParent;
	}
	
	public MoveItemDBUnit(long itemToMoveId, long newParentId) {
		this.itemToMoveId = itemToMoveId;
		this.newParentId = newParentId;
	}
	
	public void execute() throws Exception {
		// Загрузка айтемов
		if (item == null)
			item = ItemQuery.loadById(itemToMoveId, getTransactionContext().getConnection());
		if (newParent == null)
			newParent = ItemQuery.loadById(newParentId, getTransactionContext().getConnection());

		// Копирование
		executeCommand(new CopyItemDBUnit(item, newParent).ignoreFileErrors(ignoreFileErrors));

		// Удаление
		executeCommand(ItemStatusDBUnit.delete(item).ignoreFileErrors(ignoreFileErrors));
	}

}