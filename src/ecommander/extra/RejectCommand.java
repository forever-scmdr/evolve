package ecommander.extra;

import org.apache.commons.lang3.StringUtils;

import ecommander.application.extra.EmailUtils;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ResultPE;

public class RejectCommand extends Command{
	private final String SUCCESS = "success";

	@Override
	public ResultPE execute() throws Exception {
		String clientEmail = getVarSingleValue("client_email");
		String org = getVarSingleValue("organization");
		if(StringUtils.isNotBlank(clientEmail)) {
			EmailUtils.sendTextGmailDefault(getVarSingleValue("email"), "Отказ от рассылки. E-mail: "+clientEmail, "Контрагент "+org+" отказлся от рассылки на email " + clientEmail);
		}
		return getResult(SUCCESS);
	}

}
