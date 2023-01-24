package extra;

import ecommander.controllers.PageController;
import ecommander.fwk.JsoupUtils;
import ecommander.fwk.OkWebClient;
import ecommander.fwk.Pair;
import ecommander.fwk.Strings;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.pages.Command;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.LinkPE;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchApiCommand extends Command {

	public static final String PRICES_TAG = "prices";
	public static final String BREAK_TAG = "break";
	public static final String QTY_ATTR = "qty";
	public static final String PRICE_TAG = "price";


	private static final String XML_API_PAGE = "api_xml";
	private static final String API_QUERY_PART = "/search?name=";
	private static final String SERVER_PARAM = "server";
	private static final String QUERY_PARAM = "q";

	@Override
	public ResultPE execute() throws Exception {
		String server = getVarSingleValue(SERVER_PARAM);
		String query = getVarSingleValue(QUERY_PARAM);
		String xml = null;
		// если параметр server пустой - значит нужен локальный запрос (локальная страница)
		try {
			if (StringUtils.isBlank(server)) {
				LinkPE apiLink = LinkPE.newDirectLink("link", XML_API_PAGE, false);
				String url = apiLink.serialize() + API_QUERY_PART + query;
				ExecutablePagePE customerTemplate = getExecutablePage(url);
				ByteArrayOutputStream resultBytes = new ByteArrayOutputStream();
				PageController.newSimple().executePage(customerTemplate, resultBytes);
				xml = resultBytes.toString("UTF-8");
			}
			// если задан параметр server - значит надо подулючаться удаленному серверу
			else {
				String url = server + API_QUERY_PART + query;
				xml = OkWebClient.getInstance().getString(url);
			}
		} catch (Exception e) {
			return getResult("error").setValue(ExceptionUtils.getStackTrace(e));
		}
		Document doc = JsoupUtils.parseXml(xml);
		Elements providers = doc.getElementsByTag("provider");
		StringBuilder sb = new StringBuilder();
		CurrencyRates rates = new CurrencyRates();
		for (Element provider : providers) {
			String providerId = JsoupUtils.getTagValue(provider, "id");
			Elements results = provider.getElementsByTag("results");
			HashMap<String, ArrayList<Element>> identicalProducts = new HashMap<>();
			// Сначала просто сгруппировать результаты по названию продукта (группы цен)
			for (Element result : results) {
				String name = JsoupUtils.getTagValue(result, "name");
				ArrayList<Element> identical = identicalProducts.get(name);
				if (identical == null) {
					identical = new ArrayList<>();
					identicalProducts.put(name, identical);
				}
				identical.add(result);
			}
			// Теперь взять первый результат за основу и добавить в него все цены из других товаров
			for (String name : identicalProducts.keySet()) {
				ArrayList<Element> identical = identicalProducts.get(name);
				Element result = identical.get(0);
				result.tagName("product");
				String newCode = Strings.createXmlElementName(name) + "_" + Strings.createXmlElementName(providerId);
				result.attr("id", newCode);
				result.prependElement("code").text(newCode);
				Element prices = result.appendElement(PRICES_TAG);
				for (Element priceElement : identical) {
					Element aBreak = prices.appendElement(BREAK_TAG);
					aBreak.attr(QTY_ATTR, JsoupUtils.getTagValue(priceElement, "pricebreak"));
					String priceStr = JsoupUtils.getTagValue(priceElement, PRICE_TAG);
					BigDecimal price = DecimalDataType.parse(priceStr, 4);
					if (price != null) {
						String currency = StringUtils.defaultIfBlank(JsoupUtils.getTagValue(result, "currency"), "USD");
						rates.setAllPricesJsoup(aBreak, price, currency);
					}
				}
				sb.append(result.outerHtml()).append("\n");
			}
		}
		return getResult("product_list").setValue(sb.toString());
	}
}
