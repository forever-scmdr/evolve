package ecommander.extra;

import java.util.ArrayList;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;

import ecommander.application.extra.EmailUtils;
import ecommander.application.extra.ItemUtils;
import ecommander.common.Strings;
import ecommander.model.item.Item;
import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.model.item.ParameterDescription;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ItemHttpPostForm;
import ecommander.pages.elements.ResultPE;
import ecommander.persistence.commandunits.SaveNewItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.users.User;

public class SaveReview extends Command {

	@Override
	public ResultPE execute() throws Exception {

		String topic = getVarSingleValue("topic");
		String emailTo = getVarSingleValue("email");
		String requiredStr = getVarSingleValue("required");

		if (requiredStr == null)
			requiredStr = Strings.EMPTY;
		ItemHttpPostForm postForm = getItemForm();

		String roomNum = postForm.getSingleExtra((Object) "rn");

		String validationResult = validateInput(requiredStr, postForm);
		if (!StringUtils.isBlank(validationResult)) {
			saveSessionForm();
			return getRollbackResult("error_not_set");
		}
		long pid = (StringUtils.isNotBlank(roomNum)) ? Long.parseLong(roomNum) : (long) 0;
		Item parent = (pid > 0) ? ItemQuery.loadById(pid) : ItemUtils.ensureSingleRootItem("feedbacks", User.getDefaultUser(), false);

		Item review = postForm.createItem( User.getDefaultUser());
		review.setDirectParentId(parent.getId());

		executeAndCommitCommandUnits(new SaveNewItemDBUnit(review).ignoreUser(true));

		Multipart mp = new MimeMultipart();
		String mailMessage = "";

		ItemType postDesc = ItemTypeRegistry.getItemType(postForm.getItemTypeId());

		for (ParameterDescription param : postDesc.getParameterList()) {
			if(postForm.getValue(param.getId()) == null) continue;
			mailMessage += postDesc.getParameter(param.getName()).getCaption() + ": " + postForm.getValue(param.getId()) + "\r\n";
		}
		MimeBodyPart textPart = new MimeBodyPart();
		mp.addBodyPart(textPart);
		textPart.setContent(mailMessage, "text/plain;charset=UTF-8");
		
		EmailUtils.sendGmailDefault(emailTo, topic, mp);

		return getResult("success");
	}

	private boolean validateInputOr(String requiredStr, ItemHttpPostForm form) {
		String[] required = StringUtils.split(requiredStr, '|');
		boolean isValid = false;
		for (String reqParam : required) {
			Object value = form.getValue(reqParam);
			if (value instanceof String)
				isValid |= !StringUtils.isBlank((String) value);
			else
				isValid |= value != null;
		}
		return isValid;
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
				isValid = !StringUtils.isBlank((String) form.getValue(reqParam));
			if (!isValid)
				notSetParams.add(reqParam);
		}
		return StringUtils.join(notSetParams, ',');
	}
}
