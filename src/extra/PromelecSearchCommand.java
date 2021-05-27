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
		XmlDocumentBuilder xmlRequest = XmlDocumentBuilder.newDoc();
		xmlRequest.startElement("request", "login", LOGIN, "password", PZD, "customer_id", USER_ID, "method", SEARCH_BY_NAME_METHOD, "name", getVarSingleValue("query")).endElement();

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
				.startElement("cur").addText(getVarSingleValue("cur")).endElement()
				.startElement("admin").addText(getVarSingleValue("admin")).endElement()
				.startElement("request_time").addText(searchRequestTime).endElement()
				.endElement();

		addCurrencyRatios(outputPage);
		addSupplierSettings(outputPage);

		Document searchResult = Jsoup.parse(response.body().string(), "", Parser.xmlParser());
		searchResult.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		Elements el = searchResult.select("rowdata");
		if(!el.isEmpty()){
			outputPage.startElement("result");
			outputPage.addElements(el.first().html());
			outputPage.endElement();
		}
		outputPage.endElement();

		//res.setValue(response.body().string());
		res.setValue(outputPage.toString());
		return res;
	}

	private void addCurrencyRatios(XmlDocumentBuilder doc) throws Exception {
		Item currencies = ItemQuery.loadSingleItemByName(ItemNames.CURRENCIES);
		if(currencies != null){
			doc.
					startElement(ItemNames.CURRENCIES, "id", currencies.getId())
					.addElements(currencies.outputValues())
					.endElement();
		}
	}

	private void addSupplierSettings(XmlDocumentBuilder doc) throws Exception {
		Item supplierSettings = ItemQuery.loadSingleItemByParamValue(ItemNames.PRICE_CATALOG, ItemNames.price_catalog_.NAME, "promelec.ru");
		if(supplierSettings != null){
			doc.
					startElement(ItemNames.PRICE_CATALOG, "id", supplierSettings.getId())
					.addElements(supplierSettings.outputValues())
					.endElement();
		}
	}
}
