package ecommander.persistence.commandunits;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.LuceneIndexMapper;
import ecommander.filesystem.DeleteItemFilesUnit;

/**
 * Удаление айтема
 * Удаление из таблиц параметров айтема и множественных параметров реализовано с помощью каскадного удаления внешних ключей
 * @author EEEE
 */
public class DeleteItemBDUnit extends DBPersistenceCommandUnit {
	
	public static interface DeleteInformer {
		void receiveDeletedCount(int deletedCount);
	}
	
	protected final long itemId;
	protected Item item;
	private DeleteInformer informer;
	
	private volatile DeleteSubitemsComplexBDUnit deleteComplex;

	public DeleteItemBDUnit(long itemId) {
		this.itemId = itemId;
	}
	
	public DeleteItemBDUnit(Item item) {
		this.item = item;
		this.itemId = item.getId();
	}
	
	public void setInformer(DeleteInformer informer) {
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
		// Выполнение команды дополнительной обработки (до удаление айтема)
		if (item.getItemType().hasExtraHandlers()) {
			for (ItemEventCommandFactory fac : item.getItemType().getExtraHandlers()) {
				PersistenceCommandUnit command = fac.createDeleteCommand(item);
				if (command != null)
					executeCommand(command);				
			}
		}
		Statement stmt = getTransactionContext().getConnection().createStatement();
		try	{
			String sql;
			// Если айтем - простая ссылка, то удаляется сам айтем, а не его сабайтемы
			if (item.isReference()) {
				// удаление из таблицы айтема
				sql = "DELETE " + DBConstants.Item.TABLE + " FROM " + DBConstants.Item.TABLE 
					+ " WHERE " + DBConstants.Item.ID + " = " + item.getId();
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
				// нужно удаление из таблицы сабайтемов, т.к. сейчас в этой таблице уже нет 
				// индекса по ID айтема (есть только по REF_ID)
				sql = "DELETE FROM " + DBConstants.ItemParent.TABLE 
					+ " WHERE " + DBConstants.ItemParent.REF_ID + " = " + item.getRefId() 
					+ " AND " + DBConstants.ItemParent.ITEM_ID + " = " + item.getId();
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
			} else {
				if (getSubitemCount() > DeleteSubitemsComplexBDUnit.PACK_SIZE) {
					deleteComplex = new DeleteSubitemsComplexBDUnit(item);
					deleteComplex.ignoreUser(ignoreUser);
					deleteComplex.setInformer(informer);
					executeCommand(deleteComplex);
				} else {
					// Удаляются сабайтемы из главной таблицы айтемов
					// Также удаляются все ссылки на этот айтем
					sql = "DELETE " + DBConstants.Item.TABLE
						+ " FROM " + DBConstants.Item.TABLE + ", " + DBConstants.ItemParent.TABLE
						+ " WHERE " + DBConstants.ItemParent.PARENT_ID + " = " + item.getId()
						+ " AND " + DBConstants.Item.ID + " = " + DBConstants.ItemParent.ITEM_ID;
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql);
				}
				
				// Удаляется сам айтем
				sql = "DELETE " + DBConstants.Item.TABLE + " FROM " + DBConstants.Item.TABLE 
					+ " WHERE " + DBConstants.Item.REF_ID + " = " + item.getId();
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
			}
			
			// Удаление файлов
			executeCommand(new DeleteItemFilesUnit(item.getId(), item.getPredecessorsPath()));
			
			// Удаление айтема из индекса Lucene
			if (insertIntoFulltextIndex)
				LuceneIndexMapper.deleteItem(item, closeLuceneWriter);
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