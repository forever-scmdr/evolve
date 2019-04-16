package ecommander.fwk;

import ecommander.model.*;
import ecommander.persistence.commandunits.*;
import ecommander.persistence.common.PersistenceCommandUnit;

/**
 * Created by E on 19/6/2018.
 */
public class ManageUsers extends DBPersistenceCommandUnit implements ErrorCodes {

	public static class OnCreate implements ItemEventCommandFactory {

		@Override
		public PersistenceCommandUnit createCommand(Item item) throws Exception {
			return new ManageUsers(item, CREATE);
		}
	}

	public static class OnUpdate implements ItemEventCommandFactory {

		@Override
		public PersistenceCommandUnit createCommand(Item item) throws Exception {
			return new ManageUsers(item, UPDATE);
		}
	}

	public static class OnDelete implements ItemEventCommandFactory {

		@Override
		public PersistenceCommandUnit createCommand(Item item) throws Exception {
			return new ManageUsers(item, DELETE);
		}
	}

	private static byte CREATE = 1;
	private static byte UPDATE = 2;
	private static byte DELETE = 3;

	public static final String EMAIL_PARAM = "email";
	public static final String PASSWORD_PARAM = "password";
	public static final String MANAGER_GROUP = "manager";
	public static final String REGISTERED_GROUP = "registered";


	private Item userItem;
	private byte mode;

	public ManageUsers(Item item, byte mode) {
		this.userItem = item;
		this.mode = mode;
	}


	@Override
	public void execute() throws Exception {
		if (mode == CREATE) {
			if (userItem.isValueEmpty(PASSWORD_PARAM) || userItem.isValueEmpty(EMAIL_PARAM)) {
				throw new EcommanderException(VALIDATION_FAILED, "Не заполнены обязательные поля");
			}
			String userName = userItem.getStringValue(EMAIL_PARAM);
			String password = userItem.getStringValue(PASSWORD_PARAM);
			User newUser = new User(userName, password, "manager", User.ANONYMOUS_ID);
			newUser.addGroup(MANAGER_GROUP, UserGroupRegistry.getGroup(MANAGER_GROUP), User.SIMPLE);
			newUser.addGroup(REGISTERED_GROUP, UserGroupRegistry.getGroup(REGISTERED_GROUP), User.ADMIN);
			try {
				executeCommand(new SaveNewUserDBUnit(newUser));
			} catch (UserExistsExcepion e) {
				throw new EcommanderException(VALIDATION_FAILED, "Пользователь с таким именем уже существует");
			}
			executeCommand(ChangeItemOwnerDBUnit.newUser(userItem, newUser.getUserId(), UserGroupRegistry.getGroup(MANAGER_GROUP)));
		} else if (mode == UPDATE) {
			String initialEmail = (String)((SingleParameter)userItem.getParameterByName(EMAIL_PARAM)).getInitialValue();
			String initialPass = (String)((SingleParameter)userItem.getParameterByName(PASSWORD_PARAM)).getInitialValue();
			User user = UserMapper.getUser(initialEmail, initialPass, getTransactionContext().getConnection());
			if (user != null && (userItem.getParameterByName(EMAIL_PARAM).hasChanged()
					|| userItem.getParameterByName(PASSWORD_PARAM).hasChanged())) {
				user.setNewName(userItem.getStringValue(EMAIL_PARAM));
				user.setNewPassword(userItem.getStringValue(PASSWORD_PARAM));
				try {
					executeCommand(new UpdateUserDBUnit(user, false));
				} catch (UserExistsExcepion e) {
					throw new EcommanderException(VALIDATION_FAILED, "Пользователь с таким именем уже существует");
				}
			}
		} else if (mode == DELETE) {
			User user = UserMapper.getUser(userItem.getStringValue(EMAIL_PARAM), userItem.getStringValue(PASSWORD_PARAM),
					getTransactionContext().getConnection());
			if (user != null) {
				executeCommand(new DeleteUserDBUnit(user.getUserId(), true));
			}
		}
	}
}
