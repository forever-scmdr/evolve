package ecommander.special.portal.outer.currency;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.special.portal.outer.providers.ItemCache;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;

public class CurrencyRates implements ItemNames {

    private static HashMap<String, CurrencyRatesGetter> RATE_BANKS = new HashMap<>();
    private static HashSet<String> ITEM_SUPPORTED_CURRENCIES = new HashSet<>(); // валюты, которые поддерживаются айтемами товаров (EUR, USD, BYN, RUB)
    static {
        RATE_BANKS.put(RubCurrencyGetter.RUB, new RubCurrencyGetter());
        ITEM_SUPPORTED_CURRENCIES.add("BYN");
        ITEM_SUPPORTED_CURRENCIES.add("RUB");
        ITEM_SUPPORTED_CURRENCIES.add("USD");
        ITEM_SUPPORTED_CURRENCIES.add("EUR");
    }

    public static final String DEFAULT_CURRENCY = "RUB";

    public static final String CURRENCIES_KEY = "currencies_key";
    public static final String RATES_EXTRA = "rates_extra";

    private static final String RATE_SUFFIX = "_rate";
    private static final String EXTRA_SUFFIX = "_extra";
    private static final String PRICE_PREFIX = "price_";
    private static final String PRICE_PARAM_NAME = "price";

    private Item curs;
    private HashMap<String, BigDecimal[]> rates;
    private String defaultCurrency = "BYN";

    public CurrencyRates() throws Exception {
        load();
    }

    private void load() throws Exception {
        curs = ItemCache.get(CURRENCIES_KEY, () -> new ItemQuery(CURRENCIES).loadItems(), 10 * 60);
        if (curs == null)
            return;
        defaultCurrency = DEFAULT_CURRENCY;
        Item settings = ItemCache.get(CURRENCIES_KEY, () -> new ItemQuery(DISPLAY_SETTINGS).loadItems(), 10 * 60);
        if (settings != null) {
            defaultCurrency = settings.getStringValue(display_settings_.DEFAULT_CURRENCY, DEFAULT_CURRENCY);
        }
        rates = (HashMap<String, BigDecimal[]>) curs.getExtra(RATES_EXTRA);
        if (rates == null) {
            rates = new HashMap<>();
            CurrencyRatesGetter getter = getCurrencyGetter(defaultCurrency);
            HashMap<String, BigDecimal> rawRates = getter.getRates(curs.getStringValue(currencies_.XML));
            for (String curCode : rawRates.keySet()) {
                BigDecimal[] quots = new BigDecimal[2];
                quots[0] = rawRates.get(curCode);
                quots[1] = BigDecimal.ZERO;
                rates.put(curCode, quots);
            }
            for (String paramName : curs.getItemType().getParameterNames()) {
                if (StringUtils.endsWithIgnoreCase(paramName, RATE_SUFFIX)) {
                    final String CODE = StringUtils.upperCase(StringUtils.substringBefore(paramName, RATE_SUFFIX));
                    BigDecimal[] rateData = rates.get(CODE);
                    if (!StringUtils.equals(defaultCurrency, CODE)) {
                        if (rateData == null) {
                            rateData = new BigDecimal[2];
                            rateData[0] = curs.getDecimalValue(CODE + RATE_SUFFIX, new BigDecimal(1));
                            rateData[1] = curs.getDecimalValue(CODE + EXTRA_SUFFIX, new BigDecimal(0))/*.add(new BigDecimal(1))*/;
                            rates.put(CODE, rateData);
                        } else {
                            BigDecimal extra = curs.getDecimalValue(CODE + EXTRA_SUFFIX, new BigDecimal(0));
                            if (extra.compareTo(BigDecimal.ZERO) != 0) {
                                rateData[1] = extra;
                            }
                        }
                    }
                }
            }
            curs.setExtra(RATES_EXTRA, rates);
        }
    }


    public static CurrencyRatesGetter getCurrencyGetter(String curCode) {
        if (StringUtils.isBlank(curCode))
            return RATE_BANKS.get(DEFAULT_CURRENCY);
        return RATE_BANKS.get(curCode);
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
     * Установить все цены на товар исходя из оригинальной цены
     * @param xml
     * @param price
     * @param currencyCode
     * @return пары Название параметра цены (price_CUR) => Цена (BigDecimal)
     */
    public HashMap<String, BigDecimal> setAllPricesXML(XmlDocumentBuilder xml, BigDecimal price, String currencyCode) {
        HashMap<String, BigDecimal> allPrices = getAllPrices(price, currencyCode);
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
     * @param currencyCode
     * @return
     */
    private HashMap<String, BigDecimal> getAllPrices(BigDecimal price, String currencyCode) {
        HashMap<String, BigDecimal> allPrices = new HashMap<>();
        if (price == null || rates == null || rates.size() == 0)
            return allPrices;
        currencyCode = StringUtils.trim(StringUtils.upperCase(currencyCode));
        BigDecimal defaultCurrencyPrice = price; // если валюта BYN или не найдена (тоже считать что BYN)
        // Если цена в одной из валют сайта кроме дефолтной
        if (rates.containsKey(currencyCode)) {
            BigDecimal[] quotients = rates.get(currencyCode);
            BigDecimal rate = quotients[0];
            BigDecimal extra = quotients[1];
            BigDecimal extraQuotient = BigDecimal.ONE.add(extra.divide(new BigDecimal(100), 6, RoundingMode.HALF_EVEN));
            defaultCurrencyPrice = price.multiply(rate).multiply(extraQuotient).setScale(4, RoundingMode.UP);
            allPrices.put(PRICE_PREFIX + currencyCode, price);
        }
        allPrices.put(PRICE_PARAM_NAME, defaultCurrencyPrice);
        for (String CODE : ITEM_SUPPORTED_CURRENCIES) {
            if (StringUtils.equalsIgnoreCase(CODE, currencyCode))
                continue;
            BigDecimal[] quotients = rates.get(CODE);
            if (quotients != null) {
                BigDecimal rate = quotients[0];
                BigDecimal extra = quotients[1]; // не используется, т.к. это просто перевод цены в другую валюту, этот коэффициент уже учтен в начальной цене
                BigDecimal currencyPrice = defaultCurrencyPrice.divide(rate, 6, RoundingMode.HALF_EVEN).setScale(4, RoundingMode.UP);
                allPrices.put(PRICE_PREFIX + CODE, currencyPrice);
            }
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

    /**
     * Получить код валюты по умолчанию
     * @return
     */
    public String getDefaultCurrency() {
        return defaultCurrency;
    }

}
