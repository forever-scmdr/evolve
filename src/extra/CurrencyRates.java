package extra;

import ecommander.model.Item;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.Display_settings;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

/**
 * Created by E on 7/6/2019.
 */
public class CurrencyRates implements ItemNames{
	private static final String RATE_SUFFIX = "_rate";
	private static final String SCALE_SUFFIX = "_scale";
	private static final String EXTRA_SUFFIX = "_extra";
	private static final String PRICE_PREFIX = "price_";
	private static final String PRICE_PARAM_NAME = "price";

	private HashMap<String, BigDecimal[]> rates;
	private String defaultCurrency = "BYN";

	public CurrencyRates() throws Exception {
		load();
	}

	private void load() throws Exception {
		if (rates == null) {
			rates = new HashMap<>();
			Item curs = ItemQuery.loadSingleItemByName(CURRENCIES);
			if (curs != null) {
				for (String paramName : curs.getItemType().getParameterNames()) {
					if (StringUtils.endsWithIgnoreCase(paramName, RATE_SUFFIX)) {
						final String CODE = StringUtils.upperCase(StringUtils.substringBefore(paramName, RATE_SUFFIX));
						BigDecimal[] rateData = new BigDecimal[3];
						rateData[0] = curs.getDecimalValue(CODE + RATE_SUFFIX, new BigDecimal(1));
						rateData[1] = curs.getDecimalValue(CODE + SCALE_SUFFIX, new BigDecimal(1));
						rateData[2] = curs.getDecimalValue(CODE + EXTRA_SUFFIX, new BigDecimal(0))/*.add(new BigDecimal(1))*/;
						rates.put(CODE, rateData);
					}
				}
			}
			Item options = ItemQuery.loadSingleItemByName(DISPLAY_SETTINGS);
			if (options != null) {
				defaultCurrency = options.getStringValue(Display_settings.DEFAULT_CURRENCY, "BYN");
			}
		}
	}

	/**
	 * Установить значения всех параметров товара с ценой во всех валютах
	 * @param product
	 * @param price
	 * @param currencyCode
	 */
	public void setAllPrices(Item product, BigDecimal price, String currencyCode) {
		HashMap<String, BigDecimal> allPrices = getAllPrices(price, currencyCode);
		for (String paramName : allPrices.keySet()) {
			product.setValue(paramName, allPrices.get(paramName));
		}
	}

	/**
	 * Установить значения всех параметров товара с ценой во всех валютах для товара в виде XML разобранного в JSoup
	 * @param productNode
	 * @param price
	 * @param currencyCode
	 */
	public void setAllPricesJsoup(Element productNode, BigDecimal price, String currencyCode) {
		HashMap<String, BigDecimal> allPrices = getAllPrices(price, currencyCode);
		for (String paramName : allPrices.keySet()) {
			if (StringUtils.equalsIgnoreCase(paramName, PRICE_PARAM_NAME)) {
				productNode.getElementsByTag(PRICE_PARAM_NAME).remove();
			}
			productNode.appendElement(paramName).text(allPrices.get(paramName).toString());
		}
	}

	/**
	 * Получить значения цены товара во всех валютах
	 * Возвращается map название параметра => цена
	 * Название параметра содержит код валюты
	 * @param price
	 * @param currencyCode
	 * @return
	 */
	private HashMap<String, BigDecimal> getAllPrices(BigDecimal price, String currencyCode) {
		HashMap<String, BigDecimal> allPrices = new HashMap<>();
		if (price == null || rates == null || rates.size() == 0)
			return allPrices;
		currencyCode = StringUtils.trim(StringUtils.upperCase(currencyCode));
		BigDecimal defaultCurrencyPrice = price; // если валюта BYN или не найдена (тоже считать что BYN)
		// Если цена в одной из валют сайта кроме BYN
		if (rates.containsKey(currencyCode)) {
			BigDecimal[] rate = rates.get(currencyCode);
			BigDecimal extraQuotient = (new BigDecimal(1)).add(rate[2].divide(new BigDecimal(100), 6, RoundingMode.HALF_EVEN));
			defaultCurrencyPrice = price.multiply(rate[0]).divide(rate[1], 6, RoundingMode.HALF_EVEN).multiply(extraQuotient).setScale(2, RoundingMode.UP);
			allPrices.put(PRICE_PREFIX + currencyCode, price);
		}
		allPrices.put(PRICE_PARAM_NAME, defaultCurrencyPrice);
		for (String CODE : rates.keySet()) {
			if (StringUtils.equalsIgnoreCase(CODE, currencyCode))
				continue;
			BigDecimal[] rate = rates.get(CODE);
			BigDecimal currencyPrice = defaultCurrencyPrice.divide(rate[0], 6, RoundingMode.HALF_EVEN).multiply(rate[1]).setScale(2, RoundingMode.UP);
			allPrices.put(PRICE_PREFIX + CODE, currencyPrice);
		}
		return allPrices;
	}


	public BigDecimal getPrice(Item product, String currencyCode) {
		return product.getDecimalValue(PRICE_PREFIX + currencyCode);
	}

	/**
	 * Установить значения всех параметров товара с ценой во всех валютах
	 * @param product
	 * @param priceStr
	 * @param currencyCode
	 */
	public void setAllPrices(Item product, String priceStr, String currencyCode) {
		BigDecimal price = DecimalDataType.parse(priceStr, 2);
		setAllPrices(product, price, currencyCode);
	}

	/**
	 * Установить значения всех параметров товара с ценой во всех валютах
	 * Базовая цена в бел рублях
	 * @param product
	 * @param priceStr
	 */
	public void setAllPrices(Item product, String priceStr) {
		setAllPrices(product, priceStr, "BYN");
	}

	/**
	 * Установить значения всех параметров товара с ценой во всех валютах
	 * Базовая цена в бел рублях
	 * @param product
	 * @param price
	 */
	public void setAllPrices(Item product, BigDecimal price) {
		setAllPrices(product, price, "BYN");
	}

}
