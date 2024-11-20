package ecommander.extra;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ecommander.application.extra.ItemUtils;
import ecommander.extra._generated.ItemNames;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ResultPE;
import ecommander.persistence.commandunits.DeleteItemBDUnit;
import ecommander.persistence.commandunits.SaveNewItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.users.User;

public class ReceiveEmailList extends Command {

	@Override
	public ResultPE execute() throws Exception {
		List<String> emails = getVarValues("email");
		if (emails.size() > 0) {
			Item allEmails = ItemUtils.ensureSingleRootItem(ItemNames.ALL_EMAILS, User.getDefaultUser(), false);
			List<Item> oldEmails = ItemQuery.newItemQuery(ItemNames.EMAILS).loadItems();
			Item newEmails = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.EMAILS), allEmails);
			for (String email : emails) {
				if (StringUtils.isNotBlank(email))
					newEmails.setValue(ItemNames.emails.EMAIL, email);
			}
			executeCommandUnit(new SaveNewItemDBUnit(newEmails));
			for (Item item : oldEmails) {
				executeCommandUnit(new DeleteItemBDUnit(item));
			}
			commitCommandUnits();
		}
		return null;
	}

}
