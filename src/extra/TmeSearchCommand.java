package extra;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.TreeMap;

public class TmeSearchCommand extends Command {

	private static final String TOKEN = "";
	private static final String SECRET = "";
	private static final String ENCRYPTION_METHOD = "HmacSHA1";
	private static final String SEARCH_URL = "https://api.tme.eu/Products/Search.xml";
	private static final String LANGUAGES_URL = "https://api.tme.eu/Utils/GetLanguages.xml";
	private static final String COUNTRIES_URL = "https://api.tme.eu/Utils/GetCountries.xml";

	@Override
	public ResultPE execute() throws Exception {
		String query = getVarSingleValue("query");
		if(StringUtils.isBlank(query)){return getResult("empty_query");}

		query = StringUtils.normalizeSpace(query);

		TreeMap<String, String> params = new TreeMap<>(); // Because we needs sorted parameters. And will sort them on add.
		params.put("Token", TOKEN);

		params.put("SearchPlain", query);
		params.put("Country", "BY");
		params.put("Currency", "EUR");
		params.put("Language", "RU");

		boolean inStock = Integer.parseInt(getVarSingleValueDefault("minqty", "-1")) > -1;
		if(inStock){
			params.put("SearchWithStock", "true");
		}

		String signature = generateSignature(SEARCH_URL, params);
		params.put("ApiSignature", signature);

		String TmeResponse = postQueryToTmeAPI(SEARCH_URL, params);

		return getResult("success");
	}

	private String postQueryToTmeAPI(String searchUrl, TreeMap<String, String> params) {
		StringBuilder responce = new StringBuilder();

		return responce.toString();
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
