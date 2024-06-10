package extra;

import ecommander.controllers.PageController;
import ecommander.fwk.JsoupUtils;
import ecommander.fwk.OkWebClient;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.pages.Command;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.LinkPE;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SearchOctopartApiCommand extends Command {

	public static final String PRICES_TAG = "prices";
	public static final String BREAK_TAG = "break";
	public static final String QTY_ATTR = "qty";
	public static final String PRICE_TAG = "price";


	private static final String XML_API_PAGE = "api_xml";
	private static final String API_BASE_PART = ""; // /search
	private static final String API_QUERY_NAME = "query";
	private static final String SERVER_PARAM = "server";
	private static final String QUERY_PARAM = "q";

	@Override
	public ResultPE execute() throws Exception {
		String server = getVarSingleValue(SERVER_PARAM);
		List<Object> queries = getVarValues(QUERY_PARAM);
		if (queries.size() == 0) {
			LinkedHashMap<Long, Item> prods = getLoadedItems("prod");
			for (Item prod : prods.values()) {
				queries.add(prod.getValue(ItemNames.product_.NAME));
			}
		}
		String serverResultXml = null;
		// если параметр server пустой - значит нужен локальный запрос (локальная страница)
		try {
			StringBuilder apiQueryPart = new StringBuilder();
			for (Object query : queries) {
				String queryString = StringUtils.normalizeSpace((String) query);
				if (queryString.length() >= 3)
					apiQueryPart.append(apiQueryPart.length() == 0 ? '?' : '&').append(API_QUERY_NAME).append('=').append(queryString);
			}
			if (StringUtils.isNotBlank(apiQueryPart)) {
				if (StringUtils.isBlank(server)) {
					LinkPE apiLink = LinkPE.newDirectLink("link", XML_API_PAGE, false);
					String url = apiLink.serialize() + API_BASE_PART + apiQueryPart;
					ExecutablePagePE customerTemplate = getExecutablePage(url);
					ByteArrayOutputStream resultBytes = new ByteArrayOutputStream();
					PageController.newSimple().executePage(customerTemplate, resultBytes);
					serverResultXml = resultBytes.toString("UTF-8");
				}
				// если задан параметр server - значит надо подулючаться удаленному серверу
				else {
					String url = server + API_BASE_PART + apiQueryPart;
					serverResultXml = OkWebClient.getInstance().getString(url);
				}
			} else {
				return getResult("illegal_argument").setValue("Неверный формат запроса");
			}
		} catch (Exception e) {
			return getResult("error").setValue(ExceptionUtils.getStackTrace(e));
		}
		Document doc = JsoupUtils.parseXml(serverResultXml);
		Elements parts = doc.getElementsByTag("part");
		//StringBuilder sb = new StringBuilder();
		CurrencyRates rates = new CurrencyRates();
		Item catalogSettings = new ItemQuery(ItemNames.PRICE_CATALOG).addParameterEqualsCriteria(ItemNames.price_catalog_.NAME, "octopart").loadFirstItem();
		BigDecimal extraQuotient = BigDecimal.ONE; // дополнительный коэффициент для цены
		String nextDelivery = "14 дней"; // дополнительный коэффициент для цены
		if (catalogSettings != null) {
			extraQuotient = catalogSettings.getDecimalValue(ItemNames.price_catalog_.QUOTIENT, BigDecimal.ONE);
			nextDelivery = catalogSettings.getStringValue(ItemNames.price_catalog_.DEFAULT_SHIP_TIME, "");
		}
		XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
		xml.startElement("results"); // <results>
		for (Element partEl : parts) {
			String query = JsoupUtils.getTagFirstValue(partEl, "mpn");
			String category = JsoupUtils.getTagFirstValue(partEl.select("category"), "name");
			String vendor = JsoupUtils.getTagFirstValue(partEl.select("manufacturer"), "name");
			String defaultDesc = "";
			Elements descs = partEl.select("descriptions");
			HashMap<String, String> distributorDescs = new HashMap<>();
			for (Element descEl : descs) {
				String desc = JsoupUtils.getTagFirstValue(descEl, "text");
				String distr = JsoupUtils.getTagFirstValue(descEl, "credit_string");
				distributorDescs.put(distr, desc);
				if (desc.length() > defaultDesc.length())
					defaultDesc = desc;
			}
			xml.addElement("query", query);
			Elements allSellersEls = partEl.getElementsByTag("sellers");
			for (Element sellersEl : allSellersEls) {
				String distributorName = JsoupUtils.getTagFirstValue(sellersEl.select("company"), "name");
				Elements offers = sellersEl.getElementsByTag("offers");
				for (Element offer : offers) {
					String id = JsoupUtils.getTagFirstValue(offer, "id");
					String name = JsoupUtils.getTagFirstValue(offer, "sku");
					String minQty = JsoupUtils.getTagFirstValue(offer, "moq");
					String qty = JsoupUtils.getTagFirstValue(offer, "inventory_level");
					String desc = distributorDescs.getOrDefault(distributorName, defaultDesc);
					xml.startElement("product", "id", id); // <product>
					xml.addElement("code", id);
					xml.addElement("name", name);
					xml.addElement("vendor", vendor);
					xml.addElement("name_extra", category);
					xml.addElement("next_delivery", nextDelivery);
					xml.addElement("category_id", distributorName);
					xml.addElement("qty", qty);
					xml.addElement("description", desc);
					xml.addElement("min_qty", minQty);
					xml.startElement("prices"); // <prices>
					Elements prices = offer.select("prices");
					for (Element pricesEl : prices) {
						BigDecimal price = DecimalDataType.parse(JsoupUtils.getTagFirstValue(pricesEl, "price"), 2);
						price = price.multiply(extraQuotient).setScale(4, RoundingMode.UP);
						String currency = JsoupUtils.getTagFirstValue(pricesEl, "currency");
						String breakQty = JsoupUtils.getTagFirstValue(pricesEl, "quantity");
						xml.startElement("break", "qty", breakQty); // <break>
						xml.addElement("price_original", price.toPlainString());
						rates.setAllPricesXML(xml, price, currency);
						xml.endElement(); // </break>
					}
					xml.endElement(); // </prices>
					xml.endElement(); // </product>
				}
			}
		}
		xml.endElement(); // </results>
		return getResult("product_list").setValue(xml.toString());
	}
}
