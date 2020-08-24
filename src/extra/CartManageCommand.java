package extra;

import ecommander.fwk.BasicCartManageCommand;
import ecommander.fwk.Pair;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Plain_section;
import extra._generated.Price_catalog;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * Корзина
 * Created by E on 6/3/2018.
 */
public class CartManageCommand extends BasicCartManageCommand implements ItemNames {

	public static final HashSet<String> MANDATORY_PHYS = new HashSet<>();
	public static final HashSet<String> MANDATORY_JUR = new HashSet<>();
	static {
		MANDATORY_PHYS.add(user_phys_.NAME);
		//MANDATORY_PHYS.add(ItemNames.user_phys_.ADDRESS);
		//MANDATORY_PHYS.add(ItemNames.user_phys_.EMAIL);
		MANDATORY_PHYS.add(user_phys_.PHONE);
		//MANDATORY_PHYS.add(ItemNames.user_phys_.SHIP_TYPE);

		//MANDATORY_JUR.add(ItemNames.user_jur_.ACCOUNT);
		//MANDATORY_JUR.add(ItemNames.user_jur_.ADDRESS);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK_ADDRESS);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK_CODE);
		MANDATORY_JUR.add(user_jur_.CONTACT_NAME);
		MANDATORY_JUR.add(ItemNames.user_jur_.CONTACT_PHONE);
		//MANDATORY_JUR.add(user_jur_.PHONE);
		//MANDATORY_JUR.add(ItemNames.user_jur_.DIRECTOR);
		MANDATORY_JUR.add(user_jur_.EMAIL);
		//MANDATORY_JUR.add(user_jur_.PASSWORD);
		MANDATORY_JUR.add(user_jur_.ORGANIZATION);
		//MANDATORY_JUR.add(ItemNames.user_jur_.SHIP_TYPE);
		//MANDATORY_JUR.add(ItemNames.user_jur_.UNP);
	}

	private static final String MIN_PARAM = "min";
	private static final String MAX_PARAM = "max";
	private static final String QUOTIENT_PARAM = "quotient";
	private static final String PRICE_INTERVAL_ITEM = "price_interval";
	private static final String PRICE_CATALOG_ITEM = "price_catalog";
	private static final String PRICE_PREFIX = "price_";
	private static final String SUM_PREFIX = "sum_";
	private static final String RATE_POSTFIX = "_rate";
	private static final String DEFAULT = "default";

	// Раздел => Базовый коэффициент, Коэффициенты от суммы (Значение, Коэффифиент)
	private HashMap<String, Pair<BigDecimal, ArrayList<Pair<BigDecimal, BigDecimal>>>> priceIntervals = null;
	//private BigDecimal defaultQuotient = new BigDecimal(1.4d).setScale(4, BigDecimal.ROUND_CEILING);
	private HashSet<String> currencyCodes = new HashSet<>();
	private CurrencyRates currencyRates;
	Item digiKeyQuotients;

	public CartManageCommand() {
		super();
		try {
			currencyRates = new CurrencyRates();
			digiKeyQuotients = ItemQuery.loadSingleItemByParamValue(PRICE_CATALOG_ITEM, NAME_PARAM, "digikey.com");
		}catch (Exception e){}
		for (String paramName : ItemTypeRegistry.getItemType(CURRENCIES).getParameterNames()) {
			if (StringUtils.endsWithIgnoreCase(paramName, RATE_POSTFIX)) {
				currencyCodes.add(StringUtils.substringBefore(paramName, RATE_POSTFIX));
			}
		}
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
	protected boolean recalculateCart() throws Exception {
		loadCart();
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		BigDecimal sum = new BigDecimal(0); // полная сумма
		HashMap<String, BigDecimal> currencySums = new HashMap<>(); // полная сумма
		for (String currencyCode : currencyCodes) {
			currencySums.put(currencyCode, new BigDecimal(0));
		}
		double regularQuantity = 0;
		boolean result = true;

		// Обычные заказы и заказы с нулевым количеством на складе
		for (Item bought : boughts) {
			Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
			Item section = getSessionMapper().getSingleItemByName(PLAIN_SECTION, product.getId());

			BigDecimal maxQuantity = product.getDecimalValue(QTY_PARAM, new BigDecimal(MAX_QTY));
			double quantity = bought.getDoubleValue(QTY_PARAM);
			if (quantity <= 0) {
				getSessionMapper().removeItems(bought.getId(), BOUGHT_ITEM);
				result = false;
			} else {
				// Первоначальная сумма
				BigDecimal price = product.getDecimalValue(PRICE_PARAM, new BigDecimal(0d));
				BigDecimal productSum = price.multiply(new BigDecimal(quantity));
				HashMap<String, BigDecimal> currencyPrices = new HashMap<>();
				HashMap<String, BigDecimal> currencyProductSums = new HashMap<>();
				for (String currencyCode : currencyCodes) {
					BigDecimal currencyPrice = product.getDecimalValue(PRICE_PREFIX + currencyCode, new BigDecimal(0));
					currencyPrices.put(currencyCode, currencyPrice);
					currencyProductSums.put(currencyCode, currencyPrice.multiply(new BigDecimal(quantity)));
				}
				if (maxQuantity.compareTo(new BigDecimal(0)) <= 0) {
					productSum = new BigDecimal(0);
					for (String currencyCode : currencyProductSums.keySet()) {
						currencyProductSums.put(currencyCode, new BigDecimal(0));
					}
				} else {
					regularQuantity += quantity;
				}
				// Умножить сумму на коэффициент, зависящий от суммы (количества заказанных товаров)
				BigDecimal quotient = new BigDecimal(1);
				if (section != null)
					quotient = getTotalQuotient(productSum, section.getStringValue(Plain_section.NAME));
				else if(bought.getExtra("map") != null){
					quotient  = digiKeyQuotients.getDecimalValue(QUOTIENT_PARAM, BigDecimal.ONE);
				}
				productSum = productSum.multiply(quotient).setScale(2, BigDecimal.ROUND_CEILING);
				for (String currencyCode : currencyProductSums.keySet()) {
					currencyProductSums.put(currencyCode, currencyProductSums.get(currencyCode).multiply(quotient).setScale(2, BigDecimal.ROUND_CEILING));
				}
				bought.setValue(PRICE_PARAM, price);
				for (String currencyCode : currencyPrices.keySet()) {
					bought.setValue(PRICE_PREFIX + currencyCode, currencyPrices.get(currencyCode));
				}
				bought.setValue(SUM_PARAM, productSum);
				for (String currencyCode : currencySums.keySet()) {
					bought.setValue(SUM_PREFIX + currencyCode, currencyProductSums.get(currencyCode));
				}
				sum = sum.add(productSum);
				for (String currencyCode : currencyProductSums.keySet()) {
					currencySums.put(currencyCode, currencySums.get(currencyCode).add(currencyProductSums.get(currencyCode)));
				}
				// Сохранить bought
				getSessionMapper().saveTemporaryItem(bought);
			}
		}
		cart.setValue(SUM_PARAM, sum);
		for (String currencyCode : currencyCodes) {
			cart.setValue(SUM_PREFIX + currencyCode, currencySums.get(currencyCode));
		}
		cart.setValue(QTY_PARAM, regularQuantity);
		// Сохранить корзину
		getSessionMapper().saveTemporaryItem(cart);
		saveCookie();
		return result && regularQuantity > 0;
	}

	/**
	 * Загрузить интервалы цен и коэфиициенты для этих интервалов
	 */
	private void loadPriceIntervals() {
		if (priceIntervals == null) {
			priceIntervals = new HashMap<>();
			try {
				List<Item> priceCatalogs = new ItemQuery(PRICE_CATALOG_ITEM).loadItems();
				for (Item priceCatalog : priceCatalogs) {
					ArrayList<Pair<BigDecimal, BigDecimal>> intervals = new ArrayList<>();
					priceIntervals.put(priceCatalog.getStringValue(Price_catalog.NAME, DEFAULT),
							new Pair<>(priceCatalog.getDecimalValue(Price_catalog.QUOTIENT), intervals));
					List<Item> quotients = new ItemQuery(PRICE_INTERVAL_ITEM)
							.setParentId(priceCatalog.getId(), false)
							.addSorting(MAX_PARAM, "ASC").loadItems();
					for (Item quotient : quotients) {
						intervals.add(new Pair<>(quotient.getDecimalValue(MAX_PARAM), quotient.getDecimalValue(QUOTIENT_PARAM)));
					}
				}
			} catch (Exception e) {
				ServerLogger.error("Unable to load price intervals", e);
			}
		}
	}

	/**
	 * Вычислить коэффициент для товара учитвая количество заказанных единиц
	 * @param productSum
	 * @return
	 */
	private BigDecimal getTotalQuotient(BigDecimal productSum, String sectionName) {
		loadPriceIntervals();
		BigDecimal currentQuotient = new BigDecimal(1d);
		ArrayList<Pair<BigDecimal, BigDecimal>> intervals = null;
		BigDecimal basicQuotient = null;
		if (StringUtils.isNotBlank(sectionName) && priceIntervals.containsKey(sectionName)) {
			intervals = priceIntervals.get(sectionName).getRight();
			basicQuotient = priceIntervals.get(sectionName).getLeft();
		}
		if (intervals == null || intervals.size() == 0) {
			intervals = priceIntervals.get(DEFAULT).getRight();
			basicQuotient = priceIntervals.get(DEFAULT).getLeft();
		}
		if (intervals != null) {
			for (Pair<BigDecimal, BigDecimal> priceInterval : intervals) {
				currentQuotient = priceInterval.getRight();
				if (productSum.compareTo(priceInterval.getLeft()) <= 0) {
					break;
				}
			}
		}
		return currentQuotient.multiply(basicQuotient).setScale(6, BigDecimal.ROUND_CEILING);
	}

	@Override
	protected void extraLoading(Item product) throws Exception {
		Item section = new ItemQuery(PLAIN_SECTION).setChildId(product.getId(), false).loadFirstItem();
		if (section != null) {
			section.setContextParentId(ItemTypeRegistry.getPrimaryAssoc(), product.getId());
			getSessionMapper().saveTemporaryItem(section);
		}
	}

	/**
	 * Добавить продукт с DigiKey
	 */
	public ResultPE addDigiKeyToCart() throws Exception {
		ensureCart();
		String code = getVarSingleValue(CODE_PARAM);

		Item bought = getSessionMapper().getSingleItemByParamValue(BOUGHT_ITEM, CODE_PARAM, code);
		if(bought == null) {

			String name = getVarSingleValue(NAME_PARAM);
			String vendor_code = getVarSingleValue("vendor_code");
			String vendor = getVarSingleValue("vendor");
			String qty = getVarSingleValue("qty").replaceAll("[^0-9.,]", "").replace(',', '.');
			String max = getVarSingleValue("max").replaceAll("[^0-9.,]", "").replace(',', '.');
			String map = getVarSingleValue("map");

			BigDecimal decimalQty = new BigDecimal(qty).setScale(BIG_DECIMAL_SCALE_6, BigDecimal.ROUND_HALF_EVEN);
			if (decimalQty.compareTo(BigDecimal.ZERO) < 1) return getResult("ajax");
			BigDecimal maxQuantity = new BigDecimal(max).setScale(BIG_DECIMAL_SCALE_6, BigDecimal.ROUND_HALF_EVEN);
			TreeMap<BigDecimal, BigDecimal> priceMap = parsePriceMap(map);
			BigDecimal minQuantity = priceMap.firstKey();

			BigDecimal qtyMinQtyFraction = decimalQty.divide(minQuantity, BigDecimal.ROUND_HALF_EVEN);
			if (!isIntegerValue(qtyMinQtyFraction))
				decimalQty = minQuantity.multiply(qtyMinQtyFraction.setScale(0, BigDecimal.ROUND_CEILING));
			if (maxQuantity.compareTo(new BigDecimal(0)) > 0)
				decimalQty = maxQuantity.compareTo(decimalQty) > 0 ? decimalQty : maxQuantity;

			bought = getSessionMapper().createSessionItem(BOUGHT_ITEM, cart.getId());
			{
				bought.setValue(CODE_PARAM, code);
				bought.setValue(NAME_PARAM, name);
				bought.setValue(QTY_PARAM, decimalQty.doubleValue());
				bought.setValue("img", getVarSingleValue("img"));
				bought.setExtra("map", map);
				getSessionMapper().saveTemporaryItem(bought);

				Item product = getSessionMapper().createSessionItem("product", bought.getId());
				product.setValueUI(NAME_PARAM, name);
				product.setValueUI(CODE_PARAM, code);
				BigDecimal priceUSD = getPriceFromMap(priceMap, decimalQty);
				currencyRates.setAllPrices(product, priceUSD, "USD");
				product.setValue(QTY_PARAM, maxQuantity);
				product.setValue("vendor", vendor);
				product.setValue("vendor_code", vendor_code);
				product.setValue("url", getVarSingleValue("url"));
				getSessionMapper().saveTemporaryItem(product);
			}
		}else {
			Item boughtProduct = getSessionMapper().getSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, code);
			String qty = getVarSingleValue("qty").replaceAll("[^0-9.,]", "").replace(',', '.');
			BigDecimal decimalQty = new BigDecimal(qty).setScale(BIG_DECIMAL_SCALE_6, BigDecimal.ROUND_HALF_EVEN).add(new BigDecimal(bought.getDoubleValue(QTY_PARAM)));
			BigDecimal maxQuantity = boughtProduct.getDecimalValue(QTY_PARAM);
			TreeMap<BigDecimal, BigDecimal> priceMap = parsePriceMap(bought.getExtra("map").toString());
			BigDecimal minQuantity = priceMap.firstKey();
			BigDecimal qtyMinQtyFraction = decimalQty.divide(minQuantity, BigDecimal.ROUND_HALF_EVEN);
			if (!isIntegerValue(qtyMinQtyFraction))
				decimalQty = minQuantity.multiply(qtyMinQtyFraction.setScale(0, BigDecimal.ROUND_CEILING));
			if (maxQuantity.compareTo(new BigDecimal(0)) > 0)
				decimalQty = maxQuantity.compareTo(decimalQty) > 0 ? decimalQty : maxQuantity;
			BigDecimal priceUSD = getPriceFromMap(priceMap, decimalQty);
			currencyRates.setAllPrices(boughtProduct, priceUSD, "USD");
			bought.setValue(QTY_PARAM, decimalQty.doubleValue());
			getSessionMapper().saveTemporaryItem(bought);
			getSessionMapper().saveTemporaryItem(boughtProduct);
		}
		recalculateCart();
		return getResult("ajax");
	}



	private BigDecimal getPriceFromMap(TreeMap<BigDecimal, BigDecimal> priceMap, BigDecimal qty){
		if(priceMap.containsKey(qty)) return priceMap.get(qty);
		if(priceMap.size() > 0){
			BigDecimal price = priceMap.get(priceMap.firstKey());
			for(BigDecimal breakpoint : priceMap.keySet()){
				if(breakpoint.compareTo(qty) < 0){
					price = priceMap.get(breakpoint);
				}else{
					return price;
				}
			}
		}
		return BigDecimal.ZERO;
	}

	private TreeMap<BigDecimal, BigDecimal> parsePriceMap(String map){
		TreeMap prices = new TreeMap();
		for(String pair : StringUtils.split(map, ';')){
			String[] p = StringUtils.split(pair, ':');
			BigDecimal k = new BigDecimal(p[0]).setScale(BIG_DECIMAL_SCALE_6, BigDecimal.ROUND_HALF_EVEN);
			BigDecimal v = new BigDecimal(p[1]).setScale(BIG_DECIMAL_SCALE_6, BigDecimal.ROUND_HALF_EVEN);
			prices.put(k,v);
		}
		return prices;
	}
}
