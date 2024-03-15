package ecommander.special.portal.outer.providers;

import ecommander.fwk.Strings;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.special.portal.outer.ProxyRequestDispatcher;
import ecommander.special.portal.outer.Request;
import extra.CurrencyRates;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Получение данных и обработка ответа с oemsecrets.com
 * Объект класса хранится в единственном экземпляре
 */
public class OemsecretsGetter extends ProviderGetter {
    @Override
    public String getProviderName() {
        return Providers.OEMSECRETS;
    }

    @Override
    public Result getData(UserInput userInput, CurrencyRates rates) {
        // Выполняются все запросы на сервер (в частности все подзапросы BOM)
        Request request = ProxyRequestDispatcher.submitRequest(getProviderName(), userInput.getQueries().keySet());
        Result result;
        try {
            request.awaitExecution();
            result = new Result(request, SUCCESS, null);
        } catch (Exception e) {
            return new Result(request, CONNECTION_ERROR, ExceptionUtils.getStackTrace(e));
        }

        HashMap<String, BigDecimal> distributorQuotients = new HashMap<>();

        // Результирующий документ
        for (Request.Query query : request.getAllQueries()) {
            XmlDocumentBuilder queryXml = XmlDocumentBuilder.newDocPart();

            int qty = userInput.getQueries().get(query.query);
            String q = query.query;
            // <query> - открывающий
            queryXml.startElement("query", "q", q, "qty", qty, "millis", query.getProcessMillis(), "tries", query.getNumTries());
            if (query.getStatus() != Request.Status.SUCCESS) {
                String errorType = query.getStatus() == Request.Status.PROXY_FAILURE ? "proxy_failure" : "provider_failure";
                queryXml.addElement("error", query.getResult(), "type", errorType);
                queryXml.endElement(); // </query> - закрывающий (т.к. далее continue)
                query.setProcessedResult(queryXml);
                continue;
            }

            String jsonString = query.getResult();

            // Кеши и нужные значения
            LinkedHashMap<String, XmlDocumentBuilder> distributorXmls = new LinkedHashMap<>();

            // Парсинг и создание XML
            JSONObject json = new JSONObject(jsonString);

            JSONArray products;
            try {
                products = json.getJSONArray("stock");
            } catch (JSONException je) {
                query.setStatus(Request.Status.HOST_FAILURE);
                queryXml.addElement("error", "Не найдены товары", "type", "wrong_format");
                queryXml.endElement(); // </query> - закрывающий (т.к. далее continue)
                query.setProcessedResult(queryXml);
                continue;
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
                XmlDocumentBuilder distrXml = distributorXmls.computeIfAbsent(distributor, s -> XmlDocumentBuilder.newDocPart());
                BigDecimal distributorQuotient = getDistributorQuotient(distributor, distributorQuotients);
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
                distrXml.startElement("product", "id", code, "key", key);
                distrXml.addElement("code", code);
                distrXml.addElement("name", name);
                distrXml.addElement("vendor", vendor);
                distrXml.addElement("qty", product.get("quantity_in_stock"));
                distrXml.addElement("step", "1");
                distrXml.addElement("description", product.getString("description"));
                distrXml.addElement("min_qty", "1");
                distrXml.addElement("next_delivery", leadTime);
                distrXml.addElement("container", product.getString("packaging"));
                distrXml.addElement("category_id", distributor);
                JSONArray currencyBreakArray;
                BigDecimal maxPrice = BigDecimal.ONE.negate();
                BigDecimal minPrice = BigDecimal.valueOf(Double.MAX_VALUE);
                try {
                    JSONObject pricesObject = product.getJSONObject("prices");
                    currencyBreakArray = pricesObject.getJSONArray(StringUtils.upperCase(userInput.getCurCode()));
                } catch (JSONException je) {
                    currencyBreakArray = null;
                }
                if (currencyBreakArray == null) {
                    distrXml.addElement("pricebreak", "0");
                } else {
                    distrXml.addElement("pricebreak", currencyBreakArray.length());
                    boolean noCurrency = true;
                    boolean noBreaks = true;
                    for (int j = 0; j < currencyBreakArray.length(); j++) {
                        JSONObject priceBreak = currencyBreakArray.getJSONObject(j);
                        if (noCurrency) {
                            distrXml.addElement("currency_id", userInput.getCurCode());
                            noCurrency = false;
                        }
                        if (noBreaks) {
                            distrXml.startElement("prices");
                            noBreaks = false;
                        }
                        BigDecimal breakQty = priceBreak.getBigDecimal("unit_break");
                        BigDecimal priceOriginal = DecimalDataType.parse(priceBreak.getString("unit_price"), 4);
                        distrXml.startElement("break", "qty", breakQty);
                        if (priceOriginal != null) {
                            distrXml.addElement("price_original", priceOriginal);
                            // Применить коэффициент
                            priceOriginal = priceOriginal.multiply(distributorQuotient).setScale(4, RoundingMode.UP);
                            HashMap<String, BigDecimal> allPricesDecimal = rates.setAllPricesXML(distrXml, priceOriginal, userInput.getCurCode());
                            BigDecimal currentPrice = allPricesDecimal.get(userInput.getPriceParamName());
                            if (currentPrice != null) {
                                maxPrice = maxPrice.max(currentPrice);
                                minPrice = minPrice.min(currentPrice);
                                userInput.setGlobalMaxPrice(userInput.getGlobalMaxPrice().max(currentPrice));
                                userInput.setGlobalMinPrice(userInput.getGlobalMinPrice().min(currentPrice));
                            }
                        }
                        distrXml.endElement(); // break
                    }
                    if (!noBreaks) {
                        distrXml.endElement(); // prices
                    }
                    // Добавление элементов с максимальной и минимальной ценой (чтобы сохранилась в кеше)
                    distrXml.addElement("max_price", maxPrice);
                    distrXml.addElement("min_price", minPrice);
                }
                // Если девайс не подходит по фильтрам - добавить тэг <invalid>invalid</invalid>
                boolean isInvalid
                        = !userInput.shipDateFilterMatches(leadTime)
                        || !userInput.vendorFilterMatches(vendor)
                        || !userInput.distributorFilterMatches(distributor)
                        || !userInput.fromPriceFilterMatches(maxPrice)
                        || !userInput.toPriceFilterMatches(minPrice);

                if (isInvalid) {
                    distrXml.addElement("invalid", "invalid");
                }
                distrXml.endElement(); // product
            }

            // Теперь взять XML всех производителей и объединить по порядку появления
            for (String distributor : distributorXmls.keySet()) {
                XmlDocumentBuilder distributorXml = distributorXmls.get(distributor);
                queryXml.startElement("distributor", "name", distributor);
                queryXml.addElements(distributorXml.getXmlStringSB());
                queryXml.endElement(); // distributor
            }
            queryXml.endElement(); // </query> - закрывающий
            query.setProcessedResult(queryXml);
        }

        return result;
    }
}
