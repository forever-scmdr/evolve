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

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.TreeMap;

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

	private double ratioUsd = -1;
	private double q1Usd = -1;
	private double q2Usd = -1;
	private double ratioEur = -1;
	private double q1Eur = -1;
	private double q2Eur = -1;
	private double q2Arrow = -1;
	private double ratioRur = -1;
	private double q1Rur = -1;
	private double qPromelec = -1;

	/**
	 * Добавить товар Verical (arrow.com) в корзину
	 *
	 * @return updated "cart_ajax" page
	 * @throws Exception
	 */
	public ResultPE addArrowToCart() throws Exception {
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
		if (boughtProduct == null) {
			String name = getVarSingleValue(NAME_PARAM);
			bought.setValue(NAME_PARAM, name);
			bought.setValue(CODE_PARAM, code);
			String available = getVarSingleValue("available");
			available = (StringUtils.isBlank(available)) ? "0" : String.valueOf((Integer.parseInt(available) + 1) % 2);
			bought.setValueUI(NOT_AVAILABLE, available);
			String days = getVarSingleValue("delivery_time");
			if (StringUtils.isNotBlank(days))
				bought.setValueUI("delivery_time", days.trim());
			bought.setValue("aux", getVarSingleValue("aux"));
			bought.setExtra("img", getVarSingleValue("img"));
			getSessionMapper().saveTemporaryItem(bought);
			Item product = getSessionMapper().createSessionItem("product", bought.getId());
			product.setValueUI(NAME_PARAM, name);
			product.setValueUI(CODE_PARAM, getVarSingleValue("vendor_code"));
			product.setValueUI(ItemNames.product_.VENDOR, getVarSingleValue("vendor"));
			product.setValueUI(ItemNames.product_.VENDOR_CODE, getVarSingleValue("vendor_code"));
			product.setValueUI("unit", getVarSingleValue("unit"));
			double qty = StringUtils.isBlank(getVarSingleValue("max")) ? 0d : Double.parseDouble(getVarSingleValue("max"));
			product.setValue(QTY_PARAM, qty);
			product.setValueUI("currency_id", "USD");
			getSessionMapper().saveTemporaryItem(product);
			setBoughtQtys(product, bought, quantity);
		}

		bought.setValueUI("price_map", getVarSingleValue("price_map"));
		getSessionMapper().saveTemporaryItem(bought);
		recalculateCart();
		return getResult("ajax");
	}

	/**
	 * Добавить товар Farnell в корзину
	 *
	 * @return updated "cart_ajax" page
	 * @throws Exception
	 */
	public ResultPE addFarnellToCart() throws Exception {
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
		if (boughtProduct == null) {
			String name = getVarSingleValue(NAME_PARAM).trim().replaceAll("\\s+", " ");
			bought.setValue(NAME_PARAM, name);
			bought.setValue(CODE_PARAM, code);
			bought.setValueUI(NOT_AVAILABLE, getVarSingleValue(NOT_AVAILABLE).trim());

			String days = getVarSingleValue("delivery_time");
			if (StringUtils.isNotBlank(days))
				bought.setValueUI("delivery_time", days.trim());
			bought.setValue("aux", "farnell");
			bought.setExtra("img", getVarSingleValue("img").trim());
			//build price map
			try {
				StringBuilder sb = new StringBuilder();
				int i = 0;
				for (Object v : getVarValues("price")) {
					if (i > 0) sb.append(';');
					sb.append(v);
					i++;
				}
				bought.setValueUI("price_map", sb.toString());
			} catch (Exception e) {
			}
			getSessionMapper().saveTemporaryItem(bought);
			Item product = getSessionMapper().createSessionItem("product", bought.getId());
			product.setValueUI(NAME_PARAM, name);
			product.setValueUI(CODE_PARAM, code);
			product.setValueUI(ItemNames.product_.VENDOR_CODE, getVarSingleValue("vendor_code"));
			product.setValueUI("unit", getVarSingleValue("unit"));
			double qty = StringUtils.isBlank(getVarSingleValue("max")) ? 0d : Double.parseDouble(getVarSingleValue("max"));
			product.setValue(QTY_PARAM, qty);
			product.setValue(QTY_PARAM, qty);
			product.setValueUI("currency_id", "EUR");
			product.setValueUI(ItemNames.product_.VENDOR, getVarSingleValue("vendor"));
			getSessionMapper().saveTemporaryItem(product);
			setBoughtQtys(product, bought, quantity);
		}
		recalculateCart();
		return getResult("ajax");
	}

	/**
	 * Добавить товар c DigiKey в корзину
	 *
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
		if (boughtProduct == null) {
			String name = getVarSingleValue(NAME_PARAM);
			bought.setValue(NAME_PARAM, name);
			bought.setValue(CODE_PARAM, code);
			bought.setValueUI(NOT_AVAILABLE, getVarSingleValue(NOT_AVAILABLE));
			String days = getVarSingleValue("delivery_time");
			if (StringUtils.isNotBlank(days))
				bought.setValueUI("delivery_time", days.trim());
			bought.setValue("aux", getVarSingleValue("aux"));
			bought.setExtra("img", getVarSingleValue("img"));
			getSessionMapper().saveTemporaryItem(bought);
			Item product = getSessionMapper().createSessionItem("product", bought.getId());
			product.setValueUI(NAME_PARAM, name);
			product.setValueUI(CODE_PARAM, code);
			product.setValueUI(ItemNames.product_.DESCRIPTION, getVarSingleValue("description"));
			product.setValueUI(ItemNames.product_.VENDOR_CODE, getVarSingleValue("vendor_code"));
			product.setValueUI("unit", getVarSingleValue("unit"));
			double qty = StringUtils.isBlank(getVarSingleValue("max")) ? 0d : Double.parseDouble(getVarSingleValue("max"));
			product.setValue(QTY_PARAM, qty);
			product.setValueUI("currency_id", "USD");
			product.setValueUI(ItemNames.product_.VENDOR, getVarSingleValue("vendor"));
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
	 *
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
		if (boughtProduct == null) {
			String name = getVarSingleValue(NAME_PARAM);
			Item bought = getSessionMapper().createSessionItem(BOUGHT_ITEM, cart.getId());
			bought.setValue(NAME_PARAM, name);
			bought.setValue(CODE_PARAM, code);
			bought.setValueUI(NOT_AVAILABLE, getVarSingleValue(NOT_AVAILABLE));
			String days = getVarSingleValue("delivery_time");
			if (StringUtils.isNotBlank(days))
				bought.setValueUI("delivery_time", days.trim());
			bought.setValue("aux", getVarSingleValue("aux"));
			getSessionMapper().saveTemporaryItem(bought);
			Item product = getSessionMapper().createSessionItem("product", bought.getId());
			product.setValueUI(NAME_PARAM, name);
			product.setValueUI("vendor", getVarSingleValue("vendor"));
			product.setValueUI(CODE_PARAM, code);
			product.setValueUI("unit", getVarSingleValue("unit"));
			double qty = StringUtils.isBlank(getVarSingleValue("max")) ? 0d : Double.parseDouble(getVarSingleValue("max"));
			product.setValue(QTY_PARAM, qty);
			double specQ = StringUtils.isBlank(getVarSingleValue("upack")) ? Double.MAX_VALUE : Double.parseDouble(getVarSingleValue("upack"));
			product.setValue("spec_qty", specQ);

			String price = getVarSingleValue("price");
			String priceSpec = getVarSingleValue("price_spec");

			String priceStr = quantity >= specQ ? priceSpec : price;

			priceStr = StringUtils.isBlank(priceStr) ? getVarSingleValue("price") : priceStr;
			product.setValueUI(PRICE_PARAM, priceStr);
			product.setValueUI(PRICE_OPT_PARAM, priceSpec);
			product.setValueUI("price_old", price);
			product.setValueUI("currency_id", "RUR");

			product.setExtra("orig", getVarSingleValue("price_original"));
			String spec = StringUtils.isNotBlank("price_original_spec") ? getVarSingleValue("price_original_spec") : getVarSingleValue("price_original");
			product.setExtra("orig_spec", spec);

			getSessionMapper().saveTemporaryItem(product);
			setBoughtQtys(product, bought, quantity);
		} else {
			Item bought = getSessionMapper().getItem(boughtProduct.getContextParentId(), BOUGHT_ITEM);
			setBoughtQtys(boughtProduct, bought, quantity);
		}
		recalculateCart();
		return getResult("ajax");
	}

	public ResultPE addPromelecToCart() throws Exception {
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
		if (boughtProduct == null) {
			String name = getVarSingleValue(NAME_PARAM);
			bought.setValue(NAME_PARAM, name);
			bought.setValue(CODE_PARAM, code);
			bought.setValueUI(NOT_AVAILABLE, getVarSingleValue(NOT_AVAILABLE));
			String days = getVarSingleValue("delivery_time");
			if (StringUtils.isNotBlank(days))
				bought.setValueUI("delivery_time", days.trim());
			bought.setValue("aux", getVarSingleValue("aux"));
			bought.setExtra("img", getVarSingleValue("img"));
			getSessionMapper().saveTemporaryItem(bought);
			Item product = getSessionMapper().createSessionItem("product", bought.getId());
			product.setValueUI(NAME_PARAM, name);
			product.setValueUI(CODE_PARAM, code);
			product.setValueUI(ItemNames.product_.VENDOR_CODE, getVarSingleValue("vendor_code"));
			product.setValueUI("unit", getVarSingleValue("unit"));
			double qty = StringUtils.isBlank(getVarSingleValue("max")) ? 0d : Double.parseDouble(getVarSingleValue("max"));
			product.setValue(QTY_PARAM, qty);
			product.setValueUI("currency_id", "RUR");
			getSessionMapper().saveTemporaryItem(product);
			setBoughtQtys(product, bought, quantity);
		}

		bought.setValueUI("price_map", getVarSingleValue("price_map"));
		getSessionMapper().saveTemporaryItem(bought);
		recalculateCart();
		return getResult("ajax");
	}

	@Override
	protected void extraActionWithBought(Item bought) throws Exception {
		Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
		String aux = bought.getStringValue("aux");
		if (StringUtils.isNotBlank(aux)) {
			loadCurrency();
			switch (aux) {
				case "platan":
					platanPriceAction(bought, product);
					break;
				case "digikey":
					digikeyPriceAction(bought, product);
					break;
				case "promelec":
					promelecPriceAction(bought, product);
					break;
				case "farnell":
					farnellPriceAction(bought, product);
					break;
				case "arrow":
					arrowPriceAction(bought, product);
					break;
			}
		}
	}

	private void loadCurrency() throws Exception {
		if (ratioEur == -1 && ratioUsd == -1 && q1Eur == -1 && q2Eur == -1 && q1Usd == -1 && q2Usd == -1) {
			Item catalog = ItemQuery.loadSingleItemByName("catalog");
			ratioUsd = catalog.getDoubleValue("currency_ratio_usd");
			q1Usd = 1 + catalog.getDoubleValue("q1_usd", 0d);
			q2Usd = 1 + catalog.getDoubleValue("q2_usd", 0d);

			ratioEur = catalog.getDoubleValue("currency_ratio_eur");
			q1Eur = 1 + catalog.getDoubleValue("q1_eur", 0d);
			q2Eur = 1 + catalog.getDoubleValue("q2_eur", 0d);
			q2Arrow = 1 + catalog.getDoubleValue("q2_arrow", 0d);

			ratioRur = catalog.getDoubleValue("currency_ratio");
			q1Rur = 1 + catalog.getDoubleValue("q1", 0d);
			qPromelec = 1 + catalog.getDoubleValue("q2_prom", 0d);
		}
	}

	private void farnellPriceAction(Item bought, Item product) throws Exception {
		double totalQty = bought.getDoubleValue(QTY_TOTAL_PARAM);
		String specPrice = bought.getStringValue("price_map");
		TreeMap<Double, String> priceMap = parsePriceMap(specPrice, ratioEur, q1Eur, q2Eur);
		TreeMap<Double, String> priceOrigMap = parsePriceMap(specPrice, 1, 1, 1);
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

	private void arrowPriceAction(Item bought, Item product) throws Exception {
		double totalQty = bought.getDoubleValue(QTY_TOTAL_PARAM);
		String specPrice = bought.getStringValue("price_map");
		TreeMap<Double, String> priceMap = parsePriceMap(specPrice, ratioUsd, q1Usd, q2Arrow);
		TreeMap<Double, String> priceOrigMap = parsePriceMap(specPrice, 1, 1, 1);
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

	private void promelecPriceAction(Item bought, Item product) throws Exception {
		double totalQty = bought.getDoubleValue(QTY_TOTAL_PARAM);
		String specPrice = bought.getStringValue("price_map");

		TreeMap<Double, String> priceMap = parsePriceMap(specPrice, ratioRur, q1Rur, qPromelec, 100);
		TreeMap<Double, String> priceOrigMap = parsePriceMap(specPrice, 1, 1, 1);
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

	private void digikeyPriceAction(Item bought, Item product) throws Exception {
		double totalQty = bought.getDoubleValue(QTY_TOTAL_PARAM);
		String specPrice = bought.getStringValue("price_map");
		TreeMap<Double, String> priceOrigMap = parsePriceMap(specPrice, 1, 1, 1);
		TreeMap<Double, String> priceMap = parsePriceMap(specPrice, ratioUsd, q1Usd, q2Usd);
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

	private void platanPriceAction(Item bought, Item product) throws Exception {
		double specQ = product.getDoubleValue("spec_qty", Double.MAX_VALUE);
		if (bought.getDoubleValue(QTY_AVAIL_PARAM, 0d) >= specQ) {
			product.setValue(PRICE_PARAM, product.getValue(PRICE_OPT_PARAM));
			if (product.getExtra("orig_spec") != null) {
				product.setValueUI("price_original", product.getExtra("orig_spec").toString());
			}
		} else {
			product.setValue(PRICE_PARAM, product.getValue("price_old"));
			if (product.getExtra("orig") != null) {
				product.setValueUI("price_original", product.getExtra("orig").toString());
			}
		}
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
		String additionalInfo = "";
		filePart.setFileName(cart.getValue("order_num") + ".xls");
		mp.addBodyPart(filePart);
		bos.close();
		return true;
	}

}
