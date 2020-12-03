package extra;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

public class FarnellSearchCommand extends Command implements CatalogConst {
	private static final LinkedHashMap<String, String> URL_MANDATORY_CONSTANTS = new LinkedHashMap<String, String>() {{
		put("callInfo.responseDataFormat", "XML");
		put("callInfo.omitXmlSchema", "true");
		put("storeInfo.id", "ru.farnell.com");
		put("callInfo.apiKey", "nqwuz6kvr25knx8v7q3eyjm7");
		put("resultsSettings.responseGroup", "large");
		put("versionNumber", "1.1");
	}};

	private static final String IN_STOCK_AND_ROHS = "resultsSettings.refinements.filters";
	private static final String FARNELL_BASE = "https://api.element14.com/catalog/products";
	private static final String OFFSET = "resultsSettings.offset";
	private static final String NUMBER_OF_RESULTS = "resultsSettings.numberOfResults";
	private static final String TERM = "term";
	private static final String ENTITY_DECLARATION = "\n<!DOCTYPE farnell_products [<!ENTITY nbsp \"&#160;\">]>\n";

	private Document doc;
	private Element variables;

	@Override
	public ResultPE execute() throws Exception {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(FARNELL_BASE);
		int i = 0;
		for (Map.Entry<String, String> e : URL_MANDATORY_CONSTANTS.entrySet()) {
			char sep = i == 0 ? '?' : '&';
			urlBuilder.append(sep);
			urlBuilder.append(e.getKey()).append('=').append(e.getValue());
			i++;
		}
		String pageVar = getVarSingleValue("page");
		String limitVar = getVarSingleValue("limit");
		String inStockVar = getVarSingleValue("minqty");

		int limit = StringUtils.isBlank(limitVar)? 30 : Integer.parseInt(limitVar);
		int page = StringUtils.isBlank(pageVar)? 1 : Integer.parseInt(pageVar);
		int offset = (page - 1) * limit;

		int stock = StringUtils.isBlank(inStockVar)? -1 : Integer.parseInt(inStockVar);
		if(stock > -1){
			urlBuilder.append('&').append(IN_STOCK_AND_ROHS).append('=').append("inStock");
		}
		urlBuilder.append('&').append(OFFSET).append('=').append(offset);
		urlBuilder.append('&').append(NUMBER_OF_RESULTS).append('=').append(limit);

		String query = URLEncoder.encode(':' + getVarSingleValue("q"), StandardCharsets.UTF_8.toString());

		urlBuilder.append('&').append(TERM).append('=').append("any").append(query);
		System.out.println(urlBuilder);

		doc = Jsoup.parse(new URL(urlBuilder.toString()), 5000);

		addVarToResult("query", getVarSingleValue("q"));
		addVarToResult("currency", getVarSingleValue("currency"));
		addVarToResult("view", getVarSingleValue("view"));

		Item catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
		addVarToResult("rur_ratio", catalog.outputValue("currency_ratio"));
		addVarToResult("eur_ratio", catalog.outputValue("currency_ratio_eur"));
		addVarToResult("q1_eur", catalog.outputValue("q1_eur"));
		addVarToResult("q2_eur", catalog.outputValue("q2_eur"));
		addVarToResult("offset", String.valueOf(offset));
		addVarToResult("limit", String.valueOf(limit));

		ResultPE result;
		try {
			result = getResult("complete");
		} catch (EcommanderException e) {
			ServerLogger.error("no result found", e);
			return null;
		}
		String output = doc.outerHtml();
		output = output.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + ENTITY_DECLARATION);
		result.setValue(output);
		return result;
	}

	/**
	 * Appends variables to document
	 * @param name - variable name;
	 * @param value - variable value;
	 */
	private void addVarToResult(String name, String value){
		if(variables == null){
			variables = doc.getElementsByTag("keywordSearchReturn").first().prependElement("variables");
		}
		if(StringUtils.isBlank(name)) return;
		value = StringUtils.isBlank(value)? "" : value;
		variables.appendElement(name).html(value);
	}

	public static void main(String[] args) {
		String encodedString = "The quick brown fox jumps over the lazy dog";
		String secretKey = "key";
		String alg = "HmacSHA1";
		try {
			SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), alg);
			Mac mac = Mac.getInstance(alg);
			mac.init(keySpec);
			String hash = Base64.encodeBase64String(mac.doFinal(encodedString.getBytes()));
			System.out.println(hash);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}
	}
}
