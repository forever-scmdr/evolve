package extra;

import ecommander.fwk.BasicRegisterCommand;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

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
			String mandatoryPhys = getVarSingleValue("mandatory_phys");
			for (String mandatory : getMandatory(mandatoryPhys, ItemNames.USER_PHYS,CartManageCommand.MANDATORY_PHYS)) {
				if (form.isValueEmpty(mandatory)) {
					getItemForm().setValidationError(form.getId(), mandatory, "Не заполнен параметр");
					hasError = true;
				}
			}
			removeSessionForm("register");
			saveSessionForm("register");
		} else {
			String mandatoryJur = getVarSingleValue("mandatory_jur");
			for (String mandatory : getMandatory(mandatoryJur, ItemNames.USER_JUR,CartManageCommand.MANDATORY_JUR)) {
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

	/**
	 * Обязательные поля для заданного айтема, передается название айтема, переменная с полями и поля по умолчанию
	 * @param mandatoryVarValue
	 * @param itemName
	 * @param defaultMandatory
	 * @return
	 */
	public static HashSet<String> getMandatory(String mandatoryVarValue, String itemName, HashSet<String> defaultMandatory) {
		HashSet<String> mandatorySet = new HashSet<>();
		if (StringUtils.isNotBlank(mandatoryVarValue)) {
			String[] params = StringUtils.split(mandatoryVarValue, ",; ");
			ItemType type = ItemTypeRegistry.getItemType(itemName);
			for (String param : params) {
				if (type.hasParameter(param)) {
					mandatorySet.add(param);
				}
			}
		} else {
			mandatorySet.addAll(defaultMandatory);
		}
		return  mandatorySet;
	}
}
