package extra;

import ecommander.fwk.BasicCartManageCommand;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.pages.ResultPE;
import extra._generated.ItemNames;

import java.util.HashSet;

/**
 * Корзина
 * Created by E on 6/3/2018.
 */
public class CartManageCommand extends BasicCartManageCommand {

	public static final HashSet<String> MANDATORY_PHYS = new HashSet<>();
	public static final HashSet<String> MANDATORY_JUR = new HashSet<>();
	static {
		MANDATORY_PHYS.add(ItemNames.user_phys_.NAME);
		MANDATORY_PHYS.add(ItemNames.user_phys_.PHONE);

		MANDATORY_JUR.add(ItemNames.user_jur_.CONTACT_NAME);
		MANDATORY_JUR.add(ItemNames.user_jur_.PHONE);
		MANDATORY_JUR.add(ItemNames.user_jur_.ORGANIZATION);
	}

	@Override
	public ResultPE recalculate() throws Exception {
		super.recalculate();
		return getResult("ajax");
	}

	@Override
	protected boolean validate() throws Exception {
		Item form = getItemForm().getItemSingleTransient();
		boolean isPhys = form.getTypeId() == ItemTypeRegistry.getItemType(ItemNames.USER_PHYS).getTypeId();
		boolean hasError = false;
		if (isPhys) {
			for (String mandatory : MANDATORY_PHYS) {
				if (form.isValueEmpty(mandatory)) {
					getItemForm().setValidationError(form.getId(), mandatory, "Не заполнен параметр");
					hasError = true;
				}
			}
			removeSessionForm("customer_jur");
			saveSessionForm("customer_phys");
		} else {
			for (String mandatory : MANDATORY_JUR) {
				if (form.isValueEmpty(mandatory)) {
					getItemForm().setValidationError(form.getId(), mandatory, "Не заполнен параметр");
					hasError = true;
				}
			}
			removeSessionForm("customer_phys");
			saveSessionForm("customer_jur");
		}
		return !hasError;
	}

}
