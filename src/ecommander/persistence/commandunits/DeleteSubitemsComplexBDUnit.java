package ecommander.persistence.commandunits;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.persistence.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.DBConstants;

/**
 * Удаление сабайтемов айтема
 * Удаление из таблиц параметров айтема и множественных параметров реализовано с помощью каскадного удаления внешних ключей
 * @author EEEE
 */
public class DeleteSubitemsComplexBDUnit extends DeleteItemBDUnit {
	
	static final int PACK_SIZE = 100;

	public DeleteSubitemsComplexBDUnit(long itemId) {
		super(itemId);
	}
	
	public DeleteSubitemsComplexBDUnit(Item item) {
		super(item);
	}
	
	public void execute() throws Exception {
		if (item == null) {
			item = ItemQuery.loadById(itemId, getTransactionContext().getConnection());
			if (item == null)
				return;
		}
		////// Проверка прав пользователя на айтем //////
		//
		testPrivileges(item);
		//
		/////////////////////////////////////////////////
		Statement stmt = getTransactionContext().getConnection().createStatement();		
		try	{
			int deletedCount = 0;
			
			while (true) {
				String sql 
					= "SELECT " + DBConstants.ItemParent.ITEM_ID + ", " + DBConstants.ItemParent.PARENT_LEVEL 
					+ " FROM " + DBConstants.ItemParent.TABLE 
					+ " WHERE "+ DBConstants.ItemParent.PARENT_ID + " = " + itemId 
					+ " AND " + DBConstants.ItemParent.ITEM_ID + " != " + itemId
					+ " ORDER BY " + DBConstants.ItemParent.PARENT_LEVEL + " DESC LIMIT " + PACK_SIZE;
				ServerLogger.debug(sql);
				ResultSet rs = stmt.executeQuery(sql);
				ArrayList<Long> idsToDelete = new ArrayList<Long>();
				while (rs.next()) {
					long id = rs.getLong(1);
					idsToDelete.add(id);
				}
				rs.close();

				if (idsToDelete.size() == 0)
					break;
				
				DelayedTransaction transaction = new DelayedTransaction(getTransactionContext().getInitiator());
				String partialDelete = 
						"DELETE FROM " + DBConstants.Item.TABLE + " WHERE " + DBConstants.Item.ID + " IN (";
				StringBuilder ids = new StringBuilder();
				for (Long id : idsToDelete) {
					ids.append(id).append(',');
				}
				ids.setCharAt(ids.length() - 1, ')');
				transaction.addCommandUnit(new SQLCommandUnit(partialDelete + ids.toString()));
				transaction.execute();
				deletedCount += idsToDelete.size();
				inform(deletedCount);
			}
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

}