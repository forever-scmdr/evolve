package ecommander.special.portal.outer.providers;

import ecommander.fwk.*;
import ecommander.model.Item;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.special.portal.outer.ProxyRequestDispatcher;
import ecommander.special.portal.outer.Request;
import extra.CurrencyRates;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Загружает и разбирает информацию с findchips.com
 */
public class FindchipsGetter implements ProviderGetter, ItemNames {

    @Override
    public String getProviderName() {
        return Providers.FINDCHIPS;
    }

    @Override
    public Result getData(String query, UserInput userInput, CurrencyRates rates) {
        String html;
        try {
            if (StringUtils.isNotBlank(query)) {
                query = URLEncoder.encode(query, Strings.SYSTEM_ENCODING);
                Request request = ProxyRequestDispatcher.submitRequest(getProviderName(), query);
                request.awaitExecution();
                Request.Query response = request.getAllQueries().iterator().next();
                html = response.getResult();
            } else {
                return new Result(REQUEST_ERROR, "Неверный формат запроса");
            }
        } catch (Exception e) {
            return new Result(CONNECTION_ERROR, ExceptionUtils.getStackTrace(e));
        }

        // Результирующий документ
        XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();

        // Парсинг и создание XML
        Document doc = Jsoup.parse(html);
        Elements distributors = doc.select("div.distributor-results");
        if (distributors.size() == 0)
            return new Result(RESPONSE_ERROR, "Не найден элемент дистрибьютора");

        for (Element distributorEl : distributors) {
            String distributor = distributorEl.attr("data-distributor_name");
            if (!userInput.distributorFilterMatches(distributor)) {
                continue;
            }
            // Загрузка настроек поставщика
            Item catalogSettings = null;
            try {
                catalogSettings = new ItemQuery(PRICE_CATALOG).addParameterEqualsCriteria(ItemNames.price_catalog_.NAME, distributor).loadFirstItem();
            } catch (Exception e) {
                ServerLogger.error("Unable to load price catalog '" + distributor + "'", e);
            }
            BigDecimal extraQuotient = BigDecimal.ONE; // дополнительный коэффициент для цены
            if (catalogSettings != null)
                extraQuotient = catalogSettings.getDecimalValue(ItemNames.price_catalog_.QUOTIENT, BigDecimal.ONE);

            xml.startElement("distributor", "name", distributor);
            Elements lines = distributorEl.select("tr.row");
            for (Element line : lines) {
                String name = JsoupUtils.getSelectorFirstValue(line, "td:eq(0) a");
                String code = JsoupUtils.getSelectorFirstValue(line, "span.additional-value");
                if (StringUtils.isBlank(code)) {
                    code = name + "_" + Integer.toHexString(distributor.hashCode());
                }
                String key = Strings.translit(name + " " + code);
                String vendor = JsoupUtils.getSelectorFirstValue(line, "td:eq(1)");
                String description = JsoupUtils.getSelectorFirstValue(line, "td:eq(2) span:eq(0)");
                String minQtyStr = JsoupUtils.getSelectorFirstValue(line, "td:eq(2) span[data-title='Min Qty']");
                String leadTime = JsoupUtils.getSelectorFirstValue(line, "td:eq(2) span[data-title='Lead time']");
                String stepStr = minQtyStr;
                String container = JsoupUtils.getSelectorFirstValue(line, "td:eq(2) span[data-title='Container']");
                String qtyStr = JsoupUtils.getSelectorFirstValue(line, "td:eq(3)");
                xml.startElement("product", "id", code, "key", key);
                xml.addElement("code", code);
                xml.addElement("name", name);
                xml.addElement("vendor", vendor);
                xml.addElement("qty", qtyStr);
                xml.addElement("step", stepStr);
                xml.addElement("description", description);
                xml.addElement("min_qty", minQtyStr);
                xml.addElement("next_delivery", leadTime);
                xml.addElement("container", container);
                xml.addElement("category_id", distributor);
                Elements priceLis = line.select("td.td-price li[class]");
                xml.addElement("pricebreak", priceLis.size());
                boolean noCurrency = true;
                boolean noBreaks = true;
                BigDecimal maxPrice = BigDecimal.ONE.negate();
                BigDecimal minPrice = BigDecimal.valueOf(Double.MAX_VALUE);
                for (Element priceLi : priceLis) {
                    String breakQtyStr = JsoupUtils.getSelectorFirstValue(priceLi, "span.label");
                    Element data = priceLi.select("span.value").first();
                    if (data == null)
                        continue;
                    String priceStr = data.attr("data-baseprice");
                    String currencyCode = data.attr("data-basecurrency");
                    if (noCurrency) {
                        xml.addElement("currency_id", currencyCode);
                        noCurrency = false;
                    }
                    if (noBreaks) {
                        xml.startElement("prices");
                        noBreaks = false;
                    }
                    xml.startElement("break", "qty", breakQtyStr);
                    BigDecimal priceOriginal = DecimalDataType.parse(priceStr, 4);
                    if (priceOriginal != null) {
                        xml.addElement("price_original", priceOriginal);
                        // Применить коэффициент
                        priceOriginal = priceOriginal.multiply(extraQuotient).setScale(4, RoundingMode.UP);
                        HashMap<String, BigDecimal> allPricesDecimal = rates.setAllPricesXML(xml, priceOriginal, currencyCode);
                        BigDecimal currentPrice = allPricesDecimal.get(userInput.getPriceParamName());
                        if (currentPrice != null) {
                            maxPrice = maxPrice.max(currentPrice);
                            minPrice = minPrice.min(currentPrice);
                            userInput.setGlobalMaxPrice(userInput.getGlobalMaxPrice().max(currentPrice));
                            userInput.setGlobalMinPrice(userInput.getGlobalMinPrice().min(currentPrice));
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
                // Если девайс не подходит по фильтрам - добавить тэг <invalid>invalid</invalid>
                boolean isInvalid
                        = !userInput.shipDateFilterMatches(leadTime)
                        || !userInput.vendorFilterMatches(vendor)
                        || !userInput.distributorFilterMatches(distributor)
                        || !userInput.fromPriceFilterMatches(maxPrice)
                        || !userInput.toPriceFilterMatches(minPrice);

                if (isInvalid) {
                    xml.addElement("invalid", "invalid");
                }
                xml.endElement(); // product
            }
            xml.endElement(); // distributor
        }
        return new Result(SUCCESS, null, xml);
    }
}
