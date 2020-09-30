package extra;

import ecommander.fwk.ItemUtils;
import ecommander.fwk.Strings;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.MultipleHttpPostForm;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;

public class AddCommentCommand extends Command {
	private static final String REQUIRED_PARAM = "required";
	private static final String SPAM_PARAM = "spam";

	private static final String ERROR_NOT_SET_RESULT = "error_not_set";
	private static final String GENERAL_ERROR_RESULT = "general_error";
	private static final String SUCCESS_RESULT = "success";
	private static final String FORM_NAME_PARAM = "form_name";
	private static final String UNMODERATED = "unmoderated";

	@Override
	public ResultPE execute() throws Exception {
		MultipleHttpPostForm postForm = getItemForm();
		Item message = postForm.getItemSingleTransient();

		// Если обнаружен спам - просто вернуть успешный результат без отправки письма
		String spamStr = getVarSingleValue(SPAM_PARAM);
		if (StringUtils.isBlank(spamStr)) spamStr = Strings.EMPTY;
		if (isSpam(spamStr, message)) return getResult(SUCCESS_RESULT);

		//Все ли введено
		String requiredStr = getVarSingleValue(REQUIRED_PARAM);
		if (StringUtils.isBlank(requiredStr)) requiredStr = Strings.EMPTY;

		String formNameStr = getVarSingleValue(FORM_NAME_PARAM);
		String validationResult = validateInput(requiredStr, message);
		if (!StringUtils.isBlank(validationResult)) {
			saveSessionForm(formNameStr);
			return getRollbackResult(ERROR_NOT_SET_RESULT);
		}
		try {
			Item parent = ItemQuery.loadSingleItemByName(UNMODERATED);
			Item comment = ItemUtils.newChildItem("comment", parent);
			Item.updateParamValues(message, comment);
			comment.setValue("date", new Date().getTime());
			executeAndCommitCommandUnits(SaveItemDBUnit.get(comment));
		}catch (Exception e){
			return getResult(GENERAL_ERROR_RESULT);
		}
		return getResult(SUCCESS_RESULT);
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
				isValid = !StringUtils.isBlank((String) message.getValue(reqParam));
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
				isValid |= !StringUtils.isBlank((String) value);
			else
				isValid |= value != null;
		}
		return isValid;
	}
}
