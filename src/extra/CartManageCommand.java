package extra;

import ecommander.fwk.BasicCartManageCommand;
import ecommander.fwk.ItemUtils;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.pages.MultipleHttpPostForm;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.Discounts;
import extra._generated.ItemNames;
import extra._generated.User_jur;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
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
		//MANDATORY_PHYS.add(ItemNames.user_phys_.ADDRESS);
		//MANDATORY_PHYS.add(ItemNames.user_phys_.EMAIL);
		MANDATORY_PHYS.add(ItemNames.user_phys_.PHONE);
		//MANDATORY_PHYS.add(ItemNames.user_phys_.SHIP_TYPE);

		//MANDATORY_JUR.add(ItemNames.user_jur_.ACCOUNT);
		//MANDATORY_JUR.add(ItemNames.user_jur_.ADDRESS);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK_ADDRESS);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK_CODE);
		MANDATORY_JUR.add(ItemNames.user_jur_.CONTACT_NAME);
		//MANDATORY_JUR.add(ItemNames.user_jur_.CONTACT_PHONE);
		MANDATORY_JUR.add(ItemNames.user_jur_.PHONE);
		//MANDATORY_JUR.add(ItemNames.user_jur_.DIRECTOR);
		//MANDATORY_JUR.add(ItemNames.user_jur_.EMAIL);
		MANDATORY_JUR.add(ItemNames.user_jur_.ORGANIZATION);
		//MANDATORY_JUR.add(ItemNames.user_jur_.SHIP_TYPE);
		//MANDATORY_JUR.add(ItemNames.user_jur_.UNP);
	}


	private Discounts discounts;


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

	@Override
	protected boolean recalculateCart(String...priceParamName) throws Exception {
		if (discounts == null) {
			Item common = ItemUtils.ensureSingleRootAnonymousItem(ItemNames.COMMON, User.getDefaultUser());
			discounts = Discounts.get(ItemUtils.ensureSingleItem(ItemNames.DISCOUNTS,
					User.getDefaultUser(), common.getId(), common.getOwnerGroupId(), common.getOwnerUserId()));
		}
		double discount = 0.0d;
		BigDecimal originalSum = cart.getDecimalValue(SUM_PARAM);
		if (originalSum.compareTo(discounts.get_sum_more()) >= 0) {
			discount += discounts.get_sum_discount();
		}
		User_jur user = User_jur.get(new ItemQuery(ItemNames.USER_JUR).setUser(getInitiator()).loadFirstItem());
		if (user != null) {
			discount += user.getDefault_discount(0.0d);
		}
		boolean success = super.recalculateCart(user != null ? ItemNames.product_.PRICE_OPT : PRICE_PARAM);
		MultipleHttpPostForm userForm = getSessionForm("customer_jur");
		if (userForm != null) {
			user = User_jur.get(userForm.getItemSingleTransient());
			if (user != null) {
				if (StringUtils.containsIgnoreCase(user.get_ship_type(), "самовывоз")) {
					discount += discounts.get_self_delivery();
				} else if (StringUtils.containsIgnoreCase(user.get_ship_type(), "автолайт")) {
					discount -= discounts.get_autolight();
				} else if (StringUtils.containsIgnoreCase(user.get_ship_type(), "доставка")) {
					discount -= discounts.get_delivery();
				}
				if (StringUtils.containsIgnoreCase(user.get_pay_type(), "предоплата")) {
					discount += discounts.get_pay_first();
				}
			}
		}
		BigDecimal discountedSum = originalSum.multiply(new BigDecimal((100 - discount) / 100));
		cart.setValue(ItemNames.cart_.SUM_DISCOUNT, discountedSum);
		getSessionMapper().saveTemporaryItem(cart);
		return success;
	}
}
