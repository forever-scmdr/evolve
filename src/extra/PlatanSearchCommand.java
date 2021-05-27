package extra;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.net.URLEncoder;

public class PlatanSearchCommand extends Command {

	private static final String URL_VAR = "platan_feed_url";
	private static final String QUERY = "q";

	@Override
	public ResultPE execute() throws Exception {
		String url = getVarSingleValue(URL_VAR);
		String query = getVarSingleValue(QUERY);

		Document doc = Jsoup.parse(new URL(url + "?search=" + URLEncoder.encode(query, "UTF-8")), 5000);

		doc.outputSettings().outline(true);
		doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		doc.outputSettings().indentAmount(4);
		doc.outputSettings().prettyPrint(false);

		String result = doc.toString().replaceAll(">\\s+",">").replaceAll("\\s+<","<");
		result = result.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
		return getResult("result").setValue(JsoupXmlFixer.fix(result));
	}
}