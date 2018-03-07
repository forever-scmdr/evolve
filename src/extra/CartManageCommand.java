package extra;

import ecommander.fwk.BasicCartManageCommand;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import extra._generated.ItemNames;

import java.util.HashSet;

/**
 * Корзина
 * Created by E on 6/3/2018.
 */
public class CartManageCommand extends BasicCartManageCommand {

	private static final HashSet<String> MANDATORY_PHYS = new HashSet<>();
	private static final HashSet<String> MANDATORY_JUR = new HashSet<>();
	static {
		MANDATORY_PHYS.add(ItemNames.user_phys.NAME);
		MANDATORY_PHYS.add(ItemNames.user_phys.ADDRESS);
		MANDATORY_PHYS.add(ItemNames.user_phys.EMAIL);
		MANDATORY_PHYS.add(ItemNames.user_phys.PHONE);
		MANDATORY_PHYS.add(ItemNames.user_phys.SHIP_TYPE);

		MANDATORY_JUR.add(ItemNames.user_jur.ACCOUNT);
		MANDATORY_JUR.add(ItemNames.user_jur.ADDRESS);
		MANDATORY_JUR.add(ItemNames.user_jur.BANK);
		MANDATORY_JUR.add(ItemNames.user_jur.BANK_ADDRESS);
		MANDATORY_JUR.add(ItemNames.user_jur.BANK_CODE);
		MANDATORY_JUR.add(ItemNames.user_jur.CONTACT_NAME);
		MANDATORY_JUR.add(ItemNames.user_jur.CONTACT_PHONE);
		MANDATORY_JUR.add(ItemNames.user_jur.DIRECTOR);
		MANDATORY_JUR.add(ItemNames.user_jur.EMAIL);
		MANDATORY_JUR.add(ItemNames.user_jur.ORGANIZATION);
		MANDATORY_JUR.add(ItemNames.user_jur.SHIP_TYPE);
		MANDATORY_JUR.add(ItemNames.user_jur.UNP);
	}


	@Override
	protected boolean validate() throws Exception {
		Item form = getItemForm().getSingleItem();
		boolean isPhys = form.getTypeId() == ItemTypeRegistry.getItemType(ItemNames.USER_PHYS).getTypeId();
		boolean hasError = false;
		if (isPhys) {
			for (String mandatory : MANDATORY_PHYS) {
				if (form.isValueEmpty(mandatory)) {
					getItemForm().setValidationError(form.getId(), mandatory, "Не заполнен параметр");
					hasError = true;
				}
			}
			if (hasError) {
				removeSessionForm("customer_jur");
				saveSessionForm("customer_phys");
			}
		} else {
			for (String mandatory : MANDATORY_JUR) {
				if (form.isValueEmpty(mandatory)) {
					getItemForm().setValidationError(form.getId(), mandatory, "Не заполнен параметр");
					hasError = true;
				}
			}
			if (hasError) {
				removeSessionForm("customer_phys");
				saveSessionForm("customer_jur");
			}
		}
		return !hasError;
	}
}
