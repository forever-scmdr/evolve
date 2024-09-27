package extra;

import ecommander.controllers.PageController;
import ecommander.fwk.JsoupUtils;
import ecommander.fwk.OkWebClient;
import ecommander.fwk.Strings;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.pages.Command;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.LinkPE;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.special.portal.outer.currency.CurrencyRates;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SearchApiCommand extends Command {

	public static final String PRICES_TAG = "prices";
	public static final String BREAK_TAG = "break";
	public static final String QTY_ATTR = "qty";
	public static final String PRICE_TAG = "price";
	public static final String PROVIDER_PRICE_TAG = "provider_price";


	private static final String XML_API_PAGE = "api_xml";
	private static final String API_BASE_PART = "/search";
	private static final String API_QUERY_NAME = "name";
	private static final String SERVER_PARAM = "server";
	private static final String QUERY_PARAM = "q";

	@Override
	public ResultPE execute() throws Exception {
		String server = getVarSingleValue(SERVER_PARAM);
		List<Object> queries = getVarValues(QUERY_PARAM);
		String xml = null;
		// если параметр server пустой - значит нужен локальный запрос (локальная страница)
		try {
			StringBuilder apiQueryPart = new StringBuilder();
			for (Object query : queries) {
				String queryString = StringUtils.normalizeSpace((String) query);
				if (queryString.length() >= 3) {
					queryString = URLEncoder.encode(queryString, Strings.SYSTEM_ENCODING);
					apiQueryPart.append(apiQueryPart.length() == 0 ? '?' : '&').append(API_QUERY_NAME).append('=').append(queryString);
				}
			}
			if (StringUtils.isNotBlank(apiQueryPart)) {
				if (StringUtils.isBlank(server)) {
					LinkPE apiLink = LinkPE.newDirectLink("link", XML_API_PAGE, false);
					String url = apiLink.serialize() + API_BASE_PART + apiQueryPart;
					ExecutablePagePE customerTemplate = getExecutablePage(url);
					ByteArrayOutputStream resultBytes = new ByteArrayOutputStream();
					PageController.newSimple().executePage(customerTemplate, resultBytes);
					xml = resultBytes.toString("UTF-8");
				}
				// если задан параметр server - значит надо подулючаться удаленному серверу
				else {
					String url = server + API_BASE_PART + apiQueryPart;
					xml = OkWebClient.getInstance().getString(url);
				}
			} else {
				return getResult("illegal_argument").setValue("Неверный формат запроса");
			}
		} catch (Exception e) {
			return getResult("error").setValue(ExceptionUtils.getStackTrace(e));
		}
		Document doc = JsoupUtils.parseXml(xml);
		Elements providers = doc.getElementsByTag("provider");
		//StringBuilder sb = new StringBuilder();
		LinkedHashMap<String, StringBuilder> queryResults = new LinkedHashMap<>();
		CurrencyRates rates = new CurrencyRates();
		Item catalogSettings = new ItemQuery(ItemNames.PRICE_CATALOG).addParameterEqualsCriteria(ItemNames.price_catalog_.NAME, "api").loadFirstItem();
		BigDecimal extraQuotient = BigDecimal.ONE; // дополнительный коэффициент для цены
		if (catalogSettings != null)
			extraQuotient = catalogSettings.getDecimalValue(ItemNames.price_catalog_.QUOTIENT, BigDecimal.ONE);
		for (Element provider : providers) {
			String providerId = JsoupUtils.getTagFirstValue(provider, "id");
			Elements replies = provider.getElementsByTag("reply");
			for (Element reply : replies) {
				String query = JsoupUtils.getTagFirstValue(reply, "key");
				Elements results = reply.getElementsByTag("results");
				HashMap<String, ArrayList<Element>> identicalProducts = new HashMap<>();
				// Сначала просто сгруппировать результаты по названию продукта (группы цен)
				for (Element result : results) {
					String name = JsoupUtils.getTagFirstValue(result, "name");
					ArrayList<Element> identical = identicalProducts.get(name);
					if (identical == null) {
						identical = new ArrayList<>();
						identicalProducts.put(name, identical);
					}
					identical.add(result);
				}
				// Теперь взять первый результат за основу и добавить в него все цены из других товаров
				StringBuilder sb = new StringBuilder();
				for (String name : identicalProducts.keySet()) {
					ArrayList<Element> identical = identicalProducts.get(name);
					Element result = identical.get(0);
					result.tagName(ItemNames.PRODUCT);
					String newCode = Strings.createXmlElementName(name) + "_" + Strings.createXmlElementName(providerId);
					result.attr("id", newCode);
					result.prependElement(ItemNames.product_.CODE).text(newCode);
					String currency = StringUtils.defaultIfBlank(JsoupUtils.getTagFirstValue(result, "currency"), "USD");
					Element currencyEl = result.getElementsByTag("currency").first();
					if (currencyEl != null)
						currencyEl.tagName(ItemNames.product_.CURRENCY_ID);
					result.appendElement(ItemNames.product_.CATEGORY_ID).text(providerId);
					Element prices = result.appendElement(PRICES_TAG);
					for (Element priceElement : identical) {
						Element aBreak = prices.appendElement(BREAK_TAG);
						aBreak.attr(QTY_ATTR, JsoupUtils.getTagFirstValue(priceElement, "pricebreak"));
						String priceStr = JsoupUtils.getTagFirstValue(priceElement, PRICE_TAG);
						BigDecimal price = DecimalDataType.parse(priceStr, 4);
						if (price != null) {
							// Добавить тэг с оригинальной ценой (в оригинальной валюте)
							aBreak.appendElement(ItemNames.product_.PRICE_ORIGINAL).text(priceStr);
							// Применить коэффициент
							price = price.multiply(extraQuotient).setScale(4, RoundingMode.UP);
							rates.setAllPricesJsoup(aBreak, price, currency);
						}
					}
					sb.append(result.outerHtml()).append("\n");
				}
				// Добавить к запросу (выдаче по предыдущим поставщикам) выдачу по текущему поставщику
				if (StringUtils.isNotBlank(sb)) {
					StringBuilder storedResult = queryResults.get(query);
					if (storedResult == null) {
						storedResult = new StringBuilder();
						queryResults.put(query, storedResult);
					}
					if (StringUtils.isNotBlank(storedResult))
						storedResult.append('\n');
					storedResult.append(sb);
				}
			}
		}
		// Собрать из результатов по запросам общий XML документ
		XmlDocumentBuilder resultDoc = XmlDocumentBuilder.newDocPart();
		for (String query : queryResults.keySet()) {
			resultDoc.startElement("results").addElement("query", query);
			resultDoc.addElements(queryResults.get(query));
			resultDoc.endElement();
		}
		return getResult("product_list").setValue(resultDoc.toString());
	}
}
