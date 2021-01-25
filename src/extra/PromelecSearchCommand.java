package extra;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import okhttp3.*;

import java.util.concurrent.TimeUnit;

public class PromelecSearchCommand extends Command {
	private static final String LOGIN = "AlphaChip";
	private static final String PZD = "67304a79eec95cb8a694de4f911d0c96";
	private static int USER_ID = 112717;
	private static final String SEARCH_BY_NAME_METHOD = "items_data_find";
	private static final String TEST_URL = "http://base2.promelec.ru:222/rpc_test";
	private static final String PRODUCTION_URL = "http://base2.promelec.ru:221/rpc";
	private static final String TEST_VAR = "test_mode";
	private OkHttpClient client;

	@Override
	public ResultPE execute() throws Exception {
		String url = "true".equals(getVarSingleValue(TEST_VAR)) ? TEST_URL : PRODUCTION_URL;
		XmlDocumentBuilder doc = XmlDocumentBuilder.newDoc();
		doc.startElement("request", "login", LOGIN, "password", PZD, "customer_id", USER_ID, "method", SEARCH_BY_NAME_METHOD, "name", getVarSingleValue("query")).endElement();

		//initialize OkHttpClient
		client = (client == null)
				? new OkHttpClient()
				.newBuilder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.writeTimeout(10, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.build()
				: client;

		MediaType mediaType = MediaType.parse("application/xml");

		RequestBody requestBody = RequestBody.create(mediaType, doc.toString());
		Request request = new Request.Builder().url(url).post(requestBody).addHeader("accept", "application/xml").build();
		long reqStart = System.currentTimeMillis();
		Response response = client.newCall(request).execute();
		long searchRequestTime = System.currentTimeMillis() - reqStart;

		ResultPE res = getResult("complete");
		res.setValue(response.body().string());

		return res;
	}
}
