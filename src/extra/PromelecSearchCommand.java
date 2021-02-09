package extra;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.concurrent.TimeUnit;

public class PromelecSearchCommand extends Command {
	private static final String LOGIN = "ChipElectronics2";
	private static final String PZD = "c2533275a76716f296d9632744e13407";
	private static int USER_ID = 70880;
	private static final String SEARCH_BY_NAME_METHOD = "items_data_find";
	private static final String TEST_URL = "http://base2.promelec.ru:222/rpc_test";
	private static final String PRODUCTION_URL = "http://base2.promelec.ru:221/rpc";
	private static final String TEST_VAR = "test_mode";
	private OkHttpClient client;

	@Override
	public ResultPE execute() throws Exception {
		String url = "true".equals(getVarSingleValue(TEST_VAR)) ? TEST_URL : PRODUCTION_URL;
		XmlDocumentBuilder xmlRequest = XmlDocumentBuilder.newDoc();
		xmlRequest.startElement("request", "login", LOGIN, "password", PZD, "customer_id", USER_ID, "method", SEARCH_BY_NAME_METHOD, "name", getVarSingleValue("query")).endElement();
		//xmlRequest.startElement("request", "login", LOGIN, "password", PZD, "method", SEARCH_BY_NAME_METHOD, "name", getVarSingleValue("query")).endElement();

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


		ResultPE res = getResult("complete");

		XmlDocumentBuilder outputPage = XmlDocumentBuilder.newDoc();
		outputPage.startElement("page", "name", "promelec_search");
		outputPage.startElement("base").addText(getUrlBase()).endElement();
		outputPage.startElement("variables")
				.startElement("query").addText(getVarSingleValue("query")).endElement()
				.startElement("view").addText(getVarSingleValue("view")).endElement()
				.startElement("currency").addText(getVarSingleValue("currency")).endElement()
				.startElement("minqty").addText(getVarSingleValue("minqty")).endElement()
				.startElement("request_time").addText(searchRequestTime).endElement()
				.endElement();

		addCurrencyRatios(outputPage);
		//String re = response.body().string();
		Document searchResult = Jsoup.parse(response.body().string(), "", Parser.xmlParser());
		Elements el = searchResult.select("rowdata");
		if(!el.isEmpty()){
			outputPage.startElement("result");
			outputPage.addElements(el.first().html());
			outputPage.endElement();
		}
		outputPage.endElement();

		//res.setValue(re);
		res.setValue(outputPage.toString());
		return res;
	}

	private void addCurrencyRatios(XmlDocumentBuilder doc) throws Exception {
		Item currencies = ItemQuery.loadSingleItemByName(ItemNames.CATALOG);
		if(currencies != null){
			doc.
					startElement(ItemNames.CATALOG, "id", currencies.getId())
					.addElements(currencies.outputValues())
					.endElement();
		}
	}

}
