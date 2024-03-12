package ecommander.special.portal.outer.providers;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.special.portal.outer.ProxyRequestDispatcher;
import ecommander.special.portal.outer.Request;
import extra.CurrencyRates;
import extra._generated.ItemNames;
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

/**
 * Получение данных и обработка ответа с oemsecrets.com
 */
public class OemsecretsGetter implements ProviderGetter, ItemNames {
    @Override
    public String getProviderName() {
        return Providers.OEMSECRETS;
    }

    @Override
    public Result getData(String query, UserInput userInput, CurrencyRates rates) {
        String jsonString;
        try {
            if (StringUtils.isNotBlank(query)) {
                query = URLEncoder.encode(query, Strings.SYSTEM_ENCODING);
                Request request = ProxyRequestDispatcher.submitRequest(getProviderName(), query);
                request.awaitExecution();
                Request.Query response = request.getAllQueries().iterator().next();
                jsonString = response.getResult();
            } else {
                return new Result(REQUEST_ERROR, "Неверный формат запроса");
            }
        } catch (Exception e) {
            return new Result(CONNECTION_ERROR, ExceptionUtils.getStackTrace(e));
        }

        // Кеши и нужные значения
        LinkedHashMap<String, XmlDocumentBuilder> distributorXmls = new LinkedHashMap<>();
        HashMap<String, BigDecimal> distributorQuotients = new HashMap<>();

        // Парсинг и создание XML
        JSONObject json = new JSONObject(jsonString);

        JSONArray products;
        try {
            products = json.getJSONArray("stock");
        } catch (JSONException je) {
            return new Result(RESPONSE_ERROR, "Не найдены товары");
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
            BigDecimal distributorQuotient = distributorQuotients.get(distributor); // дополнительный коэффициент для цены для поставщика
            if (distributorQuotient == null) {
                Item catalogSettings = null;
                try {
                    catalogSettings = new ItemQuery(PRICE_CATALOG).addParameterEqualsCriteria(ItemNames.price_catalog_.NAME, distributor).loadFirstItem();
                } catch (Exception e) {
                    ServerLogger.error("Unable to load price catalog '" + distributor + "'", e);
                }
                if (catalogSettings != null) {
                    distributorQuotient = catalogSettings.getDecimalValue(ItemNames.price_catalog_.QUOTIENT, BigDecimal.ONE);
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
        XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
        for (String distributor : distributorXmls.keySet()) {
            XmlDocumentBuilder distributorXml = distributorXmls.get(distributor);
            xml.startElement("distributor", "name", distributor);
            xml.addElements(distributorXml.getXmlStringSB());
            xml.endElement(); // distributor
        }

        return new Result(SUCCESS, null, xml);
    }
}
