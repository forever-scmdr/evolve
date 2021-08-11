package extra;

import ecommander.fwk.BasicCartManageCommand;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.pages.ResultPE;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Корзина
 * Created by E on 6/3/2018.
 */
public class CartManageCommand extends BasicCartManageCommand {

	public static final HashSet<String> MANDATORY_PHYS = new HashSet<>();
	public static final HashSet<String> MANDATORY_JUR = new HashSet<>();
	public static final String TABLE_PRODUCT_COOKIE = "from_table";
	static {
		MANDATORY_PHYS.add(ItemNames.user_phys_.NAME);
		//MANDATORY_PHYS.add(ItemNames.user_phys_.ADDRESS);
		MANDATORY_PHYS.add(ItemNames.user_phys_.EMAIL);
		MANDATORY_PHYS.add(ItemNames.user_phys_.PHONE);
		MANDATORY_PHYS.add(ItemNames.user_phys_.SHIP_TYPE);
		MANDATORY_PHYS.add(ItemNames.user_phys_.PAY_TYPE);

		//MANDATORY_JUR.add(ItemNames.user_jur_.ACCOUNT);
		//MANDATORY_JUR.add(ItemNames.user_jur_.ADDRESS);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK_ADDRESS);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK_CODE);
		MANDATORY_JUR.add(ItemNames.user_jur_.CONTACT_NAME);
		//MANDATORY_JUR.add(ItemNames.user_jur_.CONTACT_PHONE);
		MANDATORY_JUR.add(ItemNames.user_jur_.PHONE);
		//MANDATORY_JUR.add(ItemNames.user_jur_.DIRECTOR);
		MANDATORY_JUR.add(ItemNames.user_jur_.EMAIL);
		MANDATORY_JUR.add(ItemNames.user_jur_.ORGANIZATION);
		//MANDATORY_JUR.add(ItemNames.user_jur_.SHIP_TYPE);
		MANDATORY_JUR.add(ItemNames.user_jur_.UNP);
	}

	@Override
	public ResultPE recalculate() throws Exception {
		super.recalculate();
		return getResult("cart");
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


	public ResultPE addFromTable() throws Exception {
		//Get posted vars
		String code = getVarSingleValue("code");
		String name = getVarSingleValue("name");
		String price = getVarSingleValue("price");
		String qtyString = getVarSingleValue("qty");

		//add device to cart
		ensureCart();
		addProductFromTable(code,name,price,qtyString);
		recalculateCart();

		//save cookie
		saveTableProductCookie();
		return getResult("ajax");
	}

	private void addProductFromTable(String code, String name, String price, String qtyString) throws Exception {
		Item  bought = getSessionMapper().getSingleItemByParamValue(BOUGHT_ITEM, CODE_PARAM, code);
		double qtyWanted = DoubleDataType.parse(qtyString);
		if(bought == null && qtyWanted > 0){
			bought = getSessionMapper().createSessionItem(BOUGHT_ITEM, cart.getId());
			bought.setExtra(TABLE_PRODUCT_COOKIE, true);
			bought.setValueUI(CODE_PARAM, code);
			bought.setValueUI(NAME_PARAM, name);
			getSessionMapper().saveTemporaryItem(bought);
			Item product = getSessionMapper().createSessionItem(PRODUCT_ITEM, bought.getId());
			product.setValueUI(CODE_PARAM,code);
			product.setValueUI(NAME_PARAM,name);
			product.setValueUI(PRICE_PARAM, price);
			product.setValueUI(QTY_PARAM, "1000000");
			//product.setValueUI("available", "1");
			getSessionMapper().saveTemporaryItem(product);
			setBoughtQtys(product,bought,qtyWanted);
			getSessionMapper().saveTemporaryItem(bought);
		}else if(bought != null && qtyWanted > 0){
			Item product =  getSessionMapper().getSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, code);
			double newQty = bought.getDoubleValue(QTY_PARAM,0) + qtyWanted;
			setBoughtQtys(product,bought,newQty);
			getSessionMapper().saveTemporaryItem(bought);
		}else if(bought != null && qtyWanted <=0){
			getSessionMapper().removeItems(bought.getId());
		}
	}

	private boolean boughtExists(String code) throws Exception {
		return getSessionMapper().getSingleItemByParamValue(BOUGHT_ITEM, CODE_PARAM, code) != null;
	}

	public ResultPE restoreTableProductsFromCookie() throws Exception {
		ArrayList<Object> cookies = getCookieVarValues(TABLE_PRODUCT_COOKIE);
		if(cookies.size() == 0) return null;
		String cookie = cookies.get(0).toString();
		if(StringUtils.isBlank(cookie)) return null;

		ensureCart();

		String[] devices = cookie.split(String.valueOf(COOKIE_DEVICE_SEPARATOR));
		for(String device : devices){
			String[] params = device.split(String.valueOf(COOKIE_PARAMETER_SEPARATOR));
			if(boughtExists(params[0])) continue;
			addProductFromTable(params[0], params[1], params[2], params[3]);
		}
		recalculateCart();
		saveTableProductCookie();
		return null;
	}

	private void saveTableProductCookie() throws Exception {
		ensureCart();
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		ArrayList<String> codeQtys = new ArrayList<>();
		for(Item bought : boughts){
			if (!(Boolean) bought.getExtra(CartManageCommand.TABLE_PRODUCT_COOKIE)) continue;
			StringBuilder s = new StringBuilder();
			s.append(bought.getStringValue(CODE_PARAM))
					.append(COOKIE_PARAMETER_SEPARATOR)
					.append(bought.getStringValue(NAME_PARAM))
					.append(COOKIE_PARAMETER_SEPARATOR)
					.append(bought.outputValue(PRICE_PARAM))
					.append(COOKIE_PARAMETER_SEPARATOR)
					.append(bought.outputValue(QTY_PARAM));
			codeQtys.add(s.toString());
		}
		if (codeQtys.size() > 0) {
			String cookie = StringUtils.join(codeQtys, COOKIE_DEVICE_SEPARATOR);
			setCookieVariable(TABLE_PRODUCT_COOKIE, cookie);
		} else {
			setCookieVariable(TABLE_PRODUCT_COOKIE, null);
		}
	}

	@Override
	public ResultPE delete() throws Exception {
		ResultPE deleteResult = super.delete();
		saveTableProductCookie();
		return deleteResult;
	}
}
