package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class DigiKeySearch extends Command implements DigiKeyJSONConst{
	//Sandbox Manual login URL "https://sandbox-api.digikey.com/v1/oauth2/authorize?response_type=code&client_id=tdHyZqj8BxOXSXAW5pLO4yUqd8IDrIt7&redirect_uri=http://localhost:8080/ictrade/digikey_manual_authorise";
	//Manual login URL "https://api.digikey.com/v1/oauth2/authorize?response_type=code&client_id=SzCfaUwBSDFOOR7Sk0x6iywEoQxiApeD&redirect_uri=https://ictrade.by/digikey_manual_authorise";
//	private static final String CLIENT_ID = "tdHyZqj8BxOXSXAW5pLO4yUqd8IDrIt7";
//	private static final String CLIENT_SECRET = "omEIbWNfaLUrMQNb";
//	private static final String REDIRECT_URI = "http://localhost:8080/ictrade/digikey_manual_authorise";
//	private static final String SEARCH_URI = "https://sandbox-api.digikey.com/Search/v3/Products/Keyword";
//	private static final String TOKEN_ENDPOINT = "https://sandbox-api.digikey.com/v1/oauth2/token";
	private static final String CLIENT_ID = "SzCfaUwBSDFOOR7Sk0x6iywEoQxiApeD";
	private static final String CLIENT_SECRET = "tHv7wMeAi4SU7eP0";
	private static final String REDIRECT_URI = "https://ictrade.by/digikey_manual_authorise";
	private static final String SEARCH_URI = "https://api.digikey.com/Search/v3/Products/Keyword";
	private static final String TOKEN_ENDPOINT = "https://api.digikey.com/v1/oauth2/token";
	private static final String CUSTOMER_ID = "5201995";
//	private static final String DIGIKEY_BASE_URL = "https://www.digikey.com";

	private String bearerToken, refreshToken;
	private long expiresIn;
	private Properties props;

	private long searchRequestTime;

	private HashSet<String> existingProductCodes = new HashSet<>();

	OkHttpClient client;

	private String createQuery() {
		StringBuilder sb = new StringBuilder();
		String query = getVarSingleValue("q");

		sb
				.append("{ \"Keywords\": \"")
				.append(query)
				.append('\"')
				.append(",\"RecordCount\": 50,")

				//.append(" \"Filters\":{\"TaxonomyIds\":[0],\"ManufacturerIds\": [0]},")

				.append("\"Sort\": {"
						+ "\"SortOption\": \"SortByUnitPrice\","
						+ "\"Direction\": \"Ascending\""
						+ "},")
				.append("\"SearchOptions\": ["
						+ "\"ManufacturerPartSearch\","
				);

		if("0".equals(getVarSingleValue("qty"))){
			sb.append("\"InStock\",");
		}
		sb.append("\"CollapsePackagingTypes\"]}");
		System.out.println(sb);
		return sb.toString();
	}

	@Override
	public ResultPE execute() throws Exception {
		client = (client == null)
				? new OkHttpClient()
				.newBuilder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.writeTimeout(10, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.build()
				: client;

		// --- OAUTH 2.0 ----
		loadProps();

		// check access token expiration time
		long expiresIn = Long.parseLong(props.getProperty("ApiClient.ExpirationDateTime")) - System.currentTimeMillis();

		// refresh if less then 30 seconds This should never actually happen
		if (expiresIn < 30000) {
			refreshAccessToken();
		}

		bearerToken = props.getProperty("ApiClient.AccessToken");

		// --- END OAUTH 2.0 ----

		// Sending search request;
		MediaType mediaType = MediaType.parse("application/json");
		//String inStock = ("0".equals(getVarSingleValue("qty"))) ? ",\"InStock\"" : "";

		String query = createQuery();

		RequestBody body = RequestBody.create(mediaType, query);

		// RequestBody body = RequestBody.create(mediaType, jsonRequestBody);

		Request request = new Request.Builder().url(SEARCH_URI).post(body)
				.addHeader("x-digikey-locale-language", "ru")
				.addHeader("x-digikey-locale-currency", "USD")
				.addHeader("authorization", "Bearer "+bearerToken)
				.addHeader("content-type", "application/json")
				.addHeader("accept", "application/json")
				.addHeader("X-DIGIKEY-Locale-ShipToCountry", "PL")
				.addHeader("X-DIGIKEY-Locale-Site", "RU")
				.addHeader("X-DIGIKEY-Customer-Id", CUSTOMER_ID)
				//.addHeader("X-IBM-Client-Id", CLIENT_ID)
				.addHeader("X-DIGIKEY-Client-Id", CLIENT_ID)
				.build();

		long reqStart = System.currentTimeMillis();
		Response response = client.newCall(request).execute();
		searchRequestTime = System.currentTimeMillis() - reqStart;

		// refresh if less then 10 seconds This should never actually happen
		if (expiresIn < 10000 && expiresIn > 100) {
			refreshAccessToken();
		}
		switch (response.code()) {
			// ok
			case 200:
				return buildXML(response.body().string());
			// bad request
			case 400:
				getResult("bad_requerst");
				System.out.println(response.body().string());
				// unauthorized
			case 401:
				getResult("unauthorized");
			default:
				ResultPE res = getResult("general_error");
				res.addVariable("code", String.valueOf(response.code()));
				return res;
		}
	}

	private ResultPE buildXML(String jsonString) throws Exception {
//		File out = Paths.get(AppContext.getContextPath(), "search_result.json").toFile();
//		FileUtils.deleteQuietly(out);
//		FileUtils.writeStringToFile(out, jsonString, Charset.forName("UTF-8"));
		long start = System.currentTimeMillis();
		XmlDocumentBuilder doc = XmlDocumentBuilder.newDoc();
		Item catalog = ItemQuery.loadSingleItemByName(ItemNames.CATALOG);

		double currencyRatio = catalog.getDoubleValue("currency_ratio_usd", 2.0494);
		double rurRatio = catalog.getDoubleValue("currency_ratio", 100d);
		double q1 = catalog.getDoubleValue("q1_usd", 0d);
		double q2 = catalog.getDoubleValue("q2_usd", 0d);

		JSONObject json = new JSONObject(jsonString);

		doc.startElement("page");
		doc.startElement("base").addText(getUrlBase()).endElement();
		doc.startElement("variables")
				.startElement("q").addText(getVarSingleValue("q")).endElement()
				.startElement("ratio").addText(currencyRatio).endElement()
				.startElement("rur_ratio").addText(rurRatio).endElement()
				.startElement("q1").addText(q1).endElement()
				.startElement("q2").addText(q2).endElement()
				.startElement("view").addText(getVarSingleValue("view")).endElement()
				.startElement("currency").addText(getVarSingleValue("currency")).endElement()
				.startElement("request_time").addText(searchRequestTime).endElement()
				.startElement("results").addText(json.get(JSON_PRODUCT_COUNT)).endElement()
				//.startElement("exact").addText(json.get(JSON_EXACT)).endElement()
				.endElement();
		try {
			JSONObject exactProduct = json.getJSONObject(JSON_EXACT);
			convertProductJsonToXml(doc, exactProduct);
		}catch (Exception e){}
		//addJSONArrayToResults(doc, json, JSON_EXACT);
		addJSONArrayToResults(doc, json, JSON_MANUFACTURER_PRODUCT);
		addJSONArrayToResults(doc, json, JSON_RESULTS);
		doc.startElement("elapsed_time").addText(System.currentTimeMillis() - start).endElement();
		doc.endElement();
		ResultPE result = getResult("complete");
		result.setValue(doc.toString());
		return result;
	}

	private void addJSONArrayToResults(XmlDocumentBuilder doc, JSONObject searchResults, String arrayKey) {
		try {
			JSONArray parts = searchResults.getJSONArray(arrayKey);
			if (parts != null) {
				for (int i = 0; i < parts.length(); i++) {
					JSONObject part = parts.getJSONObject(i);
					convertProductJsonToXml(doc, part);
				}
			}
		}catch (JSONException e){}
	}

	private void convertProductJsonToXml(XmlDocumentBuilder doc, JSONObject part){
		String code = part.getString(JSON_CODE);
		if(existingProductCodes.contains(code)) return;
		existingProductCodes.add(code);
		JSONObject vendor = part.getJSONObject(JSON_VENDOR);
		String vendorName = vendor.getString(JSON_VALUE);
		doc.startElement(PRODUCT, "id", part.getString(JSON_CODE) +"-dgk");
		doc.startElement(NAME).addText(part.getString(JSON_NAME)).endElement();
		doc.startElement(CODE).addText(code).endElement();
		doc.startElement(VENDOR_CODE).addText(part.getString(JSON_VENDOR_CODE)).endElement();
		doc.startElement(VENDOR).addText(vendorName).endElement();
		doc.startElement(MAIN_PIC).addText(part.get(JSON_PIC)).endElement();
		doc.startElement(PRICE).addText(part.get(JSON_PRICE)).endElement();
		doc.startElement(MIN_QTY).addText(part.get(JSON_MIN_QTY)).endElement();
		doc.startElement(QTY).addText(part.get(JSON_QTY)).endElement();
		doc.startElement(DESCRIPTION).addText(part.get(JSON_DESCRIPTION)).endElement();
		String url = part.get(JSON_URL).toString().replace(".by", ".com");
		doc.startElement(OLD_URL).addText(url).endElement();
		doc.startElement(MANUAL).addText(part.get(JSON_MANUAL)).endElement();
		JSONArray priceMap = part.getJSONArray(JSON_PRICE_MAP);
		StringBuilder tmp = new StringBuilder();
		for (int i = 0; i < priceMap.length(); i++) {
			JSONObject specPrice = priceMap.getJSONObject(i);
			tmp.append(specPrice.get(JSON_SPEC_QTY));
			tmp.append(':');
			tmp.append(specPrice.get(JSON_PRICE));
			tmp.append(';');
			doc.startElement("spec_price_map", "qty", specPrice.get(JSON_SPEC_QTY), "price", specPrice.get(JSON_PRICE)).endElement();
		}
		doc.startElement("spec_price").addText(tmp.toString()).endElement();
		//Lead status and RoHSStatus;
		doc.startElement(PARAM, "name", "RoHSStatus").addText(part.get("RoHSStatus")).endElement();
		doc.startElement(PARAM, "name", "LeadStatus").addText(part.get("LeadStatus")).endElement();

		JSONArray params = part.getJSONArray(JSON_PARAMS);
		for (int i = 0; i < params.length(); i++) {
			JSONObject param = params.getJSONObject(i);
			doc.startElement(PARAM, "name", param.get(JSON_PARAM)).addText(param.get(JSON_VALUE)).endElement();
		}
		doc.endElement();
	}

	public ResultPE getAccessCodeAndAuthorise() throws Exception {
		client = (client == null) ? new OkHttpClient() : client;
		String code = getVarSingleValue("code");
		loadProps();
		props.setProperty("ApiClient.AccessCode", code);

		//Authorising by Access Code
		RequestBody authcodeReqBody = new FormBody.Builder()
				.add("code", code)
				.add("client_id", CLIENT_ID)
				.add("client_secret", CLIENT_SECRET)
				.add("redirect_uri", REDIRECT_URI)
				.add("grant_type", "authorization_code")
				.build();

		//Exchange code for token
		Request authCodePost = new Request.Builder()
				.url(TOKEN_ENDPOINT).post(authcodeReqBody)
				.addHeader("accept", "application/json")
				.build();
		Response authResponce = client.newCall(authCodePost).execute();

		if (authResponce.isSuccessful()) {
			JSONObject responceJSON = new JSONObject(authResponce.body().string());
			bearerToken = responceJSON.getString("access_token");
			refreshToken = responceJSON.getString("refresh_token");
			expiresIn = responceJSON.getLong("expires_in");

			props.setProperty("ApiClient.AccessToken", bearerToken);
			props.setProperty("ApiClient.RefreshToken", refreshToken);
			props.setProperty("ApiClient.ExpirationDateTime", String.valueOf(System.currentTimeMillis() + (expiresIn * 1000)));
			saveProps();
			return getResult("success");
		}

		ResultPE res = getResult("error");
		res.setVariable("responce_body", authResponce.body().string());
		return res;
	}


	public ResultPE refreshAccessToken() throws Exception {
		client = (client == null) ? new OkHttpClient() : client;
		if (props == null)
			loadProps();
		refreshToken = props.getProperty("ApiClient.RefreshToken");
		RequestBody refreshTokenPostBody = new FormBody.Builder()
				.add("refresh_token", refreshToken)
				.add("client_id", CLIENT_ID)
				.add("client_secret", CLIENT_SECRET)
				.add("grant_type", "refresh_token")
				.build();
		Request refreshRequerst = new Request.Builder()
				.url(TOKEN_ENDPOINT).post(refreshTokenPostBody)
				.addHeader("accept", "application/json")
				.build();
		Response responce = client.newCall(refreshRequerst).execute();
		JSONObject responceJSON = new JSONObject(responce.body().string());
		bearerToken = responceJSON.getString("access_token");
		refreshToken = responceJSON.getString("refresh_token");
		expiresIn = responceJSON.getLong("expires_in");

		props.setProperty("ApiClient.AccessToken", bearerToken);
		props.setProperty("ApiClient.RefreshToken", refreshToken);
		props.setProperty("ApiClient.ExpirationDateTime", String.valueOf(System.currentTimeMillis() + (expiresIn * 1000)));
		saveProps();
		return null;
	}


	private void saveProps() throws Exception {
		if (props == null)
			loadProps();
		try (FileWriter writer = new FileWriter(Paths.get(AppContext.getContextPath(), "WEB-INF", "apiclient.properties").toFile());) {
			props.store(writer, "API Client Configuration");
			writer.close();
		}
	}

	private void loadProps() throws Exception {
		try (FileReader reader = new FileReader(Paths.get(AppContext.getContextPath(), "WEB-INF", "apiclient.properties").toFile());) {
			props = new Properties();

			props.load(reader);
		} catch (Exception e) {
			throw e;
		}
	}
}
