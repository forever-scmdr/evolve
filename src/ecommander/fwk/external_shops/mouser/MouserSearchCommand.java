package ecommander.fwk.external_shops.mouser;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import okhttp3.OkHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MouserSearchCommand extends Command implements MouserJsonConst {
	private static final String ENDPOINT = "https://api.mouser.com/api/v1/search/keyword?apiKey=2184963c-0916-4dd6-a5da-a51c3e166907";
	private static final String KEYWORD_REQUEST = "{\"%s\":{\"keyword\": \"%s\",\"records\":\"50\",\"startingRecord\":\"0\"%s}}";
	private static final String PART_NUMBER_REQUEST = "{\"mouserPartNumber\":\"%s\"}";
	private static final String PART_NUMBER_PATTERN = "\\d{5}";
	private OkHttpClient client;

	@Override
	public ResultPE execute() throws Exception {
		String keyword = getVarSingleValue("q");
		boolean inStock = "0".equals(getVarSingleValue("qty"));
		boolean isPartNumber = keyword.matches(PART_NUMBER_PATTERN);
//		boolean isPartNumber = true;
		String stock = inStock? ",\"searchOptions\":\"InStock\"":"";

		String endpoint = isPartNumber? String.format(ENDPOINT, "partnumber") : String.format(ENDPOINT, "keyword");
		String requestBody = isPartNumber? String.format(PART_NUMBER_REQUEST,"SearchByPartRequest", keyword) : String.format(KEYWORD_REQUEST, "SearchByKeywordRequest", keyword, stock);

		String listOfParts = loadFromExternalAPI(endpoint, requestBody);

		System.out.println(listOfParts);

		return null;
	}

	private String loadFromExternalAPI(String endpoint, String requestBody) throws IOException {
		HttpURLConnection conn = createConnection(endpoint, requestBody);
		return fetch(conn);
	}

	public ResultPE loadManufacturerList() throws IOException{
		String manufacturersUrl = "https://api.mouser.com/api/v2/search/manufacturerlist?apiKey=2184963c-0916-4dd6-a5da-a51c3e166907";
		StringBuilder result = new StringBuilder();
		URL url = new URL(manufacturersUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(conn.getInputStream()))) {
			for (String line; (line = reader.readLine()) != null; ) {
				result.append(line);
			}
		}
		String content = result.toString();
		System.out.println(content);

		return null;
	}

	private String fetch(HttpURLConnection conn) throws IOException {
		Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
		StringBuilder responseContent = new StringBuilder();

		for (int item; (item = in.read()) >= 0;) {
			responseContent.append((char) item);
		}
		return responseContent.toString();
	}

	private HttpURLConnection createConnection(String endpoint, String requestJson) throws IOException {

		byte[] encodedRequestBody = requestJson.getBytes(StandardCharsets.UTF_8);

		HttpURLConnection conn = (HttpURLConnection)new URL(endpoint).openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Content-Length", String.valueOf(encodedRequestBody.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(encodedRequestBody);
		return conn;
	}
}
