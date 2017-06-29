package ecommander.persistence.commandunits;

import ecommander.filesystem.DeleteItemsDirectoriesUnit;
import ecommander.model.Item;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.LuceneIndexMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Фактическое удалелние записей ранее удаленных айтемов из таблиц. Также удаляются файлы этих айтемов
 * Удаление из таблиц параметров айтема реализовано с помощью каскадного удаления внешних ключей
 * @author EEEE
 */
public class CleanDeletedItemsDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemTbl, DBConstants.ItemParent {
	
	private final int toDeleteQty;
	private int deletedQty;

	public CleanDeletedItemsDBUnit(int toDeleteQty) {
		this.toDeleteQty = toDeleteQty;
	}

	public void execute() throws Exception {

		// Загрузка ID для удаления
		TemplateQuery selectToDeleteIds = new TemplateQuery("Select " + toDeleteQty + " items to delete");
		selectToDeleteIds.SELECT(I_ID).FROM(ITEM).WHERE().col(I_STATUS).setByte(Item.STATUS_DELETED).
				ORDER_BY(I_ID).LIMIT(toDeleteQty);
		ArrayList<Long> deletedIds = new ArrayList<>();
		try (PreparedStatement pstmt = selectToDeleteIds.prepareQuery(getTransactionContext().getConnection())) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				deletedIds.add(rs.getLong(1));
		}
		Long[] deletedArray = new Long[deletedIds.size()];

		// Удаление файлов
		executeCommand(new DeleteItemsDirectoriesUnit(deletedArray));

		// Удаление из таблиц айтемов и предков (из предков удаляются записи только где удаляемый айтем - child)
		TemplateQuery delete = new TemplateQuery("Delete items");
		delete.DELETE(ITEM).WHERE().col(I_ID, " IN").longArrayIN(deletedArray).sql(";\r\n")
				.DELETE(ITEM_PARENT).WHERE().col(IP_CHILD_ID, " IN").longArrayIN(deletedArray);
		try (PreparedStatement pstmt = delete.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		}

		// Удаление из полнотекстового индекса
		for (Long deletedId : deletedIds) {
			LuceneIndexMapper.deleteItem(deletedId, false);
		}

		deletedQty = deletedIds.size();
	}

	int getDeletedCount() {
		return deletedQty;
	}

}