package ecommander.extra;

import org.apache.commons.lang3.StringUtils;

import ecommander.application.extra.ItemUtils;
import ecommander.application.extra.NonemptyEmailCommand;
import ecommander.extra._generated.ItemNames;
import ecommander.model.item.Item;
import ecommander.pages.elements.ResultPE;
import ecommander.persistence.commandunits.SaveNewItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.users.User;

public class NonemptyEmailCheckCommand extends NonemptyEmailCommand {

	private Item agent = null;
	
	@Override
	public ResultPE execute() throws Exception {
		String email = getItemForm().getValueStr("email");
		String action = getVarSingleValue("action");
		String targetSrc = getVarSingleValue("target_src");
		if (StringUtils.isBlank(email)) {
			saveSessionForm();
			return getRollbackResult("error_not_set");
		}
		ResultPE result = null;
		if (StringUtils.equals(action, "check")) {
			Item existingAgent = ItemQuery.loadSingleItemByParamValue(ItemNames.AGENT, ItemNames.agent.EMAIL, email);
			if (existingAgent == null) {
				saveSessionForm();
				return getResult("to_register");
			} else if (StringUtils.isNotBlank(targetSrc)) {
				return getResult("do_initial_action");
			}
			agent = existingAgent;
			result = super.execute();
		}
		if (StringUtils.equals(action, "register")) {
			result = super.execute();
			if (StringUtils.equals(result.getName(), "success")) {
				Item allEmails = ItemUtils.ensureSingleRootItem(ItemNames.ALL_EMAILS, User.getDefaultUser(), false);
				Item emails = ItemUtils.ensureSingleItem(ItemNames.EMAILS, allEmails.getId(), User.getDefaultUser(), false);
				Item agent = getItemForm().createItem(User.getDefaultUser(), emails.getId());
				//emails.setValue(ItemNames.emails.EMAIL, email);
				//executeAndCommitCommandUnits(new UpdateItemDBUnit(emails));
				executeAndCommitCommandUnits(new SaveNewItemDBUnit(agent));
				return getResult("to_initial");
			}
		}
		return result;
	}

	@Override
	protected Item getExtraItem() {
		return agent;
	}

}
