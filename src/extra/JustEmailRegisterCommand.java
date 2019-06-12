package extra;

import ecommander.fwk.*;
import ecommander.model.*;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.commandunits.SaveNewUserDBUnit;
import ecommander.persistence.commandunits.UpdateUserDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

/**
 * Команда для регистрации пользователя
 * Created by E on 5/4/2018.
 */
public class JustEmailRegisterCommand extends Command {
	public static final String PASSWORD_PARAM = "password";
	public static final String EMAIL_PARAM = "email";
	public static final String PHONE_PARAM = "phone";
	public static final String USER_ITEM = "user";

	public static final String REGISTERED_CATALOG_ITEM = "registered_catalog";

	@Override
	public ResultPE execute() throws Exception {
		return null;
	}

	public ResultPE register() throws Exception {
		if (!validate()) {
			return getResult("not_set");
		}
		Item form = getItemForm().getTransientSingleItem();
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
		executeAndCommitCommandUnits(SaveItemDBUnit.get(form).ignoreUser());
		return getResult("success");
	}


	protected boolean validate() throws Exception {
		Item form = getItemForm().getTransientSingleItem();
		boolean isPhys = form.getTypeId() == ItemTypeRegistry.getItemType(ItemNames.USER_PHYS).getTypeId();
		boolean hasError = false;
		if (isPhys) {
			for (String mandatory : CartManageCommand.MANDATORY_PHYS) {
				if (form.isValueEmpty(mandatory)) {
					getItemForm().setValidationError(form.getId(), mandatory, "Не заполнен параметр");
					hasError = true;
				}
			}
			removeSessionForm("register");
			saveSessionForm("register");
		} else {
			for (String mandatory : CartManageCommand.MANDATORY_JUR) {
				if (form.isValueEmpty(mandatory)) {
					getItemForm().setValidationError(form.getId(), mandatory, "Не заполнен параметр");
					hasError = true;
				}
			}
			removeSessionForm("register");
			saveSessionForm("register");
		}
		return !hasError;
	}
}
