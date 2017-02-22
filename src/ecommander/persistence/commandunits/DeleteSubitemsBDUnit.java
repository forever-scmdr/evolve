package ecommander.persistence.commandunits;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ecommander.common.ServerLogger;
import ecommander.model.Item;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.LuceneIndexMapper;
import ecommander.services.filesystem.DeleteSubitemsFilesUnit;

/**
 * Удаление сабайтемов айтема
 * Удаление из таблиц параметров айтема и множественных параметров реализовано с помощью каскадного удаления внешних ключей
 * @author EEEE
 */
public class DeleteSubitemsBDUnit extends DBPersistenceCommandUnit {
	
	protected final long itemId;
	protected Item item;
	private DeleteItemBDUnit.DeleteInformer informer;
	
	private volatile DeleteSubitemsComplexBDUnit deleteComplex;

	public DeleteSubitemsBDUnit(long itemId) {
		this.itemId = itemId;
	}
	
	public DeleteSubitemsBDUnit(Item item) {
		this.item = item;
		this.itemId = item.getId();
	}
	
	public void setInformer(DeleteItemBDUnit.DeleteInformer informer) {
		this.informer = informer;
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
		// Дополнительная обработка при удалении не производится
		Statement stmt = getTransactionContext().getConnection().createStatement();
		try	{
			String sql;
			// Если айтем - простая ссылка, ничего не делается
			if (item.isReference()) {
				return;
			} else {
				if (getSubitemCount() > DeleteSubitemsComplexBDUnit.PACK_SIZE) {
					deleteComplex = new DeleteSubitemsComplexBDUnit(item);
					deleteComplex.setInformer(informer);
					executeCommand(deleteComplex);
				} else {
					// Удаляются сабайтемы из главной таблицы айтемов
					// Также удаляются все ссылки на этот айтем
					sql = "DELETE " + DBConstants.Item.TABLE
						+ " FROM " + DBConstants.Item.TABLE + ", " + DBConstants.ItemParent.TABLE
						+ " WHERE " + DBConstants.ItemParent.PARENT_ID + " = " + item.getId()
						+ " AND " + DBConstants.ItemParent.ITEM_ID + " != " + item.getId()
						+ " AND " + DBConstants.Item.ID + " = " + DBConstants.ItemParent.ITEM_ID;
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql);
				}
			}
			
			// Удаление файлов
			executeCommand(new DeleteSubitemsFilesUnit(item.getId(), item.getPredecessorsPath()));
			
			// Удаление айтема из индекса Lucene
			LuceneIndexMapper.deleteSubitems(item);
			LuceneIndexMapper.closeWriter();
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}
	
	protected int getSubitemCount() throws SQLException {
		String countSql 
			= "SELECT COUNT(" + DBConstants.ItemParent.ITEM_ID + ") FROM " + DBConstants.ItemParent.TABLE 
			+ " WHERE "	+ DBConstants.ItemParent.PARENT_ID + " = " + item.getId();
		Statement stmt = null;
		try {
			stmt = getTransactionContext().getConnection().createStatement();
			ServerLogger.debug(countSql);
			ResultSet rs = stmt.executeQuery(countSql);
			rs.next();
			return rs.getInt(1);
		} finally {
			if (stmt != null) stmt.close();
		}
	}

	protected void inform(int count) {
		if (informer != null) {
			informer.receiveDeletedCount(count);
		}
	}
	
}