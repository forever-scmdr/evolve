package extra;

import ecommander.fwk.BasicRegisterCommand;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import extra._generated.ItemNames;

/**
 * Created by E on 5/4/2018.
 */
public class RegisterCommand extends BasicRegisterCommand {

	@Override
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
