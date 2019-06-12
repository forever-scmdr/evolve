package extra;

import ecommander.controllers.PageController;
import ecommander.fwk.*;
import ecommander.model.*;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.LinkPE;
import ecommander.pages.PageModelRegistry;
import ecommander.pages.PagePE;
import ecommander.persistence.commandunits.ChangeItemOwnerDBUnit;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.commandunits.SaveNewUserDBUnit;
import ecommander.persistence.common.PersistenceCommandUnit;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;

/**
 * Created by E on 2/4/2019.
 */
public class MakeUserRegistered implements ItemEventCommandFactory {

	public static final String EMAIL_PARAM = "email";
	public static final String PASSWORD_PARAM = "password";

	public static final String REGISTERED = "registered";

	private static class Command extends DBPersistenceCommandUnit {

		private Item userItem;

		public Command(Item userItem) {
			this.userItem = userItem;
		}

		@Override
		public void execute() throws Exception {
			Parameter regParam = userItem.getParameterByName(REGISTERED);
			if (regParam.hasChanged() && userItem.getByteValue(REGISTERED) != (byte) 1) {
				userItem.setValue(REGISTERED, (byte) 1);
				executeCommand(SaveItemDBUnit.get(userItem).noTriggerExtra());
				return;
			}
			if (!regParam.hasChanged()) {
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
				throw new EcommanderException(ErrorCodes.VALIDATION_FAILED, "User name already exists");
			}
			executeCommand(ChangeItemOwnerDBUnit.newUser(userItem, newUser.getUserId(), UserGroupRegistry.getGroup(REGISTERED)));

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
					EmailUtils.sendGmailDefault(customerEmail, "Регистрация на сайте skobtrade.by", regularMP);

			} catch (Exception e) {
				ServerLogger.error("error while sinding email about registration", e);
			}
		}
	}

	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new Command(item);
	}
}
