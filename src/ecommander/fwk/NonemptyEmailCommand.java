package ecommander.fwk;

import ecommander.controllers.PageController;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;
import ecommander.pages.*;
import ecommander.pages.var.StaticVariable;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
/**
 * Отправка сообщения на email с валидацией (проверка заполненности определенных полей).
 * В случае если не все обязательные поля заполнены, возвращается ошибка и отсылка не осуществляется
 * Параметры:
 * 		form - форма обратной связи (itemform)
 * 		topic - тема письма
 * 		email - адрес отсылки
 * 		required - разделенный , и | список названий параметров. Через , перечисляются обязательные параметры,
 * 				   через | параметры, один из которых должен присутствовать
 * 		spam - разделенный , список названий параметров. Если заполнен хотя-бы один из них, то считается что 
 * 			   это спам
 * 		template - страница, которая содержит шаблон письма (необязательный параметр)
 *
 * Результаты:
 * 		error_not_set - не заполнены обязательные поля
 * 		general_error - какая-то ошибка отправки почты
 *		success - успешное выполнение
 *
 * Пояснение к параметру template:
 * Если есть параметр template, то он должен хранить название страницы, которая должна выводить шаблон письма.
 * Текстовые данные, которые вводит пользователь, передаются на эту страницу как переменные страницы.
 * Каждый параметр формы соответствует переменной (совпадают имена параметров и навзвания переменных)
 * Если такой шаблон существует, то в теле письма отправляется результат выполнения этой старницы. Если пользователь
 * присоединял файлы, то они добавляются в виде вложений в письмо.
 *
 * @author E
 *
 */
public class NonemptyEmailCommand extends Command {

	private static final String EMAIL_FORM = "email_form";

	@Override
	public ResultPE execute() throws Exception {
		String topic = getVarSingleValue("topic");
		String emailTo = getVarSingleValue("email");
		String requiredStr = getVarSingleValue("required");
		if (requiredStr == null) requiredStr = Strings.EMPTY;
		String spamStr = getVarSingleValue("spam");
		if (spamStr == null) spamStr = Strings.EMPTY;
		MultipleHttpPostForm postForm = getItemForm();
		String templatePageName = getVarSingleValue("template");
		// Сообщение об ошибке в случае если не все поля заполнены
		Item message = postForm.getItemTree().getItem();
		String validationResult = validateInput(requiredStr, message);
		if (!StringUtils.isBlank(validationResult)) {
			saveSessionForm(EMAIL_FORM);
			return getRollbackResult("error_not_set");
		}
		// Если обнаружен спам - просто вернуть успешный результат без отправки письма
		if (isSpam(spamStr, message)) {
			return getResult("success");
		}
		try {
			ItemType postDesc = ItemTypeRegistry.getItemType(message.getTypeId());
			// Если есть шаблон письма
			ExecutablePagePE emailPage = null;
			if (!StringUtils.isBlank(templatePageName)) {
				try {
					emailPage = getExecutablePage(templatePageName);
				} catch (Exception e) {
					emailPage = null;
				}
			}
			boolean hasTemplate = emailPage != null;
			// Формирование тела письма
			Multipart mp = new MimeMultipart();
			String mailMessage = "";
			spamStr = StringUtils.replace(spamStr, " ", "");
			String[] spam = StringUtils.split(spamStr, ',');
			for (ParameterDescription param : postDesc.getParameterList()) {
				ParameterDescription paramDesc = postDesc.getParameter(param.getName());
				if (ArrayUtils.contains(spam, paramDesc.getName()))
					continue;
				if (paramDesc.getDataType().isFile()) {
					Object value = message.getValue(param.getName());
					if (value != null && value instanceof FileItem) {
						FileItem file = (FileItem) value;
						DataSource dataSource = new ByteArrayDataSource(file.getInputStream(), file.getContentType());
						MimeBodyPart filePart = new MimeBodyPart();
						filePart.setDataHandler(new DataHandler(dataSource));
						filePart.setFileName(file.getName());
						mp.addBodyPart(filePart);
					}
				} else {
					if (hasTemplate) {
						String varName = param.getName();
						String varValue = message.getValue(param.getId()).toString();
						emailPage.addVariable(new StaticVariable(varName, varValue));
					} else if (StringUtils.isNotBlank(message.getStringValue(param.getName()))) {
						mailMessage += postDesc.getParameter(param.getName()).getCaption() + ": "
								+ message.getValue(param.getId()) + "\r\n";
					}
				}
			}
			MimeBodyPart textPart = new MimeBodyPart();
			mp.addBodyPart(textPart);
			// Если у письма есть шаблон, выполнить страницу и установить нужный тип ответа
			if (hasTemplate) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				PageController.newSimple().executePage(emailPage, bos);
				textPart.setContent(bos.toString("UTF-8"), emailPage.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER) + ";charset=UTF-8");
			}
			// Простое письмо
			else if (!StringUtils.isBlank(mailMessage)) {
				textPart.setContent(mailMessage, "text/plain;charset=UTF-8");
			}
			// Отправка письма
			EmailUtils.sendGmailDefault(emailTo, topic, mp);
		} catch (Exception e) {
			try {
				saveSessionForm(EMAIL_FORM);
				ServerLogger.error("Error sending email message", e);
				return sendError("Email sending error", message);
			} catch (Exception e1) {
				ServerLogger.error("Error", e);
			}
		}
		// Удалить форму из сеанса
		removeSessionForm(EMAIL_FORM);
		return getResult("success");
	}

	private String validateInput(String requiredStr, Item message) {
		ArrayList<String> notSetParams = new ArrayList<>();
		requiredStr = StringUtils.replace(requiredStr, " ", "");
		String[] required = StringUtils.split(requiredStr, ',');
		for (String reqParam : required) {
			boolean isValid;
			if (reqParam.indexOf('|') > 0)
				isValid = validateInputOr(reqParam, message);
			else
				isValid = !StringUtils.isBlank((String)message.getValue(reqParam));
			if (!isValid)
				notSetParams.add(reqParam);
		}
		return StringUtils.join(notSetParams, ',');
	}
	
	private boolean isSpam(String spamStr, Item message) {
		spamStr = StringUtils.replace(spamStr, " ", "");
		String[] spam = StringUtils.split(spamStr, ',');
		for (String spamParam : spam) {
			if (!StringUtils.isBlank(message.getStringValue(spamParam)) || !StringUtils.isBlank(message.getStringExtra(spamParam)))
				return true;
		}
		return false;
	}

	private boolean validateInputOr(String requiredStr, Item message) {
		String[] required = StringUtils.split(requiredStr, '|');
		boolean isValid = false;
		for (String reqParam : required) {
			Object value = message.getValue(reqParam);
			if (value instanceof String)
				isValid |= !StringUtils.isBlank((String)value);
			else
				isValid |= value != null;
		}
		return isValid;
	}

	private ResultPE sendError(String error, Item message) throws Exception {
		getSessionMapper().saveTemporaryItem(message);
		ResultPE result = getRollbackResult("general_error");
		result.addVariable("message", error);
		return result;
	}
}