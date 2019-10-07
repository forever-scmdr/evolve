package extra;

import ecommander.fwk.BasicCartManageCommand;
import ecommander.fwk.ItemUtils;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.model.datatypes.LongDataType;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Корзина
 * Created by E on 6/3/2018.
 */
public class CartManageCommand extends BasicCartManageCommand {

	private static final String CART_TEMP_CATALOG = "cart_temp_catalog";
	private static final String CART_TEMP = "cart_temp";
	private static final String USER = "user";
	private static final String TIME = "time";

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
		MANDATORY_JUR.add(ItemNames.user_jur_.UNP);
	}


	@Override
	protected boolean validate() throws Exception {
		Item form = getItemForm().getTransientSingleItem();
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
    protected void saveCookie() throws Exception {
		checkStrategy();
        ensureCart();
        ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
        ArrayList<String> codeQtys = new ArrayList<>();
        for (Item bought : boughts) {
            Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
            double quantity = bought.getDoubleValue(QTY_TOTAL_PARAM);
            codeQtys.add(product.getStringValue(CODE_PARAM) + ":" + quantity);
        }
		Item cookieCatalog = ItemUtils.ensuteSingleRootAnonymousItem(CART_TEMP_CATALOG, User.getDefaultUser());
		String cookieId = getVarSingleValue(CART_COOKIE);
		Item cartCookie = null;
		if (StringUtils.isNotBlank(cookieId)) {
			Long id = new Long(-1);
			try {
				id = Long.parseLong(cookieId);
			} catch (Exception e) {}
			cartCookie = ItemQuery.loadByIdVisible(id);
		}
		boolean isNotAnonymous = StringUtils.isNotBlank(getInitiator().getName());
        if (codeQtys.size() > 0) {
			if (isNotAnonymous) {
				// Удалить все старые куки
				List<Item> oldCookies = ItemQuery.loadByParamValue(CART_TEMP, USER, getInitiator().getName());
				for (Item oldCookie : oldCookies) {
					if (cartCookie == null || cartCookie.getId() != oldCookie.getId())
						executeAndCommitCommandUnits(ItemStatusDBUnit.delete(oldCookie).ignoreUser());
				}
			}
			if (cartCookie == null) {
				cartCookie = Item.newChildItem(ItemTypeRegistry.getItemType(CART_TEMP), cookieCatalog);
			}
            String cookie = StringUtils.join(codeQtys, '/');
			if (StringUtils.isNotBlank(getInitiator().getName()))
				cartCookie.setValue(USER, getInitiator().getName());
			cartCookie.setValue(CART_COOKIE, cookie);
			cartCookie.setValue(TIME, System.currentTimeMillis());
			executeAndCommitCommandUnits(SaveItemDBUnit.get(cartCookie).ignoreUser());
            setCookieVariable(CART_COOKIE, cartCookie.getId() + "");
        } else {
        	if (cartCookie != null)
        		executeAndCommitCommandUnits(ItemStatusDBUnit.delete(cartCookie).ignoreUser());
            setCookieVariable(CART_COOKIE, null);
        }
    }

    @Override
    public ResultPE restoreFromCookie() throws Exception {
		checkStrategy();
		loadCart();
		if (cart != null)
			return null;
		Long cookieId = new Long(-1);
		Item cartCookie = null;
		String id = getVarSingleValue(CART_COOKIE);
		if (StringUtils.isBlank(id)) {
			if (StringUtils.isBlank(getInitiator().getName()))
				return null;
			cartCookie = ItemQuery.loadSingleItemByParamValue(CART_TEMP, USER, getInitiator().getName());
		} else {
			try {
				cookieId = Long.parseLong(id);
			} catch (Exception e) {}
			if (cookieId <= 0)
				return null;
			cartCookie = ItemQuery.loadByIdVisible(cookieId);
		}
		if (cartCookie == null || cartCookie.isValueEmpty(CART_COOKIE))
			return null;
		String[] codeQtys = StringUtils.split(cartCookie.getStringValue(CART_COOKIE), '/');
		for (String codeQty : codeQtys) {
			String[] pair = StringUtils.split(codeQty, ':');
			double qty = DoubleDataType.parse(pair[1]);
			addProduct(pair[0], qty);
		}
		recalculateCart();
		return null;
    }

	@Override
	protected void postProcessCartClean() throws Exception {
		String id = getVarSingleValue(CART_COOKIE);
		if (StringUtils.isBlank(id))
			return;
		Long cookieId = new Long(-1);
		try {
			cookieId = Long.parseLong(id);
		} catch (Exception e) {}
		if (cookieId <= 0)
			return;
		Item cartCookie = ItemQuery.loadById(cookieId);
		if (cartCookie != null)
			executeAndCommitCommandUnits(ItemStatusDBUnit.delete(cartCookie).ignoreUser());
	}
}
