package ecommander.fwk;

import ecommander.model.*;
import ecommander.pages.*;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.commandunits.SaveNewUserDBUnit;
import ecommander.persistence.commandunits.UpdateUserDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

/**
 * Команда для регистрации пользователя
 * Created by E on 5/4/2018.
 */
public abstract class BasicRegisterCommand extends Command {

	public static final String REGISTERED_CATALOG_ITEM = "registered_catalog";

	public static final String EMAIL_PARAM = "email";
	public static final String PHONE_PARAM = "phone";
	public static final String PASSWORD_PARAM = "password";

	public static final String REGISTERED_GROUP = "registered";
	public static final String USER_ITEM = "user";
	protected static final String CART_ITEM = "cart";

	@Override
	public ResultPE execute() throws Exception {
		return null;
	}

	public ResultPE register() throws Exception {
		if (!validate()) {
			return getResult("not_set");
		}
		Item form = getItemForm().getTransientSingleItem();
		if (form.isValueEmpty(PASSWORD_PARAM)) {
			return getResult("not_set");
		}
		Item catalog = ItemUtils.ensureSingleRootItem(REGISTERED_CATALOG_ITEM, User.getDefaultUser(),
				UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		String userName = (StringUtils.isEmpty(form.getStringValue(EMAIL_PARAM)))? form.getStringValue(PHONE_PARAM) : form.getStringValue(EMAIL_PARAM);
		//String userName = form.getStringValue(EMAIL_PARAM);
		String password = form.getStringValue(PASSWORD_PARAM);
		User newUser = new User(userName, password, "registered user", User.ANONYMOUS_ID);
		newUser.addGroup(REGISTERED_GROUP, UserGroupRegistry.getGroup(REGISTERED_GROUP), User.SIMPLE);
		try {
			executeAndCommitCommandUnits(new SaveNewUserDBUnit(newUser).ignoreUser());
		} catch (UserExistsExcepion e) {
			return getResult("user_exists");
		}
		Item userItem = ItemQuery.loadSingleItemByParamValue(USER_ITEM, EMAIL_PARAM, userName);
		if (userItem != null) {
			Item.updateParamValues(userItem, form);
		} else {
			userItem = form;
			userItem.setContextParentId(ItemTypeRegistry.getPrimaryAssoc(), catalog.getId());
		}
		userItem.setOwner(UserGroupRegistry.getGroup(REGISTERED_GROUP), newUser.getUserId());
		executeCommandUnit(SaveItemDBUnit.get(userItem).ignoreUser().noTriggerExtra());
		startUserSession(newUser);
		commitCommandUnits();

		//Add cart contacts!
		Item oldUserItem = getSessionMapper().getSingleRootItemByName(USER_ITEM);
		if (oldUserItem != null){
			getSessionMapper().removeItems(oldUserItem.getId());
		}
		userItem.setContextPrimaryParentId(Item.DEFAULT_ID);
		getSessionMapper().saveTemporaryItem(userItem);


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
				Item userItem = new ItemQuery(USER_ITEM).setUser(user).loadFirstItem();
				if (userItem != null) {
					// Удалить старый айтем из сеанса и сохранить новый
					Item oldUserItem = getSessionMapper().getSingleRootItemByName(USER_ITEM);
					if (oldUserItem != null)
						getSessionMapper().removeItems(oldUserItem.getId());
					userItem.setContextPrimaryParentId(Item.DEFAULT_ID);
					getSessionMapper().saveTemporaryItem(userItem);
					// Удалить корзину
					getSessionMapper().removeItems(CART_ITEM);
				}
				return getResult("login");
			} else {
				return getResult("login_error");
			}
		} catch (Exception e) {
			ServerLogger.error("Auth process error", e);
			return getResult("login_error");
		}
	}


	public ResultPE update() throws Exception {
		if (!validate()) {
			return getResult("not_set_personal");
		}
		Item form = getItemForm().getTransientSingleItem();
		boolean changeUser = false;
		User user = getInitiator();
		String pass1 = form.getStringExtra("new-password-1");
		String pass2 = form.getStringExtra("new-password-2");
		if (StringUtils.isNotBlank(pass1)) {
			if (!StringUtils.equals(pass1, pass2)) {
				return getResult("passwords_not_match_error");
			}
			user.setNewPassword(pass1);
			changeUser = true;
		}
		if (!StringUtils.equalsIgnoreCase(user.getName(), form.getStringValue(EMAIL_PARAM))) {
			user.setNewName(form.getStringValue(EMAIL_PARAM));
			changeUser = true;
		}
		if (changeUser) {
			try {
				executeAndCommitCommandUnits(new UpdateUserDBUnit(user, false).noTriggerExtra());
			} catch (UserExistsExcepion e) {
				return getResult("user_exists_personal");
			}
		}
		Item userItem = ItemQuery.loadById(form.getId());
		Item.updateParamValues(form, userItem);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(userItem));
		return getResult("success_personal");
	}

	protected abstract boolean validate() throws Exception;
}
