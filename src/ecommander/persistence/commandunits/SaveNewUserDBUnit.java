package ecommander.persistence.commandunits;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.UserNotAllowedException;
import ecommander.persistence.mappers.DBConstants;
import ecommander.model.User;

/**
 * Создается новый пользователь
 * @author EEEE
 *
 */
public class SaveNewUserDBUnit extends DBPersistenceCommandUnit {

	private User user;
	
	public SaveNewUserDBUnit(User user) {
		this.user = user;
	}
	
	public void execute() throws Exception {
		if (!getTransactionContext().getInitiator().isSuperUser() && !ignoreUser)
			throw new UserNotAllowedException();
		Statement stmt = null;
		try	{
			Connection conn = getTransactionContext().getConnection();
			stmt = conn.createStatement();
			String sql = new String();
			sql += "INSERT INTO " + DBConstants.Users.TABLE + " SET " + DBConstants.Users.LOGIN + "='" + user.getName() + "', "
					+ DBConstants.Users.GROUP + "='" + user.getGroup() + "', " + DBConstants.Users.PASSWORD + "='"
					+ user.getPassword() + "', " + DBConstants.Users.DESCRIPTION + "='" + user.getDescription() + "'";
			ServerLogger.debug(sql);
			stmt.executeUpdate(sql);
			if (user.getUserId() == User.NO_USER_ID) {
				// Получается ID нового айтема и устанавливается этот объект айтема
				ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
				rs.next();
				user.setNewId(rs.getLong(1));
			}
		} finally {
			if (stmt != null)
				stmt.close();
		}
		
	}

}
