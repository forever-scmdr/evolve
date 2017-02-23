package ecommander.persistence.commandunits;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.EcommanderException;
import ecommander.model.Item;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.DBConstants;

/**
 * Создает новую ссылку
 * Создается расширенная ссылка, т. е. создаются связи между всеми предками айтема, содержащего ссылку и всеми потомками айтема, на который
 * ведет ссылка.
 * Такую ссылку нельзя создавать между двумя элементами из одной ветви иерархии (только между параллельными ветвями)
 * 
 * Иногда (при переносе старых версий CMS) ID ссылки известно заранее. Тогда его можно передавать в конструктор, и новая ссылка будет создаваться с 
 * заданным ID
 * @author EEEE
 *
 */
public class CreateExtendedReferenceDBUnit extends DBPersistenceCommandUnit {
	
	private long itemToReferId;
	private long parentId;
	private Item itemToRefer = null;
	private long itemId = 0;
	
	public CreateExtendedReferenceDBUnit(long itemToReferId, long parentId, long... newRefItemId) {
		this.itemToReferId = itemToReferId;
		this.parentId = parentId;
		if (newRefItemId.length > 0)
			this.itemId = newRefItemId[0];
	}

	public CreateExtendedReferenceDBUnit(Item itemToRefer, long parentId) {
		this.itemToRefer = itemToRefer;
		this.parentId = parentId;
	}
	
	public void execute() throws Exception {
		Statement stmt = null;
		PreparedStatement pstmt = null;
		try	{
			Connection conn = getTransactionContext().getConnection();
			stmt = conn.createStatement();
			
			String selectSql 
				= "SELECT " + DBConstants.ItemParent.REF_ID + " FROM " + DBConstants.ItemParent.TABLE 
				+ " WHERE " + DBConstants.ItemParent.PARENT_ID  + " = " + itemToReferId
				+ " AND " + DBConstants.ItemParent.REF_ID + " = " + parentId
				+ " UNION SELECT " + DBConstants.ItemParent.PARENT_ID + " FROM " + DBConstants.ItemParent.TABLE 
				+ " WHERE " + DBConstants.ItemParent.PARENT_ID  + " = " + parentId
				+ " AND " + DBConstants.ItemParent.REF_ID + " = " + itemToReferId;
			ServerLogger.debug(selectSql);
			ResultSet rs = stmt.executeQuery(selectSql);
			// Если запрос вернул хоть одну строку, то ссылку нельзя создавать
			if (rs.next())
				throw new EcommanderException("Link from item ID " + itemToReferId + " to item ID " + parentId + " can not be created");
			
			// Загружается айтем, на который надо ссылаться
			if (itemToRefer == null)
				itemToRefer = ItemQuery.loadById(itemToReferId, getTransactionContext().getConnection());
			
			// Подсчет общего количества прямых потомков родителя айтема для установления его порядкового номера
			int weight = SaveNewItemDBUnit.findNewWeight(conn, parentId);

			TemplateQuery builder = new TemplateQuery("New item save");
			builder.sql(
					"INSERT INTO " + 
					DBConstants.Item.TABLE + 
					" SET " +
					DBConstants.Item.TYPE_ID + "=").setLong(itemToRefer.getTypeId()).sql(", " +
					DBConstants.Item.REF_ID + "=").setLong(itemToRefer.getRefId()).sql(", " + 
					DBConstants.Item.DIRECT_PARENT_ID + "=").setLong(parentId).sql(", " + 
					DBConstants.Item.PRED_ID_PATH + "=").setString(itemToRefer.getPredecessorsPath()).sql(", " +
					DBConstants.Item.OWNER_GROUP_ID + "=").setLong(itemToRefer.getOwnerGroupId()).sql(", " +
					DBConstants.Item.OWNER_USER_ID + "=").setLong(itemToRefer.getOwnerUserId()).sql(", " + 
					DBConstants.Item.KEY + "=").setString(itemToRefer.getKey()).sql(", " + 
					DBConstants.Item.TRANSLIT_KEY + "=").setString(itemToRefer.getKeyUnique()).sql(", " + 
					DBConstants.Item.PARAMS + "=").setString(itemToRefer.outputValues()).sql(", " + 
					DBConstants.Item.INDEX_WEIGHT + "=").setInt(weight);
			// Иногда (например, при переносе со старой версии CMS) ID айтема уже задан (не равняется 0)
			boolean hasId = itemId > 0;
			if (hasId)
				builder.sql(", " + DBConstants.Item.ID + "=").setLong(itemId);
			
			pstmt = builder.prepareQuery(conn, true);
			pstmt.executeUpdate();

			// Получается ID нового айтема
			//rs = insertItemStmt.executeQuery("SELECT LAST_INSERT_ID()");
			if (!hasId) {
				rs = pstmt.getGeneratedKeys();
				if (rs.next())
					itemId = rs.getLong(1);
			}
			rs.close();
			pstmt.close();
			
			
			// Непосредственная связь между айтемом, на который ведет ссылка, и родительским айтемом для ссылки 
			String sql
				= "REPLACE INTO "
				+ DBConstants.ItemParent.TABLE + "("
				+ DBConstants.ItemParent.REF_ID + ", "
				+ DBConstants.ItemParent.ITEM_ID + ", "
				+ DBConstants.ItemParent.PARENT_ID + ", "
				+ DBConstants.ItemParent.ITEM_TYPE + ", "
				+ DBConstants.ItemParent.PARENT_LEVEL + ") SELECT "
				+ itemToRefer.getRefId() + ", " + itemId + ", " + parentId + ", " + itemToRefer.getTypeId() + ", 1\r\n";
			
			// Связь предков айтема, содержащего ссылку, с айтемом, на который ведет ссылка
			// DBConstants.ItemParent.REF_ID + " = " + parentId // добавлено для производительности
			sql
				+= " UNION SELECT DISTINCT " + itemToRefer.getId() + ", " + itemId + ", " + DBConstants.ItemParent.PARENT_ID + ", " 
				+ itemToRefer.getTypeId() + ", " + DBConstants.ItemParent.PARENT_LEVEL + " + 1 FROM " + DBConstants.ItemParent.TABLE 
				+ " WHERE "	+ DBConstants.ItemParent.REF_ID + " = " + parentId 
				+ " AND " + DBConstants.ItemParent.ITEM_ID + " = " + parentId + "\r\n"; 
			
			// Связь потомков айтема, на который ведет ссылка и айтема, который непосредственно содержит ссылку
			sql
				+= " UNION SELECT DISTINCT " + DBConstants.ItemParent.REF_ID 	+ ", " + itemId + ", " + parentId + ", " 
				+ itemToRefer.getTypeId() + ", " + DBConstants.ItemParent.PARENT_LEVEL + " + 1 FROM " + DBConstants.ItemParent.TABLE 
				+ " WHERE "	+ DBConstants.ItemParent.PARENT_ID + " = " + itemToRefer.getId() + "\r\n";
			
			// Связь потомков айтема, на который ведет ссылка и айтемов-предков айтема, который непосредственно содержит ссылку
			sql
				+= " UNION SELECT DISTINCT A." + DBConstants.ItemParent.REF_ID 
				+ ", " + itemId + ", B." + DBConstants.ItemParent.PARENT_ID + ", " + itemToRefer.getTypeId()
				+ ", B." + DBConstants.ItemParent.PARENT_LEVEL + " + A." + DBConstants.ItemParent.PARENT_LEVEL + " + 1 FROM "
				+ DBConstants.ItemParent.TABLE + " AS B, " + DBConstants.ItemParent.TABLE + " AS A WHERE B." 
				+ DBConstants.ItemParent.REF_ID + " = " + parentId
				+ " AND A." + DBConstants.ItemParent.PARENT_ID + " = " + itemToRefer.getId() + "\r\n"; 
			
			ServerLogger.debug(sql);
			stmt.executeUpdate(sql);
		} finally {
			if (stmt != null)
				stmt.close();
			MysqlConnector.closeStatement(pstmt);
		}
	}

}
