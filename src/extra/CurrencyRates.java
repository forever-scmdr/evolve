package extra;

import ecommander.fwk.XmlDocumentBuilder;
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
	private static final String CURRENCY_ID_PARAM_NAME = "currency_id";

	private HashMap<String, BigDecimal[]> rates;
	private String defaultCurrency = null;

	private static CurrencyRates ratesCache = null;
	private static final String SYNC = "SYNC";

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
				defaultCurrency = options.getStringValue(Display_settings.DEFAULT_CURRENCY, "RUB");
			}
		}
		ratesCache = this;
	}

	/**
	 * Установить значения всех параметров товара с ценой во всех валютах
	 * @param product
	 * @param price
	 * @param inputCode - валюта, в которой изначально задана цена товара
	 * @param calculationCode - валюта, которая должна использоваться для расчета при заказе товара, курсов других валют и т.д.
	 */
	public void setAllPrices(Item product, BigDecimal price, String inputCode, String calculationCode) {
		HashMap<String, BigDecimal> allPrices = getAllPrices(price, inputCode, calculationCode);
		for (String paramName : allPrices.keySet()) {
			product.setValue(paramName, allPrices.get(paramName));
		}
		product.setValue(CURRENCY_ID_PARAM_NAME, inputCode);
	}

	/**
	 * Установить значения всех параметров товара с ценой во всех валютах для товара в виде XML разобранного в JSoup
	 * @param productNode
	 * @param price
	 * @param inputCode - валюта, в которой изначально задана цена товара
	 * @param calculationCode - валюта, которая должна использоваться для расчета при заказе товара, курсов других валют и т.д.
	 */
	public void setAllPricesJsoup(Element productNode, BigDecimal price, String inputCode, String calculationCode) {
		HashMap<String, BigDecimal> allPrices = getAllPrices(price, inputCode, calculationCode);
		for (String paramName : allPrices.keySet()) {
			if (StringUtils.equalsIgnoreCase(paramName, PRICE_PARAM_NAME)) {
				productNode.getElementsByTag(PRICE_PARAM_NAME).remove();
			}
			productNode.appendElement(paramName).text(allPrices.get(paramName).toString());
		}
	}

	/**
	 * Установить все цены на товар исходя из оригинальной цены
	 * @param xml
	 * @param price
	 * @param inputCode - валюта, в которой изначально задана цена товара
	 * @param calculationCode - валюта, которая должна использоваться для расчета при заказе товара, курсов других валют и т.д.
	 * @return пары Название параметра цены (price_CUR) => Цена (BigDecimal)
	 */
	public HashMap<String, BigDecimal> setAllPricesXML(XmlDocumentBuilder xml, BigDecimal price, String inputCode, String calculationCode) {
		HashMap<String, BigDecimal> allPrices = getAllPrices(price, inputCode, calculationCode);
		for (String paramName : allPrices.keySet()) {
			xml.addElement(paramName, allPrices.get(paramName).toString());
		}
		return allPrices;
	}

	/**
	 * Получить значения цены товара во всех валютах
	 * Возвращается map название параметра => цена
	 * Название параметра содержит код валюты
	 * @param price
	 * @param inputCode - валюта, в которой изначально задана цена товара
	 * @param calculationCode - валюта, которая должна использоваться для расчета при заказе товара, курсов других валют и т.д.
	 * @return
	 */
	private HashMap<String, BigDecimal> getAllPrices(BigDecimal price, String inputCode, String calculationCode) {
		HashMap<String, BigDecimal> allPrices = new HashMap<>();
		if (price == null || rates == null || rates.size() == 0)
			return allPrices;
		inputCode = StringUtils.trim(StringUtils.upperCase(inputCode));
		BigDecimal defaultCurrencyPrice = price; // если валюта BYN или не найдена (тоже считать что BYN)
		// расчет цены в промежуточной валюте (валюте сайта по умолчанию)
		// нужно чтобы потом можно было пересчитать в валюте для расчетов (в случае если валюта ввода не совпадает с валютой расчетов)
		if (rates.containsKey(inputCode)) {
			BigDecimal[] rate = rates.get(inputCode);
			BigDecimal extraQuotient = (new BigDecimal(1)).add(rate[2].divide(new BigDecimal(100), 6, RoundingMode.HALF_EVEN));
			defaultCurrencyPrice = price.multiply(rate[0]).divide(rate[1], 6, RoundingMode.HALF_EVEN).multiply(extraQuotient).setScale(4, RoundingMode.UP);
			allPrices.put(PRICE_PREFIX + inputCode, price);
		}
		if (StringUtils.equalsIgnoreCase(inputCode, calculationCode)) {
			allPrices.put(PRICE_PARAM_NAME, price);
		} else {
			BigDecimal[] rate = rates.get(calculationCode);
			BigDecimal calculationPrice = defaultCurrencyPrice.divide(rate[0], 6, RoundingMode.HALF_EVEN).multiply(rate[1]).setScale(4, RoundingMode.UP);
			allPrices.put(PRICE_PARAM_NAME, calculationPrice);
		}
		for (String CODE : rates.keySet()) {
			if (StringUtils.equalsIgnoreCase(CODE, inputCode))
				continue;
			BigDecimal[] rate = rates.get(CODE);
			BigDecimal currencyPrice = defaultCurrencyPrice.divide(rate[0], 6, RoundingMode.HALF_EVEN).multiply(rate[1]).setScale(4, RoundingMode.UP);
			allPrices.put(PRICE_PREFIX + CODE, currencyPrice);
		}
		return allPrices;
	}

	/**
	 * Пересчитать сумму из одной валюты в другую
	 * @param sum
	 * @param inputCode
	 * @param outputCode
	 * @return
	 */
	public BigDecimal recalculate(BigDecimal sum, String inputCode, String outputCode) {
		BigDecimal out = sum;
		if (rates.containsKey(inputCode)) {
			BigDecimal[] rate = rates.get(inputCode);
			out = sum.multiply(rate[0]).divide(rate[1], 6, RoundingMode.HALF_EVEN).setScale(4, RoundingMode.UP);
		}
		if (rates.containsKey(outputCode)) {
			BigDecimal[] rate = rates.get(outputCode);
			out = out.divide(rate[0], 6, RoundingMode.HALF_EVEN).multiply(rate[1]).setScale(4, RoundingMode.UP);
		}
		return out;
	}

	public BigDecimal getPrice(Item product, String currencyCode) {
		return product.getDecimalValue(PRICE_PREFIX + currencyCode);
	}

	/**
	 * Установить значения всех параметров товара с ценой во всех валютах
	 * @param product
	 * @param priceStr
	 * @param inputCode
	 * @param mainCode
	 */
	public void setAllPrices(Item product, String priceStr, String inputCode, String mainCode) {
		BigDecimal price = DecimalDataType.parse(priceStr, 2);
		setAllPrices(product, price, inputCode, mainCode);
	}

	/**
	 * Установить значения всех параметров товара с ценой во всех валютах
	 * Базовая цена в бел рублях
	 * @param product
	 * @param priceStr
	 */
	public void setAllPrices(Item product, String priceStr) {
		setAllPrices(product, priceStr, "BYN", "BYN");
	}

	/**
	 * Установить значения всех параметров товара с ценой во всех валютах
	 * Базовая цена в бел рублях
	 * @param product
	 * @param price
	 */
	public void setAllPrices(Item product, BigDecimal price) {
		setAllPrices(product, price, "BYN", "BYN");
	}

	/**
	 * Получить кеш курсов валют (загрузить, если кеша еще нет)
	 * @return
	 * @throws Exception
	 */
	public static CurrencyRates getCache() throws Exception {
		if (ratesCache == null) {
			synchronized (SYNC) {
				if (ratesCache == null)
					new CurrencyRates();
			}
		}
		return ratesCache;
	}

	/**
	 * Валюта по умолчанию в которой должны происходить итоговые расчеты цен на сайте
	 * @return
	 */
	public String getDefaultCurrency() {
		return defaultCurrency;
	}

}
