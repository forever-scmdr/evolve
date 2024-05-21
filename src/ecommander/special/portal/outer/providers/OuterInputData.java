package ecommander.special.portal.outer.providers;

import ecommander.fwk.EcommanderException;
import ecommander.model.Item;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.special.portal.outer.currency.CurrencyRates;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Данные, которые ввел пользователь для своего поиска, в том числе сам запрос, а также фильтр
 * Кроме этого тут хранятся курсы валют, текущая валюта по умолчанию и разные буферы, которые используются
 * при обработке запросов.
 * Экземпляр класса создается про поступлении запроса пользователя
 */
public class OuterInputData {

    public enum Sort {
        date, price;
        static Sort create(String str) {
            return (StringUtils.equalsIgnoreCase(str, "date")) ? date : price;
        }
    }
    public static final String SERVER_PARAM = "server";
    public static final String QUERY_PARAM = "q";
    public static final String PRICE = "price";
    public static final String PRICE_PREFIX = "price_";

    private List<Object> servers;
    private String curCode;
    private final LinkedHashMap<String, Integer> query;
    private final String unparsedQuery;
    private String remote; // Провайдер данных (provider, host)
    private BigDecimal fromFilterDecimal;
    private BigDecimal toFilterDecimal;
    private HashSet<Object> shipDateFilter;
    private HashSet<Object> vendorFilter;
    private HashSet<Object> distributorFilter;
    private HashSet<Object> dstrSet;
    // базовые коэффициенты для каждого поставщика. Загружаются по мере надобности. Это просто кеш для хранения
    private HashMap<String, BigDecimal> distributorQuotients;
    private ResultPE errorResult;

    private CurrencyRates rates = null;

    private String priceParamName;

    private Sort sort;

    OuterInputData(Command command) throws EcommanderException {
        this.servers = command.getVarValues(SERVER_PARAM);
        String query = command.getVarSingleValue(QUERY_PARAM);
        // Запрос может поступать напрямую через переменную q, а может через страничный айтем товара "prod" (его название)
        if (StringUtils.isBlank(query)) {
            Item prod = command.getSingleLoadedItem("prod");
            if (prod != null)
                query = prod.getStringValue("name");
        }
        if (StringUtils.isBlank(query)) {
            errorResult = command.getResult("illegal_argument").setValue("Неверный формат запроса");
        }
        this.unparsedQuery = query;
        this.query = new LinkedHashMap<>();
        this.remote = command.getVarSingleValueDefault("remote", Providers.FINDCHIPS);

        // Фильтр
        this.curCode = command.getVarSingleValueDefault("cur", "RUB");
        String fromFilter = command.getVarSingleValue("from");
        String toFilter = command.getVarSingleValue("to");
        this.shipDateFilter = new HashSet<>(command.getVarValues("ship_date"));
        this.vendorFilter = new HashSet<>(command.getVarValues("vendor"));
        this.distributorFilter = new HashSet<>(command.getVarValues("distributor"));
        String dstr = command.getVarSingleValue("dstr");
        String[] dstrArray = StringUtils.split(dstr, ",");
        this.dstrSet = new HashSet<>();
        if (dstrArray != null) {
            for (String s : dstrArray) {
                if (StringUtils.isNotBlank(s))
                    this.dstrSet.add(StringUtils.trim(s));
            }
        }
        BigDecimal from = DecimalDataType.parse(fromFilter, 4);
        fromFilterDecimal = from == null ? BigDecimal.ONE.negate() : from;
        BigDecimal to = DecimalDataType.parse(toFilter, 4);
        toFilterDecimal = to == null ? BigDecimal.valueOf(Double.MAX_VALUE) : to;
        sort = Sort.create(command.getVarSingleValueDefault("sort", "price"));
        parseQuery();
    }

    private OuterInputData(String bomQuery) {
        this.unparsedQuery = bomQuery;
        this.query = new LinkedHashMap<>();
        parseQuery();
    }

    /**
     * Создать экземпляр для разбора запроса
     * Из всех полей заполняется (и доступен) только поле query
     * и геттер getQueries()
     * @param bomQuery
     * @return
     */
    public static OuterInputData createForBomParsing(String bomQuery) {
        return new OuterInputData(bomQuery);
    }

    /**
     * Загрузить (если еще не загружено) и вернуть курсы валют
     * @return
     * @throws Exception
     */
    public CurrencyRates getRates() throws Exception {
        if (rates == null) {
            rates = new CurrencyRates();
            priceParamName = PRICE;
            if (!StringUtils.equals(curCode, rates.getDefaultCurrency()))
                priceParamName = PRICE_PREFIX + (StringUtils.isBlank(curCode) ? rates.getDefaultCurrency() : curCode);
        }
        return rates;
    }

    /**
     * Название параметра для цены с учетом курсов валют и валюты по умолчанию
     * @return
     */
    public String getPriceParamName() {
        return priceParamName;
    }

    /**
     * Выполнить разбор запроса.
     * Запрос может содержать несколько строк, каждая строка - отдельный подзапрос (девайс)
     * Также после названия девайса может идти его количество через разделитель табуляции
     */
    private void parseQuery() {
        String[] lines = StringUtils.split(unparsedQuery, "\r\n");
        for (String line : lines) {
            String[] parts = StringUtils.split(line, "\t ");
            if (parts.length > 1) {
                String possibleQty = parts[parts.length - 1];
                try {
                    int qty = Integer.parseInt(possibleQty);
                    if (qty > 49999) {
                        query.put(StringUtils.join(parts, ' '), 0);
                    } else {
                        query.put(StringUtils.join(parts, ' ', 0, parts.length - 1), qty);
                    }
                } catch (NumberFormatException nfe) {
                    query.put(StringUtils.join(parts, ' '), 0);
                }
            } else if (parts.length == 1) {
                query.put(parts[0], 0);
            }
        }
    }

    public boolean hasShipDateFilter() {
        return shipDateFilter.size() > 0;
    }

    public boolean hasVendorFilter() {
        return vendorFilter.size() > 0;
    }

    public boolean hasDistributorFilter() {
        return distributorFilter.size() > 0;
    }

    public boolean hasFromFilter() {
        return fromFilterDecimal != null && fromFilterDecimal.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasToFilter() {
        return toFilterDecimal != null && toFilterDecimal.compareTo(BigDecimal.valueOf(100000000)) < 0;
    }

    public boolean vendorFilterMatches(String vendor) {
        return vendorFilter.size() == 0 || vendorFilter.contains(vendor);
    }

    public boolean shipDateFilterMatches(String shipDate) {
        return shipDateFilter.size() == 0 || shipDateFilter.contains(shipDate);
    }

    public boolean distributorFilterMatches(String distributor) {
        return distributorFilter.size() == 0 || distributorFilter.contains(distributor);
    }

    public boolean fromPriceFilterMatches(BigDecimal maxPrice) {
        return fromFilterDecimal == null || maxPrice == null || maxPrice.compareTo(fromFilterDecimal) >= 0;
    }

    public boolean toPriceFilterMatches(BigDecimal minPrice) {
        return fromFilterDecimal == null || minPrice == null || minPrice.compareTo(toFilterDecimal) < 0;
    }

    public String getCurCode() {
        return curCode;
    }

    public ResultPE getErrorResult() {
        return errorResult;
    }

    public String getRemote() {
        return remote;
    }

    public LinkedHashMap<String, Integer> getQueries() {
        return query;
    }

    public BigDecimal getDistributorQuotient(String distributor) {
        BigDecimal quot = BigDecimal.ONE;
        if (distributorQuotients != null)
            quot = distributorQuotients.get(distributor);
        return quot == null ? BigDecimal.ONE : quot;
    }

    public void setDistributorQuotients(HashMap<String, BigDecimal> quotients) {
        if (quotients != null) {
            distributorQuotients = quotients;
        }
    }

    public boolean hasDistributorQuotients() {
        return distributorQuotients != null;
    }
    public Sort getSort() {
        return sort;
    }
}
