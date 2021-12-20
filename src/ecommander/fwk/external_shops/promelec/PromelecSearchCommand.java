package ecommander.fwk.external_shops.promelec;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import extra.JsoupXmlFixer;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.concurrent.TimeUnit;

public class PromelecSearchCommand extends Command {
	private static final String LOGIN = "ChipElectronics2";
	private static final String PZD = "c2533275a76716f296d9632744e13407";
	private static final int USER_ID = 70880;
	private static final String SEARCH_BY_NAME_METHOD = "items_data_find";
	private static final String TEST_URL = "http://base2.promelec.ru:222/rpc_test";
	private static final String PRODUCTION_URL = "http://base2.promelec.ru:221/rpc";
	private static final String TEST_VAR = "test_mode";
	private OkHttpClient client;

	@Override
	public ResultPE execute() throws Exception {
		String url = "true".equals(getVarSingleValue(TEST_VAR)) ? TEST_URL : PRODUCTION_URL;
		XmlDocumentBuilder xmlRequest = XmlDocumentBuilder.newDoc();
		xmlRequest.startElement("request", "login", LOGIN, "password", PZD, "customer_id", USER_ID, "method", SEARCH_BY_NAME_METHOD, "name", getVarSingleValue("q")).endElement();

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

		RequestBody requestBody = RequestBody.create(mediaType, xmlRequest.toString());
		Request request = new Request.Builder().url(url).post(requestBody).addHeader("accept", "application/xml").build();
		long reqStart = System.currentTimeMillis();
		Response response = client.newCall(request).execute();
		long searchRequestTime = System.currentTimeMillis() - reqStart;

		setPageVariable("elapsed_time", String.valueOf(searchRequestTime));
		ResultPE res = getResult("result");

		if(response.body() != null){
			Document searchResult = Jsoup.parse(response.body().string(), "", Parser.xmlParser());
			searchResult.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
			Elements el = searchResult.select("rowdata");
			res.setValue(JsoupXmlFixer.fix(el.first().html()));
		}
		return res;
	}
}