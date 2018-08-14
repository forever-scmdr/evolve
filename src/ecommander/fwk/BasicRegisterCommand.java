package ecommander.fwk;

import ecommander.controllers.LoginServlet;
import ecommander.model.*;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.commandunits.SaveNewUserDBUnit;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

/**
 * Команда для регистрации пользователя
 * Created by E on 5/4/2018.
 */
public abstract class BasicRegisterCommand extends Command {

	public static final String REGISTERED_CATALOG_ITEM = "registered_catalog";

	public static final String EMAIL_PARAM = "email";
	public static final String PASSWORD_PARAM = "password";

	public static final String REGISTERED_GROUP = "registered";

	@Override
	public ResultPE execute() throws Exception {
		return null;
	}

	public ResultPE register() throws Exception {
		if (!validate()) {
			return getResult("not_set");
		}
		Item catalog = ItemUtils.ensureSingleRootItem(REGISTERED_CATALOG_ITEM, User.getDefaultUser(),
				UserGroupRegistry.getGroup(REGISTERED_GROUP), User.ANONYMOUS_ID);
		Item form = getItemForm().getSingleItem();
		String userName = form.getStringValue(EMAIL_PARAM);
		String password = form.getStringValue(PASSWORD_PARAM);
		User newUser = new User(userName, password, "registered user", User.ANONYMOUS_ID);
		newUser.addGroup(REGISTERED_GROUP, UserGroupRegistry.getGroup(REGISTERED_GROUP), User.SIMPLE);
		try {
			executeAndCommitCommandUnits(new SaveNewUserDBUnit(newUser).ignoreUser());
		} catch (UserExistsExcepion e) {
			return getResult("user_exists");
		}
		startUserSession(newUser);
		form.setContextParentId(ItemTypeRegistry.getPrimaryAssoc(), catalog.getId());
		form.setOwner(UserGroupRegistry.getGroup(REGISTERED_GROUP), newUser.getUserId());
		executeAndCommitCommandUnits(SaveItemDBUnit.get(form));
		return getResult("success");
	}


	public ResultPE login() throws EcommanderException {
		String userName = getVarSingleValue(EMAIL_PARAM);
		String password = getVarSingleValue(PASSWORD_PARAM);
		try {
			User user;
			try (Connection conn = MysqlConnector.getConnection()) {
				user = UserMapper.getUser(userName, password, conn);
			}
			if (user != null) {
				startUserSession(user);
				return getResult("login");
			} else {
				return getResult("login_error");
			}
		} catch (Exception e) {
			ServerLogger.error("Auth process error", e);
			return getResult("login_error");
		}
	}


	protected abstract boolean validate() throws Exception;
}
