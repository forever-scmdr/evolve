package ecommander.persistence.commandunits;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.UserExistsExcepion;
import ecommander.fwk.UserNotAllowedException;
import ecommander.model.UserMapper;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.model.User;
import org.apache.commons.lang3.StringUtils;

/**
 * Обновить пользователя
 * Можно обновить всего пользователя (с логином и паролем) или только его группы
 *
 * Политика удаления пользователя - оставлять или нет айтемы, которые ему принадлежат (персональные)
 * Если айтемы сохраняются, то они становятся общими для группы, которой они принадлежали.
 * Если айтемы удаляются, то они просто удаляются.
 *
 * @author EEEE
 *
 */
public class UpdateUserDBUnit extends DBPersistenceCommandUnit implements DBConstants, DBConstants.UsersTbl {

	private User user;
	boolean deleteItems;

	public UpdateUserDBUnit(User user, boolean deleteItems) {
		this.user = user;
		this.deleteItems = deleteItems;
	}
	
	public void execute() throws Exception {

		// Проверка, изменились ли параметры пользователя
		User oldUser = UserMapper.getUser(user.getUserId(), getTransactionContext().getConnection());
		boolean justGroups = StringUtils.equals(user.getName(), oldUser.getName())
				&& StringUtils.equals(user.getPassword(), oldUser.getPassword())
				&& StringUtils.equals(user.getDescription(), oldUser.getDescription());

		// Проверка прав
		testPrivileges(user, justGroups);

		// Проверка существования нового логина
		if (!justGroups) {
			int existingId = UserMapper.getUserId(user.getName(), getTransactionContext().getConnection());
			if (existingId >= 0 && existingId != user.getUserId())
				throw new UserExistsExcepion(user.getName());
		}

		// Сохранение групп
		TemplateQuery deleteGroups = new TemplateQuery("Update user groups");

		// Сохранение пользователя
		if (!justGroups) {
			TemplateQuery updateUser = new TemplateQuery("Update user attributes");
			updateUser
					.UPDATE(U_TABLE).SET()
					.col(U_LOGIN).setString(user.getName()).com()
					.col(U_PASSWORD).setString(user.getPassword()).com()
					.col(U_DESCRIPTION).setString(user.getDescription());
			try (PreparedStatement pstmt = updateUser.prepareQuery(getTransactionContext().getConnection())) {
				pstmt.executeUpdate();
			}
		}



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
			sql = "UPDATE " + DBConstants.ItemParent.IP_TABLE + " SET " + DBConstants.ItemParent.GROUP + "=" + user.getGroupId()
					+ " WHERE " + DBConstants.ItemParent.USER + "=" + user.getUserId();
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