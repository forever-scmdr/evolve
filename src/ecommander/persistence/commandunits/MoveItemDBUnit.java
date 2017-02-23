package ecommander.persistence.commandunits;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.EcommanderException;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.filesystem.FilePersistenceCommandUnit;

/**
 * Перемещение айтема (прикрепление айтема к другому родителю)
 * - нельзя перемещать айтемы в собственные сабайтемы (также в самого себя и в прямого родителя)
 * - нельзя перемещать айтемы в айтемы, не содержашие совместимых по типу сабайтемов
 * @author EEEE
 *
 */
public class MoveItemDBUnit extends DBPersistenceCommandUnit {
	
	private static class MoveDirectoryFileUnit extends FilePersistenceCommandUnit {

		private Item itemToMove;
		private Item newParent;
		
		public MoveDirectoryFileUnit(Item itemToMove, Item newParent) {
			this.itemToMove = itemToMove;
			this.newParent = newParent;
		}
		
		public void execute() throws Exception {
			File newDirectoryName = new File(AppContext.getFilesDirPath() + newParent.getPredecessorsAndSelfPath() + itemToMove.getId());
			File directoryToMove = new File(AppContext.getFilesDirPath() + itemToMove.getPredecessorsPath() + itemToMove.getId());
			// Перемещать только ести есть файлы, которые надо перемещать
			if (directoryToMove.exists()) {
				String parentDirectoryName = createItemFileDirectoryName(newParent.getId(), newParent.getPredecessorsPath());
				File parentDirectory = new File(parentDirectoryName);
				// Создание новой директории, если такой еще нет
				if (!parentDirectory.exists())
					new File(AppContext.getFilesDirPath() + parentDirectoryName).mkdirs();
				if (!directoryToMove.renameTo(newDirectoryName)) {
					throw new EcommanderException("Directory '" + directoryToMove.getAbsolutePath() + "' can not be moved to '"
							+ newDirectoryName.getAbsolutePath() + "'");
				}				
			}
		}

		public void rollback() throws Exception {
			
		}
	}
	
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
		Statement stmt = null;
		try	{
			Connection conn = getTransactionContext().getConnection();
			stmt = conn.createStatement();
			
			// Загрузка айтемов
			if (item == null)
				item = ItemQuery.loadById(itemToMoveId, getTransactionContext().getConnection());
			if (newParent == null)
				newParent = ItemQuery.loadById(newParentId, getTransactionContext().getConnection());
			////// Проверка прав пользователя на айтем //////
			//
			testPrivileges(item);
			testPrivileges(newParent);
			//
			/////////////////////////////////////////////////
			
			
			// Проверка, можно ли копировать
			String selectSql 
				= "SELECT " + DBConstants.ItemParent.REF_ID + " FROM " + DBConstants.ItemParent.TABLE 
				+ " WHERE (" + DBConstants.ItemParent.PARENT_ID  + " = " + item.getId()
				+ " AND " + DBConstants.ItemParent.REF_ID + " = " + newParent.getId() + ")";
			ServerLogger.debug(selectSql);
			ResultSet rs = stmt.executeQuery(selectSql);
			if (rs.next() || newParent.getId() == item.getId() || newParent.getId() == item.getDirectParentId())
				throw new EcommanderException("Unable to move item ID " + item.getId() + " to it's subitem (ID " + newParent.getId() + ").");
			boolean possibleSubitem = false;
			for (String subitemName : newParent.getItemType().getAllSubitems()) {
				possibleSubitem |= ItemTypeRegistry.getItemExtenders(subitemName).contains(item.getTypeName());
			}
			if (!possibleSubitem)
				throw new EcommanderException("Unable to move item ID " + item.getId() + " to item ID " + newParent.getId()
						+ ". Incompatible types.");
			
			// Определение нового порядкового номера айтема
			int weight = SaveNewItemDBUnit.findNewWeight(conn, newParent.getId());
			
			// Если проверки прошли успешно - продолжение
			// Шаг 1. - Удалить старые связи всех родителей айтема со всеми потомками айтема (а также с самим айтемом)
			String sql
				= "DELETE " + DBConstants.ItemParent.TABLE + " FROM " + DBConstants.ItemParent.TABLE 
				+ ", (SELECT " + DBConstants.ItemParent.PARENT_ID + " AS PARENT FROM " + DBConstants.ItemParent.TABLE 
				+ " WHERE " + DBConstants.ItemParent.REF_ID + " = " + item.getId() 
				+ " AND " + DBConstants.ItemParent.PARENT_ID + " != " + item.getId()
				+ ") AS P, (SELECT " + DBConstants.ItemParent.REF_ID + " AS REF FROM " + DBConstants.ItemParent.TABLE 
				+ " WHERE " + DBConstants.ItemParent.PARENT_ID + " = " + item.getId() 
				+ " AND " + DBConstants.ItemParent.REF_ID + " != " + item.getId() 
				+ " UNION SELECT " + item.getId() 
				+ ") AS R WHERE " + DBConstants.ItemParent.PARENT_ID + " = PARENT AND " + DBConstants.ItemParent.REF_ID 
				+ " = REF";
			ServerLogger.debug(sql);
			stmt.executeUpdate(sql);

			// Шаг 2. - Создать новые связи всех новых родителей айтема со всеми потомками айтема (а также с самим айтемом)
			// " AND " + DBConstants.ItemParent.ITEM_ID + " = " + newParentId // для производительности (т.к. по REF_ID есть индекс)
			sql
				= "INSERT INTO " + DBConstants.ItemParent.TABLE + "("
				+ DBConstants.ItemParent.PARENT_ID + ", "
				+ DBConstants.ItemParent.ITEM_ID + ", " 
				+ DBConstants.ItemParent.REF_ID + ", "
				+ DBConstants.ItemParent.ITEM_TYPE + ", "
				+ DBConstants.ItemParent.PARENT_LEVEL 
				+ ") SELECT T3." 
				+ DBConstants.ItemParent.PARENT_ID 
				+ ", T1." + DBConstants.ItemParent.ITEM_ID 
				+ ", T1." + DBConstants.ItemParent.REF_ID 
				+ ", T1." + DBConstants.ItemParent.ITEM_TYPE 
				+ ", T1." + DBConstants.ItemParent.PARENT_LEVEL + " + T3." + DBConstants.ItemParent.PARENT_LEVEL + " + 1 FROM (SELECT " 
				+ DBConstants.ItemParent.PARENT_ID + ", " 
				+ DBConstants.ItemParent.PARENT_LEVEL
				+ " FROM " + DBConstants.ItemParent.TABLE 
				+ " WHERE " + DBConstants.ItemParent.REF_ID + " = " + newParent.getId()
				+ " AND " + DBConstants.ItemParent.ITEM_ID + " = " + newParent.getId()
				+ " UNION SELECT " + newParent.getId() + ", 0) AS T3, (SELECT " 
				+ DBConstants.ItemParent.ITEM_ID + ", " 
				+ DBConstants.ItemParent.REF_ID + ", " 
				+ DBConstants.ItemParent.ITEM_TYPE + ", " 
				+ DBConstants.ItemParent.PARENT_LEVEL 
				+ " FROM " + DBConstants.ItemParent.TABLE 
				+ " WHERE " + DBConstants.ItemParent.PARENT_ID + " = " + item.getId()
				+ " UNION SELECT " + item.getId() + ", " + item.getId() + ", " + item.getTypeId() + ", 0) AS T1";
			ServerLogger.debug(sql);
			stmt.executeUpdate(sql);
			
			// Шаг 3. - Поменять DIRECT_PARENT_ID, CHILD_INDEX и PRED_ID_PATH в таблице айтема
			sql
				= "UPDATE " + DBConstants.Item.TABLE + " SET " + DBConstants.Item.DIRECT_PARENT_ID + " = " + newParent.getId()
				+ ", " + DBConstants.Item.INDEX_WEIGHT + " = " + weight + ", " + DBConstants.Item.PRED_ID_PATH + " = '"
				+ newParent.getPredecessorsAndSelfPath() + "' WHERE " + DBConstants.Item.ID + " = " + item.getId();
			ServerLogger.debug(sql);
			stmt.executeUpdate(sql);
			
			// Шаг 4. - Поменять пути к файлам айтемов потомков перемещаемого айтема в таблице айтемов
			sql
				= "UPDATE " + DBConstants.Item.TABLE + ", " + DBConstants.ItemParent.TABLE + " SET " 
				+ DBConstants.Item.PRED_ID_PATH + " = REPLACE(" + DBConstants.Item.PRED_ID_PATH + ", '" 
				+ item.getPredecessorsPath() + "', '" + newParent.getPredecessorsAndSelfPath() + "') WHERE " 
				+ DBConstants.ItemParent.PARENT_ID + " = " + item.getId() + " AND "
				+ DBConstants.ItemParent.PARENT_ID + " != " + DBConstants.ItemParent.ITEM_ID + " AND "
				+ DBConstants.ItemParent.ITEM_ID + " = " + DBConstants.Item.ID;
			ServerLogger.debug(sql);
			stmt.executeUpdate(sql);
			
			// Шаг 5. - Поменять родителя во всех параметрах перемещаемого айтема
			sql 
				= "UPDATE " + DBConstants.ItemIndexes.STRING_TABLE_NAME + " SET " + DBConstants.ItemIndexes.ITEM_PARENT + " = " + newParent.getId()
				+ " WHERE " + DBConstants.ItemIndexes.REF_ID + " = " + item.getRefId() + ";"
				+ " UPDATE " + DBConstants.ItemIndexes.INT_TABLE_NAME + " SET " + DBConstants.ItemIndexes.ITEM_PARENT + " = " + newParent.getId()
				+ " WHERE " + DBConstants.ItemIndexes.REF_ID + " = " + item.getRefId() + ";"
				+ " UPDATE " + DBConstants.ItemIndexes.DOUBLE_TABLE_NAME + " SET " + DBConstants.ItemIndexes.ITEM_PARENT + " = " + newParent.getId()
				+ " WHERE " + DBConstants.ItemIndexes.REF_ID + " = " + item.getRefId() + ";";
			ServerLogger.debug(sql);
			stmt.executeUpdate(sql);			
			
			// Шаг 6. - Переместить директорию с файлами
			executeCommand(new MoveDirectoryFileUnit(item, newParent));
			
			// Установить нового родителя в айтем
			item.setDirectParentId(newParent.getId());

		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

}