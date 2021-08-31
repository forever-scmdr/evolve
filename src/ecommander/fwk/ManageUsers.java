package ecommander.fwk;

import ecommander.controllers.PageController;
import ecommander.model.*;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.LinkPE;
import ecommander.pages.PageModelRegistry;
import ecommander.pages.PagePE;
import ecommander.persistence.commandunits.*;
import ecommander.persistence.common.PersistenceCommandUnit;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;

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
	public static final String REGISTERED = "registered";


	private Item userItem;
	private byte mode;

	public ManageUsers(Item item, byte mode) {
		this.userItem = item;
		this.mode = mode;
	}


	@Override
	public void execute() throws Exception {
		if (mode == CREATE) {
			if (userItem.isValueEmpty(EMAIL_PARAM)) {
				throw new EcommanderException(VALIDATION_FAILED, "Не заполнены обязательные поля");
			}
			makeRegistered();
			/*
			String userName = userItem.getStringValue(EMAIL_PARAM);
			String password = userItem.getStringValue(PASSWORD_PARAM);
			User newUser = new User(userName, password, "manager", User.ANONYMOUS_ID);
			newUser.addGroup(REGISTERED, UserGroupRegistry.getGroup(REGISTERED), User.ADMIN);
			try {
				executeCommand(new SaveNewUserDBUnit(newUser));
			} catch (UserExistsExcepion e) {
				throw new EcommanderException(VALIDATION_FAILED, "Пользователь с таким именем уже существует");
			}
			 */
		} else if (mode == UPDATE) {
			makeRegistered();
			String initialEmail = (String)((SingleParameter)userItem.getParameterByName(EMAIL_PARAM)).getInitialValue();
			//String initialPass = (String)((SingleParameter)userItem.getParameterByName(PASSWORD_PARAM)).getInitialValue();
			User user = UserMapper.getUser(initialEmail, /*initialPass, */getTransactionContext().getConnection());
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

	private void makeRegistered() throws Exception {
		Parameter regParam = userItem.getParameterByName(REGISTERED);
		if (userItem.isPersonal()) {
			if (userItem.getByteValue(REGISTERED) != (byte) 1) {
				userItem.setValue(REGISTERED, (byte) 1);
				executeCommand(SaveItemDBUnit.get(userItem).noTriggerExtra().ignoreUser());
			}
			return;
		} else if (userItem.getByteValue(REGISTERED) == (byte) 0) {
			return;
		}
		String userName = userItem.getStringValue(EMAIL_PARAM);
		String password = userItem.getStringValue(PASSWORD_PARAM);
		if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
			throw new EcommanderException(ErrorCodes.VALIDATION_FAILED, "User name or password is empty");
		}
		User newUser = new User(userName, password, "registered user", User.ANONYMOUS_ID);
		newUser.addGroup(REGISTERED, UserGroupRegistry.getGroup(REGISTERED), User.SIMPLE);
		try {
			executeCommand(new SaveNewUserDBUnit(newUser).ignoreUser());
		} catch (UserExistsExcepion e) {
			User owner = UserMapper.getUser(userItem.getOwnerUserId());
			if (StringUtils.equalsIgnoreCase(newUser.getName(), owner.getName()))
				return;
			throw e;
		}
		executeCommand(ChangeItemOwnerDBUnit.newUser(userItem, newUser.getUserId(), UserGroupRegistry.getGroup(REGISTERED)).ignoreUser());

		// Отправка письма
		try {
			Multipart regularMP = new MimeMultipart();
			MimeBodyPart regularTextPart = new MimeBodyPart();
			regularMP.addBodyPart(regularTextPart);
			LinkPE regularLink = LinkPE.newDirectLink("link", "register_email", false);
			regularLink.addStaticVariable("user", userItem.getId() + "");
			ExecutablePagePE regularTemplate =
					PageModelRegistry.getRegistry().getExecutablePage(regularLink.serialize(), null, null);
			final String customerEmail = userItem.getStringValue("email");

			ByteArrayOutputStream regularBos = new ByteArrayOutputStream();
			PageController.newSimple().executePage(regularTemplate, regularBos);
			regularTextPart.setContent(regularBos.toString("UTF-8"), regularTemplate.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER)
					+ ";charset=UTF-8");

			if (StringUtils.isNotBlank(customerEmail))
				EmailUtils.sendGmailDefault(customerEmail, "Регистрация на сайте belchip.by", regularMP);

		} catch (PageNotFoundException pnf) {
			// ничего не делать (все нормально)
		} catch (Exception e) {
			ServerLogger.error("error while sinding email about registration", e);
		}
	}
}
