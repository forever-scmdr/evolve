package ecommander.special.portal.outer.providers;

import ecommander.fwk.EcommanderException;
import ecommander.model.Item;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import extra.CurrencyRates;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Данные, которые ввел пользователь для своего поиска, в том числе сам запрос, а также фильтр
 * Кроме этого тут хранятся курсы валют, текущая валюта по умолчанию и разные буферы, которые используются
 * при обработке запросов.
 * Экземпляр класса создается про поступлении запроса пользователя
 */
public class UserInput {
    public static final String SERVER_PARAM = "server";
    public static final String QUERY_PARAM = "q";
    public static final String PRICE_PREFIX = "price_";

    private List<Object> servers;
    private String curCode;
    private LinkedHashMap<String, Integer> query;
    private String unparsedQuery;
    private String remote; // Провайдер данных (provider, host)
    private BigDecimal fromFilterDecimal;
    private BigDecimal toFilterDecimal;
    private HashSet<Object> shipDateFilter;
    private HashSet<Object> vendorFilter;
    private HashSet<Object> distributorFilter;
    private HashSet<Object> dstrSet;
    private BigDecimal globalMaxPrice; // максимальная цена (для создания итогового документа)
    private BigDecimal globalMinPrice; // минимальная цена (для создания итогового документа)

    private ResultPE errorResult;

    private CurrencyRates rates = null;

    private String priceParamName;

    UserInput(Command command) throws EcommanderException {
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
        this.globalMaxPrice = BigDecimal.ONE.negate();
        this.globalMinPrice = BigDecimal.valueOf(Double.MAX_VALUE);
        parseQuery();
    }

    private UserInput(String bomQuery) {
        unparsedQuery = bomQuery;
        query = new LinkedHashMap<>();
        parseQuery();
    }

    /**
     * Создать экземпляр для разбора запроса
     * Из всех полей заполняется (и доступен) только поле query
     * и геттер getQueries()
     * @param bomQuery
     * @return
     */
    public static UserInput createForBomParsing(String bomQuery) {
        return new UserInput(bomQuery);
    }

    /**
     * Загрузить (если еще не загружено) и вернуть курсы валют
     * @return
     * @throws Exception
     */
    public CurrencyRates getRates() throws Exception {
        if (rates == null) {
            rates = new CurrencyRates();
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
                    if (qty > 9999) {
                        query.put(StringUtils.join(parts, ' '), 1);
                    } else {
                        query.put(StringUtils.join(parts, ' ', 0, parts.length - 1), qty);
                    }
                } catch (NumberFormatException nfe) {
                    query.put(StringUtils.join(parts, ' '), 1);
                }
            } else if (parts.length == 1) {
                query.put(parts[0], 1);
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
        return fromFilterDecimal != null;
    }

    public boolean hasToFilter() {
        return toFilterDecimal != null;
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

    public BigDecimal getGlobalMaxPrice() {
        return globalMaxPrice;
    }

    public void setGlobalMaxPrice(BigDecimal globalMaxPrice) {
        this.globalMaxPrice = globalMaxPrice;
    }

    public BigDecimal getGlobalMinPrice() {
        return globalMinPrice;
    }

    public void setGlobalMinPrice(BigDecimal globalMinPrice) {
        this.globalMinPrice = globalMinPrice;
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
}
