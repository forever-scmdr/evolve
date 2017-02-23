package ecommander.persistence.commandunits;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.UserNotAllowedException;
import ecommander.model.Item;
import ecommander.persistence.mappers.DBConstants;
import ecommander.model.User;

public class DeleteUserDBUnit extends DBPersistenceCommandUnit {

	private User user;
	private ArrayList<Item> firstLevelUserItems;
	
	public DeleteUserDBUnit(User user) {
		this.user = user;
	}
	
	public DeleteUserDBUnit(User user, ArrayList<Item> firstLevelUserItems) {
		this.user = user;
		this.firstLevelUserItems = firstLevelUserItems;
	}
	
	public void execute() throws Exception {
		Statement stmt = null;
		if (!getTransactionContext().getInitiator().isSuperUser() && !ignoreUser)
			throw new UserNotAllowedException();
		try	{
			Connection conn = getTransactionContext().getConnection();
			stmt = conn.createStatement();
			String sql = null;
			ResultSet rs = null;
			if (firstLevelUserItems != null) {
				for (Item item : firstLevelUserItems) {
					executeCommand(new DeleteItemBDUnit(item).ignoreUser(true));
				}
			}
			// Удалить другие айтемы, которые принадлежат пользователю, но не являются сабайтемами корневых айтемов пользователя
			// (сначала загрузить их)
			sql 
				= "SELECT " + DBConstants.Item.ID + " FROM " + DBConstants.Item.TABLE
				+ " WHERE " + DBConstants.Item.OWNER_USER_ID + " = " + user.getUserId();
			rs = stmt.executeQuery(sql);
			// Удалить все айтемы и сабайтемы
			while (rs.next()) {
				executeCommand(new DeleteItemBDUnit(rs.getLong(1)).ignoreUser(true));
			}
			rs.close();
			// Удалить пользователя
			sql = "DELETE FROM " + DBConstants.Users.TABLE + " WHERE " + DBConstants.Users.ID + "=" + user.getUserId();
			ServerLogger.debug(sql);
			stmt.executeUpdate(sql);
		} finally {
			if (stmt != null)
				stmt.close();
		}
		
	}

}
