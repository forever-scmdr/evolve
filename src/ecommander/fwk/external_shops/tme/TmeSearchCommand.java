package ecommander.fwk.external_shops.tme;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import extra.JsoupXmlFixer;
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
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class TmeSearchCommand extends Command {

	private static final String TOKEN = "f78df804c072cb6380857f8a4a02872ec9a9b70ee0c8ca2c09";
	private static final String SECRET = "02cbf5d58df9b4c01b84";
	private static final String ENCRYPTION_METHOD = "HmacSHA1";
	private static final String SEARCH_URL = "https://api.tme.eu/Products/Search.xml";
	private static final String PRICING_URL = "https://api.tme.eu/Products/GetPricesAndStocks.xml";
	private static final String PARAMS_URL = "https://api.tme.eu/Products/GetParameters.xml";
	private static final String DOCUMENTATION_URL = "https://api.tme.eu/Products/GetProductsFiles.xml";
	//private static final String LANGUAGES_URL = "https://api.tme.eu/Utils/GetLanguages.xml";
	//private static final String COUNTRIES_URL = "https://api.tme.eu/Utils/GetCountries.xml";
	private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	HashMap<String, Element> productMap;

	@Override
	public ResultPE execute() throws Exception {
		String query = getVarSingleValue("q");
		if(StringUtils.isBlank(query)){
			setPageVariable("error", "empty_query");
			return getResult("result");
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

		XmlDocumentBuilder outputPage = XmlDocumentBuilder.newDocPart();
		outputPage.addElements(tmeResponse);
		params = new TreeMap<>();
		addGeneralParams(params);
		Document searchResult = Jsoup.parse(outputPage.toString(), "", Parser.xmlParser());
		productMap = new HashMap<>();
		Elements products = searchResult.select("Product");
		for (Element product : products){
			String symbol = product.select("Symbol").text();
			productMap.put(symbol, product);
		}

		params = new TreeMap<>();
		addGeneralParams(params);

		ArrayList<String> pricing = loadExtraInfo(PRICING_URL, productMap.keySet());
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

		ArrayList<String> techParams = loadExtraInfo(PARAMS_URL, productMap.keySet());

		for(String tech : techParams){
			Document techDoc = Jsoup.parse(tech, "", Parser.xmlParser());
			Elements techList = techDoc.select("Product");
			for(Element el : techList){
				String symbol = el.select("Symbol").text();
				Element paramsEl = el.select("ParameterList").first();
				Element product = productMap.get(symbol);
				product.appendChild(paramsEl);
			}
		}

		ArrayList<String> documentations = loadExtraInfo(DOCUMENTATION_URL, productMap.keySet());
		for(String documentation : documentations){
			Document docDoc = Jsoup.parse(documentation,"",Parser.xmlParser());
			Elements docList = docDoc.select("Product");
			for(Element el : docList){
				String symbol = el.select("Symbol").text();
				Element paramsEl = el.select("DocumentList").first();
				Element product = productMap.get(symbol);
				product.appendChild(paramsEl);
			}
		}

		ResultPE result = getResult("result");
		result.setValue(JsoupXmlFixer.fix(searchResult.outerHtml()));
		return result;
	}

	/**
	 * Загрузить доллнительную информацию о неких товарах. Какую именно информацию, указано в параметре url.
	 * Список артикулов - в параметре  productCodes. Возаращает массив XML документов с нужной информацией
	 * @param url
	 * @param productCodes
	 */
	private ArrayList<String> loadExtraInfo(String url, Collection<String> productCodes) throws Exception {
		TreeMap<String, String> params = new TreeMap<>();
		addGeneralParams(params);
		ArrayList<String> pricing = new ArrayList<>();
		int i = 0;
		for(String code : productCodes){
			String key = "SymbolList["+i+"]";
			params.put(key, code);
			i++;
			if(i > 9){
				pricing.add(signAndSubmit(url, params));
				params = new TreeMap<>();
				addGeneralParams(params);
			}
		}
		if(params.size() > 4){
			pricing.add(signAndSubmit(url, params));
		}
		return pricing;
	}

	private String signAndSubmit(String url, TreeMap<String, String> params) throws Exception {
		String signature = generateSignature(url, params);
		params.put("ApiSignature", signature);
		return loadFromTmeAPI(url, params);
	}

	private void addGeneralParams(Map<String,String> params){
		params.put("Token", TOKEN);
		params.put("Country", "BY");
		params.put("Currency", "EUR");
		params.put("Language", "RU");
	}

	private String loadFromTmeAPI(String searchUrl, Map<String, String> params) throws IOException {
		byte[] postedData = preparePostData(params);

		HttpURLConnection conn = (HttpURLConnection)new URL(searchUrl).openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postedData.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postedData);

		Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
		StringBuilder responseContent = new StringBuilder();

		for (int item; (item = in.read()) >= 0;) {
			responseContent.append((char) item);
		}
		return responseContent.toString();
	}

	private byte[] preparePostData(Map<String, String> params) throws UnsupportedEncodingException {
		StringBuilder postData = new StringBuilder();
		for (Map.Entry<String, String> param : params.entrySet()) {
			if (postData.length() != 0)
				postData.append('&');

			postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			postData.append('=');
			postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
		}
		return postData.toString().getBytes(StandardCharsets.UTF_8);
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
