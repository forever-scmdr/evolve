package extra;

import ecommander.model.Item;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Created by E on 7/6/2019.
 */
public class CurrencyRates implements ItemNames{
	private static final String RATE_SUFFIX = "_rate";
	private static final String SCALE_SUFFIX = "_scale";
	private static final String EXTRA_SUFFIX = "_extra_quotient";
	private static final String PRICE_PREFIX = "price_";

	private HashMap<String, BigDecimal[]> rates;

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
						rateData[0] = curs.getDecimalValue(CODE + RATE_SUFFIX, BigDecimal.ONE);
						rateData[1] = curs.getDecimalValue(CODE + SCALE_SUFFIX, BigDecimal.ONE);
						rateData[2] = BigDecimal.valueOf(curs.getDoubleValue(CODE + EXTRA_SUFFIX, 0.0)).add(new BigDecimal(1));
						rates.put(CODE, rateData);
					}
				}
			}
		}
	}

	/**
	 * Установить значения всех параметров товара с ценой во всех валютах
	 * @param product
	 * @param price
	 * @param currencyCode
	 */
	public void setPrice(Item product, BigDecimal price, String currencyCode) {
		if (price == null || rates == null || rates.size() == 0)
			return;
		currencyCode = StringUtils.trim(StringUtils.upperCase(currencyCode));
		BigDecimal bynPrice = price; // если валюта BYN или не найдена (тоже считать что BYN)
		// Если цена в одной из валют сайта кроме BYN
		if (rates.containsKey(currencyCode)) {
			BigDecimal[] rate = rates.get(currencyCode);
			BigDecimal extraQuotient = (new BigDecimal(1)).add(rate[2].divide(new BigDecimal(100), 6, BigDecimal.ROUND_HALF_EVEN));
			bynPrice = price.multiply(rate[0]).divide(rate[1], 6, BigDecimal.ROUND_HALF_EVEN).multiply(extraQuotient).setScale(4, BigDecimal.ROUND_CEILING);
		}
		product.setValue(Product.PRICE, bynPrice);
		/*
		for (String CODE : rates.keySet()) {
			BigDecimal[] rate = rates.get(CODE);
			BigDecimal currencyPrice = bynPrice.divide(rate[0], 6, BigDecimal.ROUND_HALF_EVEN).multiply(rate[1]).setScale(4, BigDecimal.ROUND_CEILING);
			product.setValue(PRICE_PREFIX + CODE, currencyPrice);
		}
		*/
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
	public void setPrice(Item product, String priceStr, String currencyCode) {
		BigDecimal price = DecimalDataType.parse(priceStr, 2);
		setPrice(product, price, currencyCode);
	}

	/**
	 * Установить значения всех параметров товара с ценой во всех валютах
	 * Базовая цена в бел рублях
	 * @param product
	 * @param priceStr
	 */
	public void setPrice(Item product, String priceStr) {
		setPrice(product, priceStr, "BYN");
	}

	/**
	 * Установить значения всех параметров товара с ценой во всех валютах
	 * Базовая цена в бел рублях
	 * @param product
	 * @param price
	 */
	public void setPrice(Item product, BigDecimal price) {
		setPrice(product, price, "BYN");
	}

}
