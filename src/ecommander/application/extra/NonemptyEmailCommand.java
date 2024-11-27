package ecommander.application.extra;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import ecommander.common.ServerLogger;
import ecommander.common.Strings;
import ecommander.controllers.PageController;
import ecommander.extra.SubscribeCommand;
import ecommander.model.item.Item;
import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.model.item.ParameterDescription;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ExecutablePagePE;
import ecommander.pages.elements.ItemHttpPostForm;
import ecommander.pages.elements.PagePE;
import ecommander.pages.elements.ResultPE;
import ecommander.pages.elements.variables.StaticVariablePE;
import ecommander.users.User;
import nl.captcha.Captcha;
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

	@Override
	public ResultPE execute() throws Exception {

		Captcha captcha = (Captcha) getSessionObject("capt");

		String answer = getVarSingleValue("answer");
		if (!captcha.isCorrect(answer))
		{
			return getResult("capcha_error");
		}


		String topic = getVarSingleValue("topic");
		String emailTo = getVarSingleValue("email");
		String requiredStr = getVarSingleValue("required");
		//--
		String createSubscriber = getVarSingleValue("create_subscriber");
		createSubscriber = createSubscriber == null ? "no" : createSubscriber;
		//--
		if (requiredStr == null) requiredStr = Strings.EMPTY;
		String spamStr = getVarSingleValue("spam");
		if (spamStr == null) spamStr = Strings.EMPTY;
		ItemHttpPostForm postForm = getItemForm();
		
		
		Item form = postForm.createItem(User.getDefaultUser());
		getSessionMapper().removeItems(form.getTypeName());
		getSessionMapper().saveTemporaryItem(form);
		
		String templatePageName = getVarSingleValue("template");
		// Сообщение об ошибке в случае если не все поля заполнены
		String validationResult = validateInput(requiredStr, postForm);
		if (!StringUtils.isBlank(validationResult)) {
			saveSessionForm();
			return getRollbackResult("error_not_set");
		}
		// Если обнаружен спам - просто вернуть успешный результат без отправки письма
//		if (isSpam(spamStr, postForm)) {
//			return getResult("success");
//		}
		try {
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
			ItemType postDesc = ItemTypeRegistry.getItemType(postForm.getItemTypeId());
			for (ParameterDescription param : postDesc.getParameterList()) {
				ParameterDescription paramDesc = postDesc.getParameter(param.getName());
				if(paramDesc == null) continue;
				if (ArrayUtils.contains(spam, paramDesc.getName()))
					continue;
				if (paramDesc.getDataType().isFile()) {
					Object value = postForm.getValue(param.getName());
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
						String varValue = (String) (postForm.getValue(param.getId()) == null? "" : postForm.getValue(param.getId()));
						
						emailPage.addVariable(new StaticVariablePE(varName, varValue));
					} else if(StringUtils.isNotBlank((String)postForm.getValue(param.getId()))){
						mailMessage += postDesc.getParameter(param.getName()).getCaption() + ": "
								+ postForm.getValue(param.getId()) + "\r\n\r";
					}
				}
			}
			// Вывод дополнительного айтема (нужно на термобресте)
			Item extra = getExtraItem();
			if (extra != null) {
				mailMessage += "\r\nДОПОЛНИТЕЛЬНО\r\n\r\n";
				for (ParameterDescription param : extra.getItemType().getParameterList()) {
					if (!extra.getParameterByName(param.getName()).isEmpty()) {
						String value = StringUtils.join(extra.outputValues(param.getName()), ", ");
						mailMessage += param.getCaption() + ": " + value + "\r\n\r";
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
				saveSessionForm();
				ServerLogger.error("Error sending email message", e);
				return sendError("Email sending error", postForm);
			} catch (Exception e1) {
				ServerLogger.error("Error", e);
			}
		}
		// Удалить форму из сеанса
		
		if(createSubscriber.equalsIgnoreCase("yes")){
			SubscribeCommand sc = new SubscribeCommand();
			sc.createSubscriber(postForm);
		}
		
		removeSessionForm();
		getSessionMapper().removeItems(form.getTypeName());
		
		String successPage = getVarSingleValue("success_page");
		if(StringUtils.isNotBlank(successPage)) {
			return getResult("success_page");
		}
		
		return getResult("success");
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
		ResultPE result = getRollbackResult("general_error");
		result.addVariable("message", message);
		return result;
	}
	
	protected Item getExtraItem() {
		return null;
	}
}