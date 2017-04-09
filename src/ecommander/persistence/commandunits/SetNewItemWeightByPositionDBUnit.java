package ecommander.persistence.commandunits;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import ecommander.model.Item;
import ecommander.persistence.mappers.DBConstants;
/**
 * Поднять или опустить айтем на одну позицию вверх или вних
 * @author EEEE
 *
 */
public class SetNewItemWeightByPositionDBUnit extends DBPersistenceCommandUnit {

	private Item item;
	private int newPosition;
	
	public SetNewItemWeightByPositionDBUnit(Item item, int newPosition) {
		this.item = item;
		this.newPosition = newPosition;
	}
	
	public void execute() throws Exception {
		Statement stmt = null;
		try {
			Connection conn = getTransactionContext().getConnection();
			stmt = conn.createStatement();
			String sign = up ? " < " : " > ";
			String sql 
				= "SELECT " + DBConstants.Item.INDEX_WEIGHT + " FROM " + DBConstants.Item.TABLE 
				+ " WHERE " + DBConstants.Item.DIRECT_PARENT_ID + " = " + item.getDirectParentId()
				+ " AND " + DBConstants.Item.INDEX_WEIGHT + sign + item.getChildWeight()
				+ " ORDER BY " + DBConstants.Item.INDEX_WEIGHT + " LIMIT 2";
			int indexBefore = 0;
			int indexAfter = 64000000;
			ResultSet rs = stmt.executeQuery(sql);
			// Если ничего не найдено, значит этот элемент и тк или первый или последний
			if (!rs.next())
				return;
			if (up)
				indexAfter = rs.getInt(1);
			else
				indexBefore = rs.getInt(1);
			if (rs.next()) {
				if (up)
					indexBefore = rs.getInt(1);
				else
					indexAfter = rs.getInt(1);
			}
			executeCommand(new SetNewItemWeightDBUnit(item.getId(), item.getDirectParentId(), indexBefore, indexAfter));
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}
}
