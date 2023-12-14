package extra;

import ecommander.fwk.JsoupUtils;
import ecommander.fwk.OkWebClient;
import ecommander.fwk.Strings;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
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
import java.util.List;


/**
 * Формирует следующую структуру из https://www.findchips.com
 *

 <result>
 	<query>HMC0603JT100M</query>
 	<distributor name="Farnell">
		<product id="708-HMC0603JT100M">
			<code>708-HMC0603JT100M</code>
			<name>HMC0603JT100M</name>
			<vendor>SEI Stackpole Electronics Inc</vendor>
 			<qty>40512</qty>
			<currency_id>USD</currency_id>
			<pricebreak>3</pricebreak>
			<step>500</step>
			<min_qty>500</min_qty>
 			<description>Thick Film Resistors - SMD 100MOhms 0603 0.1W 5% High Value</description>
			<next_delivery>32 Weeks, 1 Days</next_delivery>
 			<container>Reel</container>
			<prices>
				<break qty="1">
					<price_original>0.06</price_original>
					<price_USD>0.06</price_USD>
					<price_BYN>0.17</price_BYN>
					<price>4.63</price>
					<price_RUB>4.63</price_RUB>
					<price_EUR>0.05</price_EUR>
				</break>
				<break qty="1000">
					<price_original>0.04</price_original>
					<price_USD>0.04</price_USD>
					<price_BYN>0.13</price_BYN>
					<price>3.45</price>
					<price_RUB>3.45</price_RUB>
					<price_EUR>0.04</price_EUR>
				</break>
				<break qty="5000">
					<price_original>0.04</price_original>
					<price_USD>0.04</price_USD>
					<price_BYN>0.12</price_BYN>
					<price>3.23</price>
					<price_RUB>3.23</price_RUB>
					<price_EUR>0.04</price_EUR>
 				</break>
			 </prices>
 		</product>
 		<product>
 			...
 		</product>
 			...
 	</distributor>
 </result>



 *
 *
 */
public class SearchFindchipsCommand extends Command {

	private static final String SERVER_PARAM = "server";
	private static final String QUERY_PARAM = "q";

	@Override
	public ResultPE execute() throws Exception {
		List<Object> servers = getVarValues(SERVER_PARAM);
		String query = getVarSingleValue(QUERY_PARAM);
		String html = null;

		// Получение ответа сервера
		XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
		xml.startElement("result");
		for (Object server : servers) {
			try {
				query = StringUtils.normalizeSpace(query);
				query = URLEncoder.encode(query, Strings.SYSTEM_ENCODING);
				if (StringUtils.isNotBlank(query)) {
					if (StringUtils.isNotBlank((String) server)) {
						String requestUrl = server + query;
						html = OkWebClient.getInstance().getString(requestUrl);
					}
					// если задан параметр server - значит надо подулючаться удаленному серверу
					else {
						return getResult("illegal_argument").setValue("Не указан сервер");
					}
				} else {
					return getResult("illegal_argument").setValue("Неверный формат запроса");
				}
			} catch (Exception e) {
				return getResult("error").setValue(ExceptionUtils.getStackTrace(e));
			}

			// Загрузка курсов валют
			CurrencyRates rates = new CurrencyRates();
			Item catalogSettings = new ItemQuery(ItemNames.PRICE_CATALOG).addParameterEqualsCriteria(ItemNames.price_catalog_.NAME, "api").loadFirstItem();
			BigDecimal extraQuotient = BigDecimal.ONE; // дополнительный коэффициент для цены
			if (catalogSettings != null)
				extraQuotient = catalogSettings.getDecimalValue(ItemNames.price_catalog_.QUOTIENT, BigDecimal.ONE);

			// Парсинг и создание XML
			Document doc = Jsoup.parse(html);
			Elements distributors = doc.select("div.distributor-results");
			if (distributors.size() == 0)
				return getResult("error").setValue("Не найден элемент дистрибьютора");
			xml.addElement("query", query);
			for (Element distributorEl : distributors) {
				String distributor = distributorEl.attr("data-distributor_name");
				xml.startElement("distributor", "name", distributor);
				Elements lines = distributorEl.select("tr.row");
				for (Element line : lines) {
					String name = JsoupUtils.getSelectorFirstValue(line, "td:eq(0) a");
					String code = JsoupUtils.getSelectorFirstValue(line, "span.additional-value");
					String vendor = JsoupUtils.getSelectorFirstValue(line, "td:eq(1)");
					String description = JsoupUtils.getSelectorFirstValue(line, "td:eq(2) span:eq(0)");
					String minQtyStr = JsoupUtils.getSelectorFirstValue(line, "td:eq(2) span[data-title='Min Qty']");
					String leadTime = JsoupUtils.getSelectorFirstValue(line, "td:eq(2) span[data-title='Lead time']");
					String stepStr = minQtyStr;
					String container = JsoupUtils.getSelectorFirstValue(line, "td:eq(2) span[data-title='Container']");
					String qtyStr = JsoupUtils.getSelectorFirstValue(line, "td:eq(3)");
					xml.startElement("product", "id", code);
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
							rates.setAllPricesXML(xml, priceOriginal, currencyCode);
						}
						xml.endElement(); // break
					}
					if (!noBreaks) {
						xml.endElement(); // prices
					}
					xml.endElement(); // product
				}
				xml.endElement(); // distributor
			}
		}
		xml.endElement(); // result

		return getResult("product_list").setValue(xml.toString());
	}
}
