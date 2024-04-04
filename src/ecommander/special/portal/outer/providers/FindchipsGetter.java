package ecommander.special.portal.outer.providers;

import ecommander.fwk.JsoupUtils;
import ecommander.fwk.Strings;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.special.portal.outer.Request;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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
    public void processQueryResult(Request.Query query, OuterInputData input) throws Exception {

        // XML запроса
        XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();

        // <server/>
        addServerElement(xml, query);
        if (query.getStatus() != Request.Status.SUCCESS) {
            String errorType = query.getStatus() == Request.Status.PROXY_FAILURE ? "proxy_failure" : "provider_failure";
            xml.addElement("error", query.getResult(), "type", errorType);
            query.setProcessedResult(xml);
            return;
        }

        // Парсинг и создание XML
        Document doc = Jsoup.parse(query.getResult());
        Elements distributors = doc.select("div.distributor-results");
        if (distributors.size() == 0) {
            query.setStatus(Request.Status.HOST_FAILURE);
            xml.addElement("error", "Не найден элемент дистрибьютора", "type", "wrong_format");
            query.setProcessedResult(xml);
            return;
        }

        for (Element distributorEl : distributors) {
            String distributor = distributorEl.attr("data-distributor_name");
            if (!input.distributorFilterMatches(distributor)) {
                continue;
            }

            BigDecimal extraQuotient = getDistributorQuotient(distributor, input); // дополнительный коэффициент для цены

            xml.startElement("distributor", "name", distributor);
            boolean hasValidProducts = false;
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
                String justNumbersQty = RegExUtils.replaceAll(qtyStr, "\\D+", "");
                boolean isExactMatch = StringUtils.equalsAnyIgnoreCase(name, query.query) || StringUtils.equalsIgnoreCase(code, query.query);
                boolean isValid = NumberUtils.toInt(justNumbersQty, -1) > 0;
                xml.startElement("product", "id", code, "key", key, "query_exact_match", isExactMatch);
                xml.addElement("code", code);
                xml.addElement("name", name);
                xml.addElement("vendor", vendor);
                xml.addElement("qty", justNumbersQty);
                xml.addElement("step", stepStr);
                xml.addElement("description", description);
                xml.addElement("min_qty", minQtyStr);
                xml.addElement("next_delivery", leadTime);
                xml.addElement("container", container);
                xml.addElement("category_id", distributor);
                Elements priceLis = line.select("td.td-price li[class]");
                xml.addElement("pricebreak", priceLis.size());
                boolean noCurrency = true;
                boolean hasPrice = false;
                ArrayList<PriceBreak> prices = new ArrayList<>();
                // Разбор элементов с ценой
                for (Element priceLi : priceLis) {
                    String breakQtyStr = JsoupUtils.getSelectorFirstValue(priceLi, "span.label");
                    int breakQty = NumberUtils.toInt(breakQtyStr, -1);
                    Element data = priceLi.select("span.value").first();
                    if (data == null || breakQty <= 0)
                        continue;
                    String priceStr = data.attr("data-baseprice");
                    String currencyCode = data.attr("data-basecurrency");
                    BigDecimal priceOriginal = DecimalDataType.parse(priceStr, 4);
                    prices.add(new PriceBreak(breakQty, currencyCode, priceOriginal));
                    if (noCurrency) {
                        xml.addElement("currency_id", currencyCode);
                        noCurrency = false;
                    }
                }
                // Сортировка элементов с ценой и запись их в нужном порядке в XML
                if (prices.size() > 0) {
                    prices.sort(Comparator.comparingInt(o -> o.qty));
                    xml.startElement("prices");
                    for (PriceBreak p : prices) {
                        xml.startElement("break", "qty", p.qty);
                        if (p.priceOriginal != null) {
                            hasPrice |= p.priceOriginal.compareTo(BigDecimal.ZERO) > 0;
                            xml.addElement("price_original", p.priceOriginal);
                            // Применить коэффициент
                            BigDecimal priceMultiplied = p.priceOriginal.multiply(extraQuotient).setScale(4, RoundingMode.UP);
                            input.getRates().setAllPricesXML(xml, priceMultiplied, p.curCode);
                        }
                        xml.endElement(); // break
                    }
                    xml.endElement(); // prices
                }

                isValid &= hasPrice;

                // Отменить девайс, если он не валиден
                if (isValid) {
                    hasValidProducts = true;
                    xml.endElement(); // product
                } else {
                    xml.cancelElement(); // product cancel
                }
            }
            // Если в поставщике есть валидные товары - добавить поставщика
            if (hasValidProducts) {
                xml.endElement(); // distributor
            } else {
                xml.cancelElement();
            }
        }
        query.setProcessedResult(xml);

    }

}
