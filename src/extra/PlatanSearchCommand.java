package extra;

import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;
import java.net.URLEncoder;

public class PlatanSearchCommand extends Command {

	private static final String URL_VAR = "platan_feed_url";
	private static final String QUERY = "query";

	@Override
	public ResultPE execute() throws Exception {
		String url = getVarSingleValue(URL_VAR);
		String query = getVarSingleValue(QUERY);

		Document doc = Jsoup.parse(new URL(url + "?search=" + URLEncoder.encode(getVarSingleValue("query"), "UTF-8").replaceAll("\\+", "%20")), 5000);
		Item catalog = ItemQuery.loadSingleItemByName(ItemNames.CATALOG);
		Element root = doc.child(0);
		root.prepend("<currency_ratio>"+catalog.getDoubleValue("currency_ratio", 100d)+"</currency_ratio>");
		root.prepend("<q1>"+catalog.getDoubleValue("q1", 0d)+"</q1>");
		root.prepend("<q2>"+catalog.getDoubleValue("q2", 0d)+"</q2>");
		root.prepend("<currency>"+getVarSingleValue("currency").trim()+"</currency>");
		root.prepend("<view>"+getVarSingleValue("view").trim()+"</view>");
		root.prependElement("base").text(getUrlBase());

		doc.outputSettings().outline(true);
		doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		doc.outputSettings().indentAmount(4);
		doc.outputSettings().prettyPrint(false);

		String result = doc.toString().replaceAll(">\\s+",">").replaceAll("\\s+<","<");
		return getResult("result").setValue(result);
	}
}
