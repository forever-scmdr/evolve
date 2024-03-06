package extra;

import ecommander.fwk.Strings;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.special.portal.outer.ProxyRequestDispatcher;
import ecommander.special.portal.outer.Request;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SearchOemsecretsCommand extends SearchFindchipsCommand {

    private static final String USD = "USD";
    private static final String URL_WITH_KEY
            = "https://oemsecretsapi.com/partsearch?apiKey=5yddaj3l7y9m6bvolfwu2bycbqxylktaqj3gugtqx4kmsat2hprit7cubn3ge7m1&searchTerm={Q}&currency={CUR}";

    public SearchOemsecretsCommand(Command outer) {
        super(outer);
    }

    /**
     * Загрузить данные с сервера и оформить в виде XML
     * Возвращает резльутат ResultPE в случае ошибки
     * @param rootXml
     * @return
     */
    protected ResultPE getFromServer(XmlDocumentBuilder rootXml, CurrencyRates rates) throws Exception {
        String jsonString;
        String proxy = getVarSingleValue("proxy");
        inp.curCode = USD; // временно, возможно надо на постоянно оставить
        try {
            String query = StringUtils.normalizeSpace(inp.query);
            if (StringUtils.isNotBlank(query)) {
                query = URLEncoder.encode(query, Strings.SYSTEM_ENCODING);
                Request request = ProxyRequestDispatcher.submitRequest("oemsecrets", query);
                request.awaitExecution();
                Request.Query response = request.getAllQueries().iterator().next();
                jsonString = response.getResult();
                /*
                String requestUrl = StringUtils.replace(URL_WITH_KEY, "{Q}", query);
                requestUrl = StringUtils.replace(requestUrl, "{CUR}", inp.curCode);
                if (StringUtils.isNotBlank(proxy) && StringUtils.startsWith(proxy, "http")) {
                    String proxyUrl = proxy + "?url=" + URLEncoder.encode(requestUrl, Strings.SYSTEM_ENCODING);
                    jsonString = OkWebClient.getInstance().getString(proxyUrl);
                } else {
                    jsonString = OkWebClient.getInstance().getString(requestUrl);
                }
                 */
            } else {
                return getResult("illegal_argument").setValue("Неверный формат запроса");
            }
        } catch (Exception e) {
            return getResult("error").setValue(ExceptionUtils.getStackTrace(e));
        }

        // Кеши и нужные значения
        String priceParamName = PRICE_PREFIX + (StringUtils.isBlank(inp.curCode) ? rates.getDefaultCurrency() : inp.curCode);
        LinkedHashMap<String, XmlDocumentBuilder> distributorXmls = new LinkedHashMap<>();
        HashMap<String, BigDecimal> distributorQuotients = new HashMap<>();

        // Парсинг и создание XML
        JSONObject json = new JSONObject(jsonString);

        JSONArray products;
        try {
            products = json.getJSONArray("stock");
        } catch (JSONException je) {
            return getResult("error").setValue("Не найдены товары");
        }
        for (int i = 0; i < products.length(); i++) {
            JSONObject product = products.getJSONObject(i);
            JSONObject distributorObject;
            try {
                distributorObject = product.getJSONObject("distributor");
            } catch (JSONException je) {
                continue;
            }
            String distributor = distributorObject.getString("distributor_name");
            XmlDocumentBuilder xml = distributorXmls.computeIfAbsent(distributor, s -> XmlDocumentBuilder.newDocPart());
            BigDecimal distributorQuotient = distributorQuotients.get(distributor); // дополнительный коэффициент для цены для поставщика
            if (distributorQuotient == null) {
                Item catalogSettings = new ItemQuery(PRICE_CATALOG).addParameterEqualsCriteria(price_catalog_.NAME, distributor).loadFirstItem();
                if (catalogSettings != null) {
                    distributorQuotient = catalogSettings.getDecimalValue(price_catalog_.QUOTIENT, BigDecimal.ONE);
                } else {
                    distributorQuotient = BigDecimal.ONE;
                }
                distributorQuotients.put(distributor, distributorQuotient);
            }
            String sku = product.get("sku").toString();
            String partNumber = product.get("source_part_number").toString();
            String leadTime = product.get("lead_time").toString();
            String vendor = product.getString("manufacturer");
            String name = sku;
            String code = partNumber;
            if (StringUtils.isBlank(code)) {
                code = name + "_" + Integer.toHexString(distributor.hashCode());
            }
            String key = Strings.translit(name + " " + code);
            xml.startElement("product", "id", code, "key", key);
            xml.addElement("code", code);
            xml.addElement("name", name);
            xml.addElement("vendor", vendor);
            xml.addElement("qty", product.get("quantity_in_stock"));
            xml.addElement("step", "1");
            xml.addElement("description", product.getString("description"));
            xml.addElement("min_qty", "1");
            xml.addElement("next_delivery", leadTime);
            xml.addElement("container", product.getString("packaging"));
            xml.addElement("category_id", distributor);
            JSONArray currencyBreakArray;
            BigDecimal maxPrice = BigDecimal.ONE.negate();
            BigDecimal minPrice = BigDecimal.valueOf(Double.MAX_VALUE);
            try {
                JSONObject pricesObject = product.getJSONObject("prices");
                currencyBreakArray = pricesObject.getJSONArray(StringUtils.upperCase(inp.curCode));
            } catch (JSONException je) {
                currencyBreakArray = null;
            }
            if (currencyBreakArray == null) {
                xml.addElement("pricebreak", "0");
            } else {
                xml.addElement("pricebreak", currencyBreakArray.length());
                boolean noCurrency = true;
                boolean noBreaks = true;
                for (int j = 0; j < currencyBreakArray.length(); j++) {
                    JSONObject priceBreak = currencyBreakArray.getJSONObject(j);
                    if (noCurrency) {
                        xml.addElement("currency_id", inp.curCode);
                        noCurrency = false;
                    }
                    if (noBreaks) {
                        xml.startElement("prices");
                        noBreaks = false;
                    }
                    BigDecimal breakQty = priceBreak.getBigDecimal("unit_break");
                    BigDecimal priceOriginal = DecimalDataType.parse(priceBreak.getString("unit_price"), 4);
                    xml.startElement("break", "qty", breakQty);
                    if (priceOriginal != null) {
                        xml.addElement("price_original", priceOriginal);
                        // Применить коэффициент
                        priceOriginal = priceOriginal.multiply(distributorQuotient).setScale(4, RoundingMode.UP);
                        HashMap<String, BigDecimal> allPricesDecimal = rates.setAllPricesXML(xml, priceOriginal, inp.curCode);
                        BigDecimal currentPrice = allPricesDecimal.get(priceParamName);
                        if (currentPrice != null) {
                            maxPrice = maxPrice.max(currentPrice);
                            minPrice = minPrice.min(currentPrice);
                            inp.globalMaxPrice = inp.globalMaxPrice.max(currentPrice);
                            inp.globalMinPrice = inp.globalMinPrice.min(currentPrice);
                        }
                    }
                    xml.endElement(); // break
                }
                if (!noBreaks) {
                    xml.endElement(); // prices
                }
                // Добавление элементов с максимальной и минимальной ценой (чтобы сохранилась в кеше)
                xml.addElement("max_price", maxPrice);
                xml.addElement("min_price", minPrice);
            }
            // Если девайс не подходит по фильтрам - добавить тэг <invalid>invalid</invalid>
            boolean isInvalid
                    = (inp.hasShipDateFilter && !inp.shipDateFilter.contains(leadTime))
                    || (inp.hasVendorFilter && !inp.vendorFilter.contains(vendor))
                    || (inp.hasDistributorFilter && !inp.distributorFilter.contains(distributor))
                    || (inp.hasFromFilter && maxPrice.compareTo(inp.fromFilterDecimal) < 0)
                    || (inp.hasToFilter && minPrice.compareTo(inp.toFilterDecimal) > 0);
            if (isInvalid) {
                xml.addElement("invalid", "invalid");
            }
            xml.endElement(); // product
        }

        // Теперь взять XML всех производителей и объединить по порядку появления
        rootXml.addElement("query", inp.query);
        for (String distributor : distributorXmls.keySet()) {
            XmlDocumentBuilder distributorXml = distributorXmls.get(distributor);
            rootXml.startElement("distributor", "name", distributor);
            rootXml.addElements(distributorXml.getXmlStringSB());
            rootXml.endElement(); // distributor
        }

        return null;
    }
}
