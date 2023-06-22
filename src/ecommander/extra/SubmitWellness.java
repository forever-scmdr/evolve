package ecommander.extra;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;

import ecommander.application.extra.EmailUtils;
import ecommander.common.ServerLogger;
import ecommander.common.Strings;
import ecommander.controllers.PageController;
import ecommander.model.item.Item;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ExecutablePagePE;
import ecommander.pages.elements.ItemHttpPostForm;
import ecommander.pages.elements.PagePE;
import ecommander.pages.elements.ResultPE;

public class SubmitWellness extends Command {
	private static final String DAY_PREFIX = "day_";
	private static final String DAYS = "days";

	@Override
	public ResultPE execute() throws Exception {
		ItemHttpPostForm form = getItemForm();
		saveSessionForm();
		// Проверка
		int days = 0;
		try {
			days = Integer.parseInt(form.getSingleExtra(DAYS));
		} catch (Exception e) {
			getResult("error").addVariable("nocache", Math.round(Math.random() * 100000000) + "");
		}
		for (int i = 1; i < days; i += 2) {
			String dayVal = form.getSingleExtra(DAY_PREFIX + i);
			if (StringUtils.isBlank(dayVal))
				getResult("error").addVariable("nocache", Math.round(Math.random() * 100000000) + "");
		}
		// Отправка письма
		String topic = getVarSingleValue("topic");
		String emailToManager = getVarSingleValue("email");
		String requiredStr = getVarSingleValue("required");
		if (requiredStr == null) requiredStr = Strings.EMPTY;
		String spamStr = getVarSingleValue("spam");
		if (spamStr == null) spamStr = Strings.EMPTY;
		// Сообщение об ошибке в случае если не все поля заполнены
		String validationResult = validateInput(requiredStr, form);
		if (!StringUtils.isBlank(validationResult)) {
			return getResult("error").addVariable("nocache", Math.round(Math.random() * 100000000) + "");
		}
		// Если обнаружен спам - просто вернуть успешный результат без отправки письма
		if (isSpam(spamStr, form)) {
			removeSessionForm();
			return getResult("success");
		}
		String emailToUser = form.getValueStr("email");
		try {
			
			ExecutablePagePE userEmail = getExecutablePage("wellness_user");
			ExecutablePagePE managerEmail = getExecutablePage("wellness_manager");

			// Письмо юзеру
			
			Multipart mpUser = new MimeMultipart();
			MimeBodyPart textPartUser = new MimeBodyPart();
			mpUser.addBodyPart(textPartUser);
			ByteArrayOutputStream bosUser = new ByteArrayOutputStream();
			PageController.newSimple().executePage(userEmail, bosUser);
			textPartUser.setContent(bosUser.toString("UTF-8"), userEmail.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER) + ";charset=UTF-8");
			EmailUtils.sendGmailDefault(emailToUser, topic, mpUser);
			
			// Письмо менеждеру

			Multipart mpManager = new MimeMultipart();
			MimeBodyPart textPartManager = new MimeBodyPart();
			mpManager.addBodyPart(textPartManager);
			ByteArrayOutputStream bosManager = new ByteArrayOutputStream();
			PageController.newSimple().executePage(managerEmail, bosManager);
			textPartManager.setContent(bosManager.toString("UTF-8"), managerEmail.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER) + ";charset=UTF-8");
			EmailUtils.sendGmailDefault(emailToManager, topic, mpManager);
			

		} catch (Exception e) {
			try {
				ServerLogger.error("Error sending email message", e);
				return sendError("Email sending error", form);
			} catch (Exception e1) {
				ServerLogger.error("Error", e);
			}
		}
		// Удалить форму из сеанса
		removeSessionForm();
		return getResult("success").addVariable("nocache", Math.round(Math.random() * 100000000) + "");
	}

	private String validateInput(String requiredStr, ItemHttpPostForm form) {
		ArrayList<String> notSetParams = new ArrayList<String>();
		requiredStr = StringUtils.replace(requiredStr, " ", "");
		String[] required = StringUtils.split(requiredStr, ',');
		for (String reqParam : required) {
			boolean isValid = false;
			if (reqParam.indexOf('|') > 0)
				isValid = validateInputOr(reqParam, form);
			else
				isValid = !StringUtils.isBlank((String)form.getValue(reqParam));
			if (!isValid)
				notSetParams.add(reqParam);
		}
		return StringUtils.join(notSetParams, ',');
	}
	
	private boolean isSpam(String spamStr, ItemHttpPostForm form) {
		spamStr = StringUtils.replace(spamStr, " ", "");
		String[] spam = StringUtils.split(spamStr, ',');
		for (String spamParam : spam) {
			if (!StringUtils.isBlank(form.getValueStr(spamParam)) || !StringUtils.isBlank(form.getSingleExtra(spamParam)))
				return true;
		}
		return false;
	}

	private boolean validateInputOr(String requiredStr, ItemHttpPostForm form) {
		String[] required = StringUtils.split(requiredStr, '|');
		boolean isValid = false;
		for (String reqParam : required) {
			Object value = form.getValue(reqParam);
			if (value instanceof String)
				isValid |= !StringUtils.isBlank((String)value);
			else
				isValid |= value != null;
		}
		return isValid;
	}

	private ResultPE sendError(String message, ItemHttpPostForm postForm) throws Exception {
		Item userPost = postForm.createItem(getInitiator().getUserId(), getInitiator().getGroupId());
		getSessionMapper().saveTemporaryItem(userPost);
		return getResult("error").addVariable("message", message).addVariable("nocache", Math.round(Math.random() * 100000000) + "");
	}
	
}
