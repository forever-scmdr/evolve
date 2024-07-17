package ecommander.special.portal.outer.providers;

import ecommander.fwk.Strings;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.special.portal.outer.Request;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

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
    public void processQueryResult(Request.Query query, OuterInputData input) throws Exception {
        XmlDocumentBuilder queryXml = XmlDocumentBuilder.newDocPart();

        // <server/>
        addServerElement(queryXml, query);
        if (query.getStatus() != Request.Status.SUCCESS) {
            String errorType = query.getStatus() == Request.Status.PROXY_FAILURE ? "proxy_failure" : "provider_failure";
            queryXml.addElement("error", query.getResultString(StandardCharsets.UTF_8), "type", errorType);
            query.setProcessedResult(queryXml);
            return;
        }

        String jsonString = query.getResultString(StandardCharsets.UTF_8);

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
            query.setProcessedResult(queryXml);
            return;
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
            BigDecimal distributorQuotient = getDistributorQuotient(distributor, input);
            String sku = product.get("sku").toString();
            String partNumber = product.get("source_part_number").toString();
            String leadTime = product.get("lead_time").toString();
            String vendor = product.getString("manufacturer");
            String minQtyStr = Objects.toString(product.get("moq"), "0");
            String qtyStr = Objects.toString(product.get("quantity_in_stock"), "");
            String justNumbersQty = RegExUtils.replaceAll(qtyStr, "\\D+", "");
            String name = sku;
            String code = partNumber;
            if (StringUtils.isBlank(code)) {
                code = name + "_" + Integer.toHexString(distributor.hashCode());
            }
            String key = Strings.translit(name + " " + code);
            boolean isExactMatch = StringUtils.equalsAnyIgnoreCase(name, query.query) || StringUtils.equalsIgnoreCase(code, query.query);
            boolean isValid = NumberUtils.toInt(justNumbersQty, -1) > 0;
            distrXml.startElement("product", "id", code, "key", key, "query_exact_match", isExactMatch);
            distrXml.addElement("code", code);
            distrXml.addElement("name", name);
            distrXml.addElement("vendor", vendor);
            distrXml.addElement("qty", justNumbersQty);
            distrXml.addElement("description", product.getString("description"));
            distrXml.addElement("next_delivery", leadTime);
            distrXml.addElement("container", product.getString("packaging"));
            distrXml.addElement("category_id", distributor);
            JSONArray currencyBreakArray;
            boolean hasPrice = false;
            try {
                JSONObject pricesObject = product.getJSONObject("prices");
                currencyBreakArray = pricesObject.getJSONArray(StringUtils.upperCase(input.getCurCode()));
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
                        distrXml.addElement("currency_id", input.getCurCode());
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
                        hasPrice |= priceOriginal.compareTo(BigDecimal.ZERO) > 0;
                        // Применить коэффициент
                        priceOriginal = priceOriginal.multiply(distributorQuotient).setScale(4, RoundingMode.UP);
                        input.getRates().setAllPricesXML(distrXml, priceOriginal, input.getCurCode());
                    }
                    distrXml.endElement(); // break
                    // Запомнить минимальное количество, т.к. это будет минимальное количество и шаг заказа
                    if (j == 0 && (StringUtils.isBlank(minQtyStr) || NumberUtils.toInt(minQtyStr, 0) <= 0))
                        minQtyStr = breakQty.intValue() + "";
                }
                if (!noBreaks) {
                    distrXml.endElement(); // prices
                }
            }
            // Запись минимального заказа, он же шаг заказа. Если явно не указан - взять минимальный price break
            distrXml.addElement("min_qty", minQtyStr);
            distrXml.addElement("step", minQtyStr);

            isValid &= hasPrice;
            // Отменить девайс, если он не валиден
            if (isValid) {
                distrXml.endElement(); // product
            } else {
                distrXml.cancelElement(); // product cancel
            }
        }

        // Теперь взять XML всех производителей и объединить по порядку появления
        for (String distributor : distributorXmls.keySet()) {
            StringBuilder distributorResult = distributorXmls.get(distributor).getXmlStringSB();
            if (StringUtils.isNotBlank(distributorResult)) {
                queryXml.addElements(distributorResult);
            }
        }
        query.setProcessedResult(queryXml);
    }
}
