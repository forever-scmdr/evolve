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
		selectToDeleteIds.SELECT(I_ID).FROM(ITEM_TBL).WHERE().col(I_STATUS).byte_(Item.STATUS_DELETED).
				ORDER_BY(I_ID).LIMIT(toDeleteQty);
		ArrayList<Long> deletedIds = new ArrayList<>();
		try (PreparedStatement pstmt = selectToDeleteIds.prepareQuery(getTransactionContext().getConnection())) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				deletedIds.add(rs.getLong(1));
			}
		}
		Long[] deletedArray = deletedIds.toArray(new Long[0]);

		// Удаление файлов
		executeCommand(new DeleteItemsDirectoriesUnit(deletedArray));

		// Удаление из таблиц айтемов и предков (из предков удаляются записи только где удаляемый айтем - child)
		TemplateQuery delete = new TemplateQuery("Delete items");
		delete.DELETE(ITEM_TBL).WHERE().col_IN(I_ID).longIN(deletedArray).sql(";\r\n")
				.DELETE(ITEM_PARENT_TBL).WHERE().col_IN(IP_CHILD_ID).longIN(deletedArray);
		try (PreparedStatement pstmt = delete.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		}

		// Удаление из полнотекстового индекса
		if (insertIntoFulltextIndex)
		LuceneIndexMapper.getSingleton().deleteItem(deletedArray);

		deletedQty = deletedArray.length;
	}

	int getDeletedCount() {
		return deletedQty;
	}

}