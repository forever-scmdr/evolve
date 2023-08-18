package extra;

import ecommander.fwk.BasicRegisterCommand;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import extra._generated.ItemNames;

import java.util.HashSet;

/**
 * Created by E on 5/4/2018.
 */
public class RegisterCommand extends BasicRegisterCommand {

	@Override
	protected boolean validate() throws Exception {
		Item form = getItemForm().getItemSingleTransient();
		boolean isPhys = form.getTypeId() == ItemTypeRegistry.getItemType(ItemNames.USER_PHYS).getTypeId();
		boolean hasError = false;
		if (isPhys) {
			HashSet<String> mandatorySet = new HashSet<>(CartManageCommand.MANDATORY_PHYS);
			mandatorySet.add(ItemNames.user_.EMAIL);
			mandatorySet.add(ItemNames.user_.PASSWORD);
			for (String mandatory : mandatorySet) {
				if (form.isValueEmpty(mandatory)) {
					getItemForm().setValidationError(form.getId(), mandatory, "Не заполнен параметр");
					hasError = true;
				}
			}
			removeSessionForm("register");
			saveSessionForm("register");
		} else {
			HashSet<String> mandatorySet = new HashSet<>(CartManageCommand.MANDATORY_JUR);
			mandatorySet.add(ItemNames.user_.EMAIL);
			mandatorySet.add(ItemNames.user_.PASSWORD);
			for (String mandatory : mandatorySet) {
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
