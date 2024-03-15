package ecommander.special.portal.outer.providers;

import ecommander.fwk.JsoupUtils;
import ecommander.fwk.Strings;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.special.portal.outer.ProxyRequestDispatcher;
import ecommander.special.portal.outer.Request;
import extra.CurrencyRates;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

/**
 * Загружает и разбирает информацию с findchips.com
 * Объект класса хранится в единственном экземпляре
 */
public class FindchipsGetter extends ProviderGetter {

    @Override
    public String getProviderName() {
        return Providers.FINDCHIPS;
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
            // XML запроса
            XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();

            int qty = userInput.getQueries().get(query.query);
            String q = query.query;
            // <query> - открывающий
            xml.startElement("query", "q", q, "qty", qty, "millis", query.getProcessMillis(), "tries", query.getNumTries());
            if (query.getStatus() != Request.Status.SUCCESS) {
                String errorType = query.getStatus() == Request.Status.PROXY_FAILURE ? "proxy_failure" : "provider_failure";
                xml.addElement("error", query.getResult(), "type", errorType);
                xml.endElement(); // </query> - закрывающий (т.к. далее continue)
                query.setProcessedResult(xml);
                continue;
            }

            // Парсинг и создание XML
            Document doc = Jsoup.parse(query.getResult());
            Elements distributors = doc.select("div.distributor-results");
            if (distributors.size() == 0) {
                query.setStatus(Request.Status.HOST_FAILURE);
                xml.addElement("error", "Не найден элемент дистрибьютора", "type", "wrong_format");
                xml.endElement(); // </query> - закрывающий (т.к. далее continue)
                query.setProcessedResult(xml);
                continue;
            }

            for (Element distributorEl : distributors) {
                String distributor = distributorEl.attr("data-distributor_name");
                if (!userInput.distributorFilterMatches(distributor)) {
                    continue;
                }

                BigDecimal extraQuotient = getDistributorQuotient(distributor, distributorQuotients); // дополнительный коэффициент для цены

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
            xml.endElement(); // </query> - закрывающий
            query.setProcessedResult(xml);
        }
        return result;
    }
}
