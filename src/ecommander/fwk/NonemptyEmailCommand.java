package ecommander.fwk;

import ecommander.controllers.PageController;
import ecommander.model.*;
import ecommander.pages.*;
import ecommander.pages.var.StaticVariable;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Отправка сообщения на email с валидацией (проверка заполненности определенных полей).
 * В случае если не все обязательные поля заполнены, возвращается ошибка и отсылка не осуществляется
 * Параметры:
 * 		topic - тема письма
 * 		email - адрес отсылки
 * 		required - разделенный , и | список названий параметров. Через , перечисляются обязательные параметры,
 * 				   через | параметры, один из которых должен присутствовать
 * 		spam - разделенный , список названий параметров. Если заполнен хотя-бы один из них, то считается что 
 * 			   это спам
 * 	    form_name - название формы для сохранения
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

	private static final String TOPIC_PARAM = "topic";
	private static final String EMAIL_PARAM = "email";
	private static final String REQUIRED_PARAM = "required";
	private static final String SPAM_PARAM = "spam";
	private static final String FORM_NAME_PARAM = "form_name";
	private static final String TEMPLATE_PARAM = "template";

	private static final String ERROR_NOT_SET_RESULT = "error_not_set";
	//private static final String GENERAL_ERROR_RESULT = "general_error";
	private static final String SUCCESS_RESULT = "success";


	private static final String EMAIL_FORM = "email_form";

	@Override
	public ResultPE execute() throws Exception {
		String topic = getVarSingleValue(TOPIC_PARAM);
		String emailTo = getVarSingleValue(EMAIL_PARAM);
		String requiredStr = getVarSingleValue(REQUIRED_PARAM);
		if (StringUtils.isBlank(requiredStr)) requiredStr = Strings.EMPTY;
		String spamStr = getVarSingleValue(SPAM_PARAM);
		if (StringUtils.isBlank(spamStr)) spamStr = Strings.EMPTY;
		String formNameStr = getVarSingleValue(FORM_NAME_PARAM);
		if (StringUtils.isBlank(formNameStr)) formNameStr = EMAIL_FORM;
		MultipleHttpPostForm postForm = getItemForm();
		String templatePageName = getVarSingleValue(TEMPLATE_PARAM);
		// Сообщение об ошибке в случае если не все поля заполнены
		Item message = postForm.getItemSingleTransient();
		InputValues messageInput = new InputValues(); // Все данные, полученные из формы в единообразном виде (и параметры и extra)
		ItemType postDesc = ItemTypeRegistry.getItemType(message.getTypeId());
		// Переписать сначала все параметры
		for (ParameterDescription param : postDesc.getParameterList()) {
			ParameterDescription paramDesc = postDesc.getParameter(param.getName());
			Collection<SingleParameter> paramVals = message.getParamValues(paramDesc.getName());
			for (SingleParameter paramVal : paramVals) {
				messageInput.add(paramDesc.getName(), paramVal.getValue());
			}
			if (paramVals.size() == 0) {
				messageInput.add(paramDesc.getName(), "");
			}
		}
		// Переписать все extra
		for (String extraKey : message.getExtraKeys()) {
			ArrayList<Object> extras = message.getListExtra(extraKey);
			for (Object extra : extras) {
				if(extraKey.equals(SPAM_PARAM) && StringUtils.isNotBlank(extra.toString())) return getResult(SUCCESS_RESULT);
				messageInput.add(extraKey, extra);
			}
		}
		String validationResult = validateInput(requiredStr, messageInput);
		if (!StringUtils.isBlank(validationResult)) {
			saveSessionForm(formNameStr);
			return getRollbackResult(ERROR_NOT_SET_RESULT);
		}
		// Если обнаружен спам - просто вернуть успешный результат без отправки письма
		if (isSpam(spamStr, messageInput)) {
			return getResult(SUCCESS_RESULT);
		}
		try {
			// Если есть шаблон письма
			ExecutablePagePE emailPage = null;
			if (!StringUtils.isBlank(templatePageName)) {
				try {
					saveSessionForm(formNameStr);
					emailPage = getExecutablePage(templatePageName);
				} catch (Exception e) {
					emailPage = null;
				}
			}
			boolean hasTemplate = emailPage != null;
			// Формирование тела письма
			Multipart mp = new MimeMultipart();
			StringBuilder mailMessage = new StringBuilder();
			for (Object key : messageInput.getKeys()) {
				String paramName = key.toString();
				ArrayList<Object> values = messageInput.getExtraList(paramName);
				if (SPAM_PARAM.equals(key)) continue;
				if (values.size() > 0 && values.get(0) instanceof FileItem) {
					for (Object value : values) {
						FileItem file = (FileItem) value;
						DataSource dataSource = new ByteArrayDataSource(file.getInputStream(), file.getContentType());
						MimeBodyPart filePart = new MimeBodyPart();
						filePart.setDataHandler(new DataHandler(dataSource));
						filePart.setFileName(file.getName());
						mp.addBodyPart(filePart);
					}
				} else {
					if (hasTemplate) {
						emailPage.addVariable(new StaticVariable(paramName, values.toArray(new Object[0])));
					} else {
						ParameterDescription paramDesc = postDesc.getParameter(paramName);
						String caption = paramDesc != null ? paramDesc.getCaption() : paramName;
						String v = StringUtils.joinWith(", ", values);
						mailMessage.append(caption + ": " + v.substring(1,v.length()-1) + "\r\n");
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
				textPart.setContent(mailMessage.toString(), "text/plain;charset=UTF-8");
			}
			// Отправка письма
			topic = StringUtils.isBlank(postForm.getSingleStringExtra("topic"))? topic : postForm.getSingleStringExtra("topic");
			EmailUtils.sendGmailDefault(emailTo, topic, mp);
		} catch (Exception e) {
			try {
				saveSessionForm(formNameStr);
				ServerLogger.error("Error sending email message", e);
				return sendError("Email sending error", message);
			} catch (Exception e1) {
				ServerLogger.error("Error", e);
			}
		}
		// Удалить форму из сеанса
		removeSessionForm(formNameStr);
		return getResult(SUCCESS_RESULT);
	}

	private String validateInput(String requiredStr, InputValues message) {
		ArrayList<String> notSetParams = new ArrayList<>();
		requiredStr = StringUtils.replace(requiredStr, " ", "");
		String[] required = StringUtils.split(requiredStr, ',');
		for (String reqParam : required) {
			boolean isValid;
			if (reqParam.indexOf('|') > 0) {
				isValid = validateInputOr(reqParam, message);
			} else {
				isValid = message.get(reqParam) != null && StringUtils.isNotBlank(message.get(reqParam).toString());
			}
			if (!isValid)
				notSetParams.add(reqParam);
		}
		return StringUtils.join(notSetParams, ',');
	}
	
	private boolean isSpam(String spamStr, InputValues message) {
		spamStr = StringUtils.replace(spamStr, " ", "");
		String[] spam = StringUtils.split(spamStr, ',');
		for (String spamParam : spam) {
			if (message.get(spamParam) != null && StringUtils.isNotBlank(message.get(spamParam).toString()))
				return true;
		}
		return false;
	}

	private boolean validateInputOr(String requiredStr, InputValues message) {
		String[] required = StringUtils.split(requiredStr, '|');
		boolean isValid = false;
		for (String reqParam : required) {
			Object value = message.get(reqParam);
			if (value instanceof String)
				isValid |= StringUtils.isNotBlank((String)value);
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