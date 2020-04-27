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
import org.json.JSONObject;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class DigiKeySearch extends Command {
	private static final String CLIENT_ID = "36c0ed09-22b8-4ee2-8af9-7b04915ee7ac";
	private static final String CLIENT_SECRET = "A5qE8iN0tH3fG6sN5xL7wI2mC3bK5cH6yJ1bB8oR6lB5rH5tE4";
	private static final String REDIRECT_URI = "https://ictrade.by/digikey_manual_authorise";
	private static final String AUTH_ENDPOINT = "https://sso.digikey.com/as/token.oauth2";
	private static final String CUSTOMER_ID = "5201995";

	private String bearerToken, refreshToken;
	private long expiresIn;
	private Properties props;

	private long searchRequestTime;

	OkHttpClient client;

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
		String inStock = ("0".equals(getVarSingleValue("qty"))) ? ",\"InStock\"" : "";

		RequestBody body = RequestBody.create(mediaType, "{\"SearchOptions\":[\"CollapsePackingTypes\"" + inStock + "],\"Keywords\":\""
				+ getVarSingleValue("old_query")
				+ "\",\"RecordCount\":\"50\",\"RecordStartPosition\":\"0\",\"Sort\":{\"Option\":\"SortByUnitPrice\",\"Direction\":\"Ascending\",\"SortParameterId\":\"50\"}}");

		// RequestBody body = RequestBody.create(mediaType, jsonRequestBody);

		Request request = new Request.Builder().url("https://api.digikey.com/services/partsearch/v2/keywordsearch").post(body)
				.addHeader("x-digikey-locale-language", "ru")
				.addHeader("x-digikey-locale-currency", "USD")
				.addHeader("authorization", bearerToken)
				.addHeader("content-type", "application/json")
				.addHeader("accept", "application/json")
				.addHeader("X-DIGIKEY-Locale-ShipToCountry", "PL")
				.addHeader("X-DIGIKEY-Locale-Site", "BY")
				.addHeader("X-DIGIKEY-Customer-Id", CUSTOMER_ID)
				.addHeader("X-IBM-Client-Id", CLIENT_ID).build();

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
				// unauthorized
			case 401:
				getResult("unauthorized");
			default:
				return getResult("general_error");
		}

	}

	private ResultPE buildXML(String jsonString) throws Exception {
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
				.startElement("q").addText(getVarSingleValue("old_query")).endElement()
				.startElement("ratio").addText(currencyRatio).endElement()
				.startElement("rur_ratio").addText(rurRatio).endElement()
				.startElement("q1").addText(q1).endElement()
				.startElement("q2").addText(q2).endElement()
				.startElement("currency").addText(getVarSingleValue("currency")).endElement()
				.startElement("request_time").addText(searchRequestTime).endElement()
				.startElement("results").addText(json.get("Results")).endElement()
				.startElement("exact").addText(json.get("ExactMatches")).endElement()
				.endElement();


		JSONArray parts = json.getJSONArray("Parts");

		for (int i = 0; i < parts.length(); i++) {

			JSONObject part = parts.getJSONObject(i);
			JSONObject manuf = part.getJSONObject("ManufacturerName");
			String producer = manuf.getString("Text");

			doc.startElement("product", "id", part.get("PartId") + "-dgk")
					.startElement("name").addText(part.get("ProductDescription")).endElement()
					.startElement("code").addText(part.get("DigiKeyPartNumber")).endElement()
					.startElement("producer_code").addText(part.get("ManufacturerPartNumber")).endElement()
					.startElement("main_pic").addText(part.get("PrimaryPhoto")).endElement()
					.startElement("price").addText(part.get("UnitPrice")).endElement()
					.startElement("min_qty").addText(part.get("MinimumOrderQuantity")).endElement()
					.startElement("qty").addText(part.get("QuantityOnHand")).endElement()
					.startElement("description").addText(part.get("DetailedDescription")).endElement()
					.startElement("url").addText(part.get("PartUrl")).endElement()
					.startElement("producer").addText(producer).endElement();

			JSONArray params = part.getJSONArray("Parameters");
			for (int j = 0; j < params.length(); j++) {
				JSONObject param = params.getJSONObject(j);
				doc.startElement("parameter", "name", param.get("Parameter")).addText(param.get("Value")).endElement();
			}
			doc.endElement();
		}
		doc.startElement("elapsed_time").addText(System.currentTimeMillis() - start).endElement();
		doc.endElement();
		ResultPE result = getResult("complete");
		result.setValue(doc.toString());
		return result;
	}

	public ResultPE refreshAccessToken() throws Exception {
		client = (client == null) ? new OkHttpClient() : client;
		if (props == null)
			loadProps();
		refreshToken = props.getProperty("ApiClient.RefreshToken");
		RequestBody refreshTokenPostBody = new FormBody.Builder().add("refresh_token", refreshToken).add("client_id", CLIENT_ID)
				.add("client_secret", CLIENT_SECRET).add("grant_type", "refresh_token").build();
		Request refreshRequerst = new Request.Builder().url(AUTH_ENDPOINT).post(refreshTokenPostBody).addHeader("accept", "application/json")
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

	public ResultPE getAccessCodeAndAuthorise() throws Exception {
		client = (client == null) ? new OkHttpClient() : client;
		String code = getVarSingleValue("code");
		loadProps();
		props.setProperty("ApiClient.AccessCode", code);

		//Authorising by Access Code
		RequestBody authcodeReqBody = new FormBody.Builder()
				.add("grant_type", "authorization_code").add("code", code).add("client_id", CLIENT_ID).add("client_secret", CLIENT_SECRET)
				.add("redirect_uri", REDIRECT_URI).build();
		Request authCodePost = new Request.Builder().url(AUTH_ENDPOINT).post(authcodeReqBody).addHeader("accept", "application/json")
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
		return getResult("error");
	}

	private void saveProps() throws Exception {
		if (props == null)
			loadProps();
		try (FileWriter writer = new FileWriter(Paths.get(AppContext.getContextPath(), "WEB-INF", "apiclient.properties").toFile());) {
			props.store(writer, "API Client Configuration");
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
