package extra;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class TmeSearchCommand extends Command {

	private static final String TOKEN = "f78df804c072cb6380857f8a4a02872ec9a9b70ee0c8ca2c09";
	private static final String SECRET = "02cbf5d58df9b4c01b84";
	private static final String ENCRYPTION_METHOD = "HmacSHA1";
	private static final String SEARCH_URL = "https://api.tme.eu/Products/Search.xml";
	private static final String PRICING_URL = "https://api.tme.eu/Products/GetPricesAndStocks.xml";
	private static final String LANGUAGES_URL = "https://api.tme.eu/Utils/GetLanguages.xml";
	private static final String COUNTRIES_URL = "https://api.tme.eu/Utils/GetCountries.xml";
	private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	@Override
	public ResultPE execute() throws Exception {
		String query = getVarSingleValue("query");
		if(StringUtils.isBlank(query)){
			ResultPE result = getResult("success");
			XmlDocumentBuilder outputPage = XmlDocumentBuilder.newDoc();
			outputPage.startElement("page", "name", getPageName());
			outputPage.startElement("base").addText(getUrlBase()).endElement();
			outputPage.addElement("error", "empty_query");
			outputPage.endElement();
			result.setValue(outputPage.toString());
			return result;
		}

		query = StringUtils.normalizeSpace(query);

		TreeMap<String, String> params = new TreeMap<>(); // Because we needs sorted parameters. And will sort them on add.
		params.put("SearchPlain", query);
		addGeneralParams(params);

		boolean inStock = Integer.parseInt(getVarSingleValueDefault("minqty", "-1")) > -1;
		if(inStock){
			params.put("SearchWithStock", "true");
		}

		String signature = generateSignature(SEARCH_URL, params);
		params.put("ApiSignature", signature);

		String tmeResponse = loadFromTmeAPI(SEARCH_URL, params);
		tmeResponse = StringUtils.substringAfter(tmeResponse, XML_DECLARATION);

		XmlDocumentBuilder outputPage = XmlDocumentBuilder.newDoc();
		outputPage.startElement("page", "name", getPageName());
		outputPage.startElement("base").addText(getUrlBase()).endElement();

		outputPage.startElement("variables")
				.startElement("query").addText(getVarSingleValue("query")).endElement()
				.startElement("view").addText(getVarSingleValue("view")).endElement()
				.startElement("currency").addText(getVarSingleValue("currency")).endElement()
				.startElement("minqty").addText(getVarSingleValue("minqty")).endElement()
				.endElement();

		addCurrencyRatios(outputPage);

		outputPage.addElements(tmeResponse);
		outputPage.endElement();

		params = new TreeMap<String, String>();
		addGeneralParams(params);
		Document searchResult = Jsoup.parse(outputPage.toString(), "", Parser.xmlParser());
		HashMap<String, Element> productMap = new HashMap<>();
		Elements products = searchResult.select("Product");
		for (Element product : products){
			String symbol = product.select("Symbol").text();
			productMap.put(symbol, product);
		}

		params = new TreeMap<>();
		addGeneralParams(params);

		ArrayList<String> pricing = new ArrayList<>();

		int i = 0;
		for(String symbol : productMap.keySet()){
			String key = "SymbolList["+i+"]";
			params.put(key, symbol);
			i++;
			if(i > 9){
				i = 0;
				signature = generateSignature(PRICING_URL, params);
				params.put("ApiSignature", signature);
				String pricingResponse = loadFromTmeAPI(PRICING_URL, params);
				pricing.add(pricingResponse);

				params = new TreeMap<>();
				addGeneralParams(params);
			}
		}

		if(params.size() > 4){
			signature = generateSignature(PRICING_URL, params);
			params.put("ApiSignature", signature);

			String pricingResponse = loadFromTmeAPI(PRICING_URL, params);
			pricing.add(pricingResponse);
		}

		for(String priceList : pricing){
			Document priceDoc = Jsoup.parse(priceList, "", Parser.xmlParser());
			Elements priceMap = priceDoc.select("Product");
			for(Element m : priceMap){
				String symbol = m.select("Symbol").text();
				Element qty = m.select("Amount").last();
				Element p = m.select("PriceList").first();
				Element product = productMap.get(symbol);
				product.appendChild(qty);
				product.appendChild(p);
			}
		}

		ResultPE result = getResult("success");
		result.setValue(searchResult.outerHtml());
		return result;
	}

	private void addCurrencyRatios(XmlDocumentBuilder doc) throws Exception {
		Item currencies = ItemQuery.loadSingleItemByName(ItemNames.CATALOG);
		if(currencies != null){
			doc.
					startElement(ItemNames.CATALOG, "id", currencies.getId())
					.addElements(currencies.outputValues())
					.endElement();
		}
	}

	private void addGeneralParams(Map<String,String> params){
		params.put("Token", TOKEN);
		params.put("Country", "BY");
		params.put("Currency", "EUR");
		params.put("Language", "RU");
	}

	private String loadFromTmeAPI(String searchUrl, Map<String, String> params) throws IOException {
		byte[] postedData = preparePostData(searchUrl, params);

		HttpURLConnection conn = (HttpURLConnection)new URL(searchUrl).openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postedData.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postedData);

		Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		StringBuilder responseContent = new StringBuilder();

		for (int item; (item = in.read()) >= 0;) {
			responseContent.append((char) item);
		}
		return responseContent.toString();
	}

	private byte[] preparePostData(String searchUrl, Map<String, String> params) throws UnsupportedEncodingException {
		StringBuilder postData = new StringBuilder();

		for (Map.Entry<String, String> param : params.entrySet()) {
			if (postData.length() != 0)
				postData.append('&');

			postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			postData.append('=');
			postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
		}

		return postData.toString().getBytes("UTF-8");
	}

	private String generateSignature(String url, TreeMap<String, String> params) throws Exception{
		String encodedParams = encodeParams(params);
		String signatureBase = "POST&" + URLEncoder.encode(url, "UTF-8") + "&" + URLEncoder.encode(encodedParams, "UTF-8");
		return hmacEncrypt(signatureBase);
	}

	private String hmacEncrypt(String signatureBase) throws InvalidKeyException, NoSuchAlgorithmException {
		Mac hmac = Mac.getInstance(ENCRYPTION_METHOD);
		SecretKeySpec secret = new SecretKeySpec(SECRET.getBytes(), ENCRYPTION_METHOD);
		hmac.init(secret);
		byte[] result = hmac.doFinal(signatureBase.getBytes());
		return Base64.getEncoder().encodeToString(result);
	}

	private String encodeParams(TreeMap<String,String> sortedParams) throws Exception{
		URIBuilder builder = new URIBuilder();
		sortedParams.forEach(builder::addParameter);
		return builder
				.build()
				.toString()
				.substring(1) // Without "?"
				.replaceAll("[+]", "%20") // Encode + char
				.replaceAll("%7E", "~"); // Revert ~ char
	}
}