package extra;

import ecommander.fwk.BasicCartManageCommand;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.pages.ResultPE;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

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

	/**
	 * Добавить товар Farnell в корзину
	 * @return updated "cart_ajax" page
	 * @throws Exception
	 */
	public ResultPE addFarnellToCart() throws Exception{
		checkStrategy();
		String code = getVarSingleValue(CODE_PARAM).trim();
		double quantity = 0;
		try {
			quantity = DoubleDataType.parse(getVarSingleValue(QTY_PARAM).trim());
		} catch (Exception e) {
			return getResult("ajax");
		}
		ensureCart();
		Item boughtProduct = getSessionMapper().getSingleItemByParamValue("product", CODE_PARAM, code);
		Item bought = getSessionMapper().createSessionItem(BOUGHT_ITEM, cart.getId());
		if(boughtProduct == null){
			String name = getVarSingleValue(NAME_PARAM).trim().replaceAll("\\s+", " ");
			bought.setValue(NAME_PARAM, name);
			bought.setValue(CODE_PARAM, code);
			bought.setValueUI(NOT_AVAILABLE, getVarSingleValue(NOT_AVAILABLE).trim());
			bought.setValue("aux", "farnell");
			bought.setExtra("img", getVarSingleValue("img").trim());
			//build price map
			try{
				StringBuilder sb = new StringBuilder();
				int i=0;
				for(Object v : getVarValues("price")){
					if(i > 0) sb.append(';');
					sb.append(v);
					i++;
				}
				bought.setValueUI("price_map",sb.toString());
			}catch (Exception e){}
			getSessionMapper().saveTemporaryItem(bought);
			Item product = getSessionMapper().createSessionItem("product", bought.getId());
			product.setValueUI(NAME_PARAM, name);
			product.setValueUI(CODE_PARAM, code);
			product.setValueUI(ItemNames.product_.VENDOR_CODE, getVarSingleValue("vendor_code"));
			product.setValueUI("unit", getVarSingleValue("unit"));
			double qty = StringUtils.isBlank(getVarSingleValue("max"))? 0d : Double.parseDouble(getVarSingleValue("max"));
			product.setValue(QTY_PARAM, qty);
			getSessionMapper().saveTemporaryItem(product);
			setBoughtQtys(product, bought, quantity);
		}
		recalculateCart();
		return getResult("ajax");
	}

	/**
	 * Добавить товар c DigiKey в корзину
	 * @return updated "cart_ajax" page
	 * @throws Exception
	 */
	public ResultPE addDgkToCart() throws Exception {
		checkStrategy();
		String code = getVarSingleValue(CODE_PARAM);
		double quantity = 0;
		try {
			quantity = DoubleDataType.parse(getVarSingleValue(QTY_PARAM));
		} catch (Exception e) {
			return getResult("ajax");
		}

		ensureCart();

		Item boughtProduct = getSessionMapper().getSingleItemByParamValue("product", CODE_PARAM, code);
		Item bought = getSessionMapper().createSessionItem(BOUGHT_ITEM, cart.getId());
		if(boughtProduct == null){
			String name = getVarSingleValue(NAME_PARAM);
			bought.setValue(NAME_PARAM, name);
			bought.setValue(CODE_PARAM, code);
			bought.setValueUI(NOT_AVAILABLE, getVarSingleValue(NOT_AVAILABLE));
			bought.setValue("aux", getVarSingleValue("aux"));
			bought.setExtra("img", getVarSingleValue("img"));
			getSessionMapper().saveTemporaryItem(bought);
			Item product = getSessionMapper().createSessionItem("product", bought.getId());
			product.setValueUI(NAME_PARAM, name);
			product.setValueUI(CODE_PARAM, code);
			product.setValueUI(ItemNames.product_.VENDOR_CODE, getVarSingleValue("vendor_code"));
			product.setValueUI("unit", getVarSingleValue("unit"));
			double qty = StringUtils.isBlank(getVarSingleValue("max"))? 0d : Double.parseDouble(getVarSingleValue("max"));
			product.setValue(QTY_PARAM, qty);
			getSessionMapper().saveTemporaryItem(product);
			setBoughtQtys(product, bought, quantity);
		}

		bought.setValueUI("price_map", getVarSingleValue("dgk_spec"));
		getSessionMapper().saveTemporaryItem(bought);
		recalculateCart();
		return getResult("ajax");
	}

	/**
	 * Добавить товар c Платана в корзину
	 * @return
	 * @throws Exception
	 */
	public ResultPE addPltToCart() throws Exception {
		checkStrategy();
		String code = getVarSingleValue(CODE_PARAM);
		double quantity = 0;
		try {
			quantity = DoubleDataType.parse(getVarSingleValue(QTY_PARAM));
		} catch (Exception e) {
			return getResult("ajax");
		}

		ensureCart();
		// Проверка, есть ли уже такой девайс в корзине (если есть, изменить количество)
		Item boughtProduct = getSessionMapper().getSingleItemByParamValue("product", CODE_PARAM, code);
		if(boughtProduct == null){
			String name = getVarSingleValue(NAME_PARAM);
			Item bought = getSessionMapper().createSessionItem(BOUGHT_ITEM, cart.getId());
			bought.setValue(NAME_PARAM, name);
			bought.setValue(CODE_PARAM, code);
			bought.setValueUI(NOT_AVAILABLE, getVarSingleValue(NOT_AVAILABLE));
			bought.setValue("aux", getVarSingleValue("aux"));
			getSessionMapper().saveTemporaryItem(bought);
			Item product = getSessionMapper().createSessionItem("product", bought.getId());
			product.setValueUI(NAME_PARAM, name);
			product.setValueUI(CODE_PARAM, code);
			product.setValueUI("unit", getVarSingleValue("unit"));
			double qty = StringUtils.isBlank(getVarSingleValue("max"))? 0d : Double.parseDouble(getVarSingleValue("max"));
			product.setValue(QTY_PARAM, qty);
			double specQ = StringUtils.isBlank(getVarSingleValue("upack"))? Double.MAX_VALUE : Double.parseDouble(getVarSingleValue("upack"));
			product.setValue("spec_qty", specQ);

			String price = getVarSingleValue("price");
			String priceSpec =  getVarSingleValue("price_spec");

			String priceStr = quantity >= specQ? priceSpec : price;
			priceStr = StringUtils.isBlank(priceStr)? getVarSingleValue("price") : priceStr;
			product.setValueUI(PRICE_PARAM, priceStr);
			product.setValueUI(PRICE_OPT_PARAM, priceSpec);
			product.setValueUI("price_old", price);
			getSessionMapper().saveTemporaryItem(product);
			setBoughtQtys(product, bought, quantity);
		}else{
			Item bought = getSessionMapper().getItem(boughtProduct.getContextParentId(), BOUGHT_ITEM);
			setBoughtQtys(boughtProduct, bought, quantity);
		}
		recalculateCart();
		return getResult("ajax");
	}

	@Override
	protected void extraActionWithBought(Item bought, Item product){
		String aux = bought.getStringValue("aux");
		if(StringUtils.isBlank(aux))
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
