package ecommander.persistence.commandunits;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.UserExistsExcepion;
import ecommander.fwk.UserNotAllowedException;
import ecommander.persistence.mappers.DBConstants;
import ecommander.model.User;

/**
 * Создается новый пользователь
 * @author EEEE
 *
 */
public class UpdateUserDBUnit extends DBPersistenceCommandUnit {

	private User user;
	
	public UpdateUserDBUnit(User user) {
		this.user = user;
	}
	
	public void execute() throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		if (getTransactionContext().getInitiator().getUserId() != user.getUserId() 
				&& getTransactionContext().getInitiator().isSuperUser()
				&& !ignoreUser)
			throw new UserNotAllowedException();
		try	{
			Connection conn = getTransactionContext().getConnection();
			stmt = conn.createStatement();
			// Проверяется наличие логина
			String sql = new String();
			sql = "SELECT COUNT(*) FROM " + DBConstants.Users.TABLE + " WHERE " + DBConstants.Users.LOGIN + "='" 
					+ user.getName() + "' AND " + DBConstants.Users.ID + "!=" + user.getUserId();
			ServerLogger.debug(sql);
			rs = stmt.executeQuery(sql);
			if (rs.next() && rs.getInt(1) != 0) {
				throw new UserExistsExcepion(user.getName());
			}
			// Поменять запись юзера
			sql = "UPDATE " + DBConstants.Users.TABLE + " SET " + DBConstants.Users.LOGIN + "='" + user.getName() + "', "
					+ DBConstants.Users.GROUP + "='" + user.getGroup() + "', " + DBConstants.Users.PASSWORD + "='"
					+ user.getPassword() + "', " + DBConstants.Users.DESCRIPTION + "='" + user.getDescription() + "' WHERE "
					+ DBConstants.Users.ID + "=" + user.getUserId();
			ServerLogger.debug(sql);
			stmt.executeUpdate(sql);
			// Установить новую группу пользователя во все айтемы этого пользователя
			sql = "UPDATE " + DBConstants.Item.TABLE + " SET " + DBConstants.Item.OWNER_GROUP_ID + "=" + user.getGroupId()
					+ " WHERE " + DBConstants.Item.OWNER_USER_ID + "=" + user.getUserId();
			ServerLogger.debug(sql);
			stmt.executeUpdate(sql);
		} finally {
			if (stmt != null)
				stmt.close();
			if (rs != null)
				rs.close();
		}
		
	}

}