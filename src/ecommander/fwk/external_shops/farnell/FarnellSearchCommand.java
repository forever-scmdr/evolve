package ecommander.fwk.external_shops.farnell;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.integration.CatalogConst;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import extra.JsoupXmlFixer;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
		put("userInfo.customerId", "epasschipru");
	}};

	private static final String IN_STOCK_AND_ROHS = "resultsSettings.refinements.filters";
	private static final String FARNELL_BASE = "https://api.element14.com/catalog/products";
	private static final String OFFSET = "resultsSettings.offset";
	private static final String NUMBER_OF_RESULTS = "resultsSettings.numberOfResults";
	private static final String TERM = "term";
	//private static final String ENTITY_DECLARATION = "\n<!DOCTYPE farnell_products [<!ENTITY nbsp \"&#160;\">]>\n";
	private static final String SIGNATURE_VAR = "userInfo.signature";
	private static final String TIMESTAMP_VAR = "userInfo.timestamp";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

	private Document doc;

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

		LocalDateTime nowUTC = LocalDateTime.now(ZoneId.of("UTC"));
		String formattedNow = DATE_FORMATTER.format(nowUTC)+'T'+TIME_FORMATTER.format(nowUTC);
		urlBuilder.append('&').append(TIMESTAMP_VAR).append('=').append(formattedNow);

		String signature = generateSignature(formattedNow);
		urlBuilder.append('&').append(SIGNATURE_VAR).append('=').append(signature);

		System.out.println(urlBuilder);

		doc = Jsoup.parse(new URL(urlBuilder.toString()), 5000);

		Element root = doc.getElementsByTag("keywordSearchReturn").first();

		setPageVariable("offset", String.valueOf(offset));
		setPageVariable("limit", String.valueOf(limit));

		ResultPE result;
		try {
			result = getResult("result");
		} catch (EcommanderException e) {
			ServerLogger.error("no result, named \"result\" found", e);
			return null;
		}
		String output = JsoupXmlFixer.fix(root.outerHtml());
		//output = output.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + ENTITY_DECLARATION);
		result.setValue(output);
		return result;
	}

	private String generateSignature(String formattedNow) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException{
		String toEncode = "searchByKeyword" + formattedNow;
		SecretKeySpec keySpec = new SecretKeySpec("8iRUet0c7csU".getBytes("UTF-8"), "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(keySpec);
		String hash = Base64.encodeBase64String(mac.doFinal(toEncode.getBytes()));
		return URLEncoder.encode(hash, "UTF-8");
	}

}
