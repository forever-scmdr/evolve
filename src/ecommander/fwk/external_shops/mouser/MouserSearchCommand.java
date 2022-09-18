package ecommander.fwk.external_shops.mouser;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MouserSearchCommand extends Command{
	private static final String ENDPOINT = "https://api.mouser.com/api/v1/search/keyword?apiKey=80dc6a51-a23c-406f-ace3-1f3bfe4da4d5";
	private static final String KEYWORD_REQUEST = "{\"%s\":{\"keyword\": \"%s\",\"records\":\"50\",\"startingRecord\":\"0\"%s}}";
	private static final String PART_NUMBER_REQUEST = "{\"mouserPartNumber\":\"%s\"}";
	private static final String PART_NUMBER_PATTERN = "\\d{5}";

	@Override
	public ResultPE execute() throws Exception {
		String keyword = getVarSingleValue("q");
		boolean inStock = "0".equals(getVarSingleValue("minqty"));
		//boolean isPartNumber = keyword.matches(PART_NUMBER_PATTERN);
		boolean isPartNumber = false;
		String stock = inStock? ",\"searchOptions\":\"InStock\"":"";

		String endpoint = isPartNumber? String.format(ENDPOINT, "partnumber") : String.format(ENDPOINT, "keyword");
		String requestBody = isPartNumber? String.format(PART_NUMBER_REQUEST,"SearchByPartRequest", keyword) : String.format(KEYWORD_REQUEST, "SearchByKeywordRequest", keyword, stock);

		String listOfParts = loadFromExternalAPI(endpoint, requestBody);

		String res = MouserJsonToXMLConverter.convert(listOfParts);

		ResultPE result = getResult("result");
		result.setValue(res);

		return result;
	}

	private String loadFromExternalAPI(String endpoint, String requestBody) throws IOException {
		HttpURLConnection conn = createConnection(endpoint, requestBody);
		return fetch(conn);
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
