package extra;

import ecommander.controllers.PageController;
import ecommander.fwk.BasicCartManageCommand;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.LinkPE;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

/**
 * Корзина
 * Created by E on 6/3/2018.
 */
public class CartManageCommand extends BasicCartManageCommand {

	public static final HashSet<String> MANDATORY_PHYS = new HashSet<>();
	public static final HashSet<String> MANDATORY_JUR = new HashSet<>();

	private static final String MIN_QTY_PARAM = "min_qty";

	static {
		MANDATORY_PHYS.add(ItemNames.user_phys_.NAME);
		MANDATORY_PHYS.add(ItemNames.user_phys_.PHONE);

		MANDATORY_JUR.add(ItemNames.user_jur_.CONTACT_NAME);
		MANDATORY_JUR.add(ItemNames.user_jur_.PHONE);
		MANDATORY_JUR.add(ItemNames.user_jur_.ORGANIZATION);
	}

	public ResultPE addExternalToCart() throws Exception {
		checkStrategy();
		String code = getVarSingleValue(CODE_PARAM);
		double quantity = 0;
		try {
			quantity = DoubleDataType.parse(getVarSingleValue(QTY_PARAM));
		}catch (Exception e){
			return getResult("ajax");
		}
		ensureCart();
		Item boughtProduct = getSessionMapper().getSingleItemByParamValue("product", CODE_PARAM, code);

		if(boughtProduct == null){
			if(quantity <= 0) return getResult("ajax");

			String name = getVarSingleValue(NAME_PARAM);

			//bought
			Item bought = getSessionMapper().createSessionItem(BOUGHT_ITEM, cart.getId());
			bought.setValue(NAME_PARAM, name);
			bought.setValue(CODE_PARAM, code);
			bought.setValueUI(NOT_AVAILABLE, getVarSingleValue("not_available"));
			bought.setValueUI("delivery_time", getVarSingleValue("delivery_time"));
			bought.setValue("aux", getVarSingleValue("aux"));
			bought.setExtra("img", StringEscapeUtils.escapeXml10(getVarSingleValue("img")));
			bought.setExtra("id", getVarSingleValue("id"));
			bought.setValueUI("price_map", getVarSingleValue("price_map"));
			getSessionMapper().saveTemporaryItem(bought);

			//product
			Item product = getSessionMapper().createSessionItem("product", bought.getId());
			product.setValueUI(NAME_PARAM, name);
			product.setValueUI(MIN_QTY_PARAM, getVarSingleValueDefault("min_qty", "1"));
			product.setValueUI(CODE_PARAM, getVarSingleValue("code"));
			product.setValueUI(ItemNames.product_.VENDOR, getVarSingleValueDefault("vendor",""));
			product.setValueUI(ItemNames.product_.VENDOR_CODE, getVarSingleValueDefault("vendor_code",""));
			product.setValueUI("unit", getVarSingleValue("unit"));
			double qty = StringUtils.isBlank(getVarSingleValue("max")) ? 0d : Double.parseDouble(getVarSingleValue("max"));
			product.setValue(QTY_PARAM, qty);

			Item shop = ItemQuery.loadSingleItemByParamValue("shop", NAME_PARAM, getVarSingleValue("aux"), Item.STATUS_NORMAL);
			//Item currency = loadCurrency(shop);

			//product.setValueUI("currency_id", currency.getStringValue(NAME_PARAM));
			getSessionMapper().saveTemporaryItem(product);
			setBoughtQtys(product, bought, quantity);
		}
		else{
			Item bought = getSessionMapper().getItem(boughtProduct.getContextParentId(), BOUGHT_ITEM);
			if (quantity <= 0) {
				getSessionMapper().removeItems(bought.getId());
				return getResult("ajax");
			}
			setBoughtQtys(boughtProduct, bought, quantity);
			getSessionMapper().saveTemporaryItem(bought);
		}
		recalculateCart();
		return getResult("ajax");
	}


	@Override
	protected void extraActionWithBought(Item bought) throws Exception {
		Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
		String aux = bought.getStringValue("aux");
		if(StringUtils.isNotBlank(aux)){
//			Item shop = ItemQuery.loadSingleItemByParamValue("shop", NAME_PARAM, aux, Item.STATUS_NORMAL);
//			Item currency = loadCurrency(shop);
//			double q1 = 1 + currency.getDoubleValue("q",0d);
//			double q2 = 1 + shop.getDoubleValue("q", 0d);
//			double ratio = currency.getDoubleValue("ratio");
//			int scale = currency.getIntValue("scale");
			String priceMapString = bought.getStringValue("price_map");
			TreeMap<Double, String> priceMap = new TreeMap<>();//parsePriceMap(priceMapString, ratio, q1, q2, scale);
			TreeMap<Double, String> priceOrigMap = parsePriceMap(priceMapString, 1, 1, 1, 1);
			double totalQty = bought.getDoubleValue(QTY_TOTAL_PARAM);
			if (priceMap.size() > 0) {
				for (Double breakpoint : priceMap.keySet()) {
					if (breakpoint <= totalQty) {
						product.setValueUI(PRICE_PARAM, priceMap.get(breakpoint));
						product.setValueUI("price_original", priceOrigMap.get(breakpoint));
					} else {
						break;
					}
				}
			}
		}
	}

	private Item loadCurrency(Item shop) throws Exception {
		ArrayList<Item> currencies = ItemQuery.loadByParentId(shop.getId(), new Byte[]{ItemTypeRegistry.getAssocId("general")});
		return (currencies.size() > 0)? currencies.get(0) : null;
	}


	private TreeMap<Double, String> parsePriceMap(String specPrice, double ratio, double q1, double q2, int... scale) throws Exception {
		int currencyScale = (scale != null && scale.length > 0)? scale[0] : 1;
		if (StringUtils.isBlank(specPrice) || specPrice.indexOf(':') == -1) return new TreeMap<>();
		TreeMap<Double, String> result = new TreeMap<>();
		String[] z = specPrice.split(";");
		for (String pair : z) {
			String[] p = pair.split(":");
			Double q = DoubleDataType.parse(p[0]);
			Double pr = (DoubleDataType.parse(p[1]) * q1 * q2 * ratio)/currencyScale;
			result.put(q, String.valueOf(pr));
		}
		return result;
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

	@Override
	protected boolean addExtraEmailBodyParts(boolean isCustomerEmail, Multipart mp) throws Exception {
		if (isCustomerEmail) return true;

		LinkPE customerEmailLink = LinkPE.newDirectLink("link", "order_excel", false);
		ExecutablePagePE excelTemplate = getExecutablePage(customerEmailLink.serialize());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PageController.newSimple().executePage(excelTemplate, bos);
		DataSource dataSource = new ByteArrayDataSource(bos.toByteArray(), "application/vnd.ms-excel");
		MimeBodyPart filePart = new MimeBodyPart();
		filePart.setDataHandler(new DataHandler(dataSource));
		filePart.setFileName(cart.getValue("order_num") + ".xls");
		mp.addBodyPart(filePart);
		bos.close();
		return true;
	}

}
