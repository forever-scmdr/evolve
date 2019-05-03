package extra;

import ecommander.fwk.ItemUtils;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;

/**
 * Команда для регистрации пользователя
 * Created by E on 5/4/2018.
 */
public class JustEmailRegisterCommand extends RegisterCommand {

	@Override
	public ResultPE execute() throws Exception {
		return null;
	}

	public ResultPE register() throws Exception {
		if (!validate()) {
			return getResult("not_set");
		}
		Item form = getItemForm().getItemSingleTransient();
		if (form.isValueEmpty(PASSWORD_PARAM)) {
			return getResult("not_set");
		}
		Item catalog = ItemUtils.ensureSingleRootItem(REGISTERED_CATALOG_ITEM, User.getDefaultUser(),
				UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		String userName = (StringUtils.isEmpty(form.getStringValue(EMAIL_PARAM)))? form.getStringValue(PHONE_PARAM) : form.getStringValue(EMAIL_PARAM);
		Item existingUser = new ItemQuery(USER_ITEM).addParameterEqualsCriteria(EMAIL_PARAM, userName).loadFirstItem();
		if (existingUser != null) {
			return getResult("user_exists");
		}
		form.setContextPrimaryParentId(catalog.getId());
		form.setOwner(UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(form).ignoreUser().noTriggerExtra());
		return getResult("success");
	}
}
