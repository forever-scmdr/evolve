package ecommander.persistence.commandunits;

import ecommander.fwk.UserExistsExcepion;
import ecommander.model.User;
import ecommander.model.UserMapper;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;

/**
 * Создается новый пользователь
 * @author EEEE
 *
 */
public class SaveNewUserDBUnit extends DBPersistenceCommandUnit implements DBConstants.UsersTbl, DBConstants.UserGroups {

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
		insertUser.INSERT_INTO(USER, U_LOGIN, U_PASSWORD, U_DESCRIPTION)
				.sql(" VALUES (")
				.setString(user.getName()).com()
				.setString(user.getPassword()).com()
				.setString(user.getDescription()).sql(");\r\n");
		HashSet<User.Group> groups = user.getGroups();
		if (groups.size() > 0)
			insertUser.INSERT_INTO(USER_GROUP, UG_GROUP_ID, UG_GROUP_NAME, UG_ROLE, UG_USER_ID)
					.sql(" VALUES ");
		boolean notFirst = false;
		for (User.Group group : groups) {
			if (notFirst)
				insertUser.com();
			insertUser.sql(" (").setByte(group.id).com()
					.setString(group.name).com()
					.setByte(group.role)
					.sql(", LAST_INSERT_ID())");
			notFirst = true;
		}
		try (PreparedStatement pstmt = insertUser.prepareQuery(getTransactionContext().getConnection(), true)) {
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			user.setNewId(rs.getInt(1));
		}
	}

}
