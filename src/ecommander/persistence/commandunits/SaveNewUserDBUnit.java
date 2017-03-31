package ecommander.persistence.commandunits;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.UserExistsExcepion;
import ecommander.fwk.UserNotAllowedException;
import ecommander.model.UserMapper;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.model.User;

/**
 * Создается новый пользователь
 * @author EEEE
 *
 */
public class SaveNewUserDBUnit extends DBPersistenceCommandUnit implements DBConstants, DBConstants.UsersTbl {

	private User user;
	
	public SaveNewUserDBUnit(User user) {
		this.user = user;
	}
	
	public void execute() throws Exception {

		// Проверить права доступа
		testPrivileges(user, false);

		// Проверить существование пользователя с таким именем
		if (UserMapper.userNameExists(user.getName(), getTransactionContext().getConnection()))
			throw new UserExistsExcepion(user.getName());

		// Сохранить пользователя и его права
		TemplateQuery insertUser = new TemplateQuery("Create new User");
		insertUser.INSERT_INTO(TABLE, LOGIN, PASSWORD, DESCRIPTION).sql(" VALUES (").setString(user.getName()).com()
				.setString(user.getPassword()).com().setString(user.getDescription()).sql(";\r\n");
		ArrayList<User.Group> groups = user.getGroups();
		if (groups.size() > 0)
			insertUser
					.INSERT_INTO(DBConstants.UserGroups.TABLE, DBConstants.UserGroups.GROUP_ID,
							DBConstants.UserGroups.GROUP_NAME, DBConstants.UserGroups.ROLE,
							DBConstants.UserGroups.USER_ID)
					.sql(" VALUES ");
		boolean notFirst = false;
		for (User.Group group : groups) {
			if (notFirst)
				insertUser.com();
			insertUser.sql(" (").setByte(group.id).com()
					.setString(group.name).com()
					.setByte(group.role).com()
					.setInt(user.getUserId()).sql(")");
			notFirst = false;
		}
		try (PreparedStatement pstmt = insertUser.prepareQuery(getTransactionContext().getConnection(), true)) {
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			user.setNewId(rs.getInt(1));
		}
	}

}
