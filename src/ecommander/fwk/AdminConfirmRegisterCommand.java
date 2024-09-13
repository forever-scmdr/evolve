package ecommander.fwk;

import ecommander.controllers.PageController;
import ecommander.controllers.SessionContext;
import ecommander.model.*;
import ecommander.pages.*;
import ecommander.persistence.commandunits.ChangeItemOwnerDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.commandunits.SaveNewUserDBUnit;
import ecommander.persistence.commandunits.UpdateUserDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;

/**
 * Команда для регистрации пользователя с подтверждением от админа.
 * Пользователь после регистрации не создается, создается только айтем пользователя и отправляется письмо админу,
 * который должен подтвердить регистрацию. После подтверждения новому пользователю отправляется письмо
 */
public class AdminConfirmRegisterCommand extends BasicRegisterCommand {

	public static final String REGISTERED_CATALOG_ITEM = "registered_catalog";

	public static final String EMAIL_PARAM = "email";
	public static final String PHONE_PARAM = "phone";
	public static final String PASSWORD_PARAM = "password";

	public static final String REGISTERED_GROUP = "registered";
	public static final String USER_ITEM = "user";

	@Override
	public ResultPE execute() throws Exception {
		return null;
	}

	/**
	 * Регистрация. Точнее только подача заявки на регистрацию
	 * Конечное действие, вызывается из pages.xml через method или method-var
	 * @return
	 * @throws Exception
	 */
	public ResultPE register() throws Exception {
		if (!validate()) {
			return getResult("not_set");
		}
		Item form = getItemForm().getItemSingleTransient();
		if (form.isValueEmpty(PASSWORD_PARAM)) {
			return getResult("not_set");
		}
		String userName = (StringUtils.isEmpty(form.getStringValue(EMAIL_PARAM)))? form.getStringValue(PHONE_PARAM) : form.getStringValue(EMAIL_PARAM);
		Item userItem = ItemQuery.loadSingleItemByParamValue(USER_ITEM, EMAIL_PARAM, userName);
		if (userItem != null || UserMapper.userNameExists(userName)) {
			return getResult("user_exists");
		}
		Item catalog = ItemUtils.ensureSingleRootItem(REGISTERED_CATALOG_ITEM, User.getDefaultUser(),
				UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);

		userItem = form;
		userItem.setContextParentId(ItemTypeRegistry.getPrimaryAssoc(), catalog.getId());
		userItem.setOwner(UserGroupRegistry.getGroup(REGISTERED_GROUP), User.ANONYMOUS_ID);
		executeCommandUnit(SaveItemDBUnit.get(userItem).ignoreUser().noTriggerExtra());
		commitCommandUnits();

		sendAdminEmail(userItem, getUrlBase());

		return getResult("success");
	}

	/**
	 * Подтверждение регистрации
	 * Конечное действие, вызывается из pages.xml через method или method-var
	 * @return
	 * @throws Exception
	 */
	public ResultPE confirmRegistration() throws Exception {
		Item userItem = getSingleLoadedItem("user");
		if (userItem == null) {
			return getResult("not_set");
		}
		try {
			userItem.setValue("registered", (byte)1);
			executeCommandUnit(SaveItemDBUnit.get(userItem).ignoreUser());
			commitCommandUnits();
		} catch (UserExistsExcepion e) {
			return getResult("user_exists");
		}

		sendUserEmail(userItem);

		return getResult("login"); // т.к. перенаправляет на любую страницу
	}


	/**
	 * Отправить email об одобрении регистрации пользователю
	 * @param userItem
	 */
	public void sendUserEmail(Item userItem) {
		// Отправка письма
		try {
			Multipart regularMP = new MimeMultipart();
			MimeBodyPart regularTextPart = new MimeBodyPart();
			regularMP.addBodyPart(regularTextPart);
			LinkPE regularLink = LinkPE.newDirectLink("link", "register_email", false);

			regularLink.addStaticVariable("user", userItem.getId() + "");
			regularLink.addStaticVariable("base", getUrlBase());
			ExecutablePagePE regularTemplate =
					PageModelRegistry.getRegistry().getExecutablePage(regularLink.serialize(), null, null);
			final String customerEmail = userItem.getStringValue("email");

			ByteArrayOutputStream regularBos = new ByteArrayOutputStream();
			PageController.newSimple().executePage(regularTemplate, regularBos);
			regularTextPart.setContent(regularBos.toString("UTF-8"), regularTemplate.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER)
					+ ";charset=UTF-8");

			if (StringUtils.isNotBlank(customerEmail))
				EmailUtils.sendGmailDefault(customerEmail, "Регистрация на сайте " + getUrlBase(), regularMP);

		} catch (Exception e) {
			ServerLogger.error("error while sinding email about registration", e);
		}
	}

	/**
	 * Отправить email о регистрации пользователю
	 * @param userItem
	 */
	public void sendAdminEmail(Item userItem, String siteUrl) {
		// Отправка письма
		try {
			Multipart regularMP = new MimeMultipart();
			MimeBodyPart regularTextPart = new MimeBodyPart();
			regularMP.addBodyPart(regularTextPart);
			LinkPE regularLink = LinkPE.newDirectLink("link", "new_user_email", false);

			regularLink.addStaticVariable("user", userItem.getId() + "");
			regularLink.addStaticVariable("base", siteUrl);
			//ExecutablePagePE regularTemplate = getExecutablePage(regularLink.serialize());
			User admin = UserMapper.getUser("admin");
			ExecutablePagePE regularTemplate =
					PageModelRegistry.getRegistry().getExecutablePage(regularLink.serialize(), null, SessionContext.userOnlySessionContext(admin));

			final String adminEmail = getVarSingleValue("email");

			ByteArrayOutputStream regularBos = new ByteArrayOutputStream();
			PageController.newSimple().executePage(regularTemplate, regularBos);
			regularTextPart.setContent(regularBos.toString("UTF-8"), regularTemplate.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER)
					+ ";charset=UTF-8");

			if (StringUtils.isNotBlank(adminEmail))
				EmailUtils.sendGmailDefault(adminEmail, "Запрос на регистрацию " + siteUrl, regularMP);

		} catch (Exception e) {
			ServerLogger.error("error while sinding email about registration", e);
		}
	}

}
