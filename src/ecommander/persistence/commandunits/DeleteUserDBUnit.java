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

	public DeleteUserDBUnit(User user) {
		this.user = user;
	}

	public void execute() throws Exception {
		Statement stmt = null;
		if (!getTransactionContext().getInitiator().isSuperUser() && !ignoreUser)
			throw new UserNotAllowedException();
		try	{
			Connection conn = getTransactionContext().getConnection();
			stmt = conn.createStatement();
			// Сделать айтемы пользователя общими
			String sql
				= "UPDATE " + DBConstants.ItemParent.TABLE
				+ " SET " + DBConstants.ItemParent.USER  + "=0"
				+ " WHERE " + DBConstants.ItemParent.USER + "=" + user.getUserId();
			stmt.executeUpdate(sql);
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
