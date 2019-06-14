package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.integration.CatalogConst;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.xml.sax.HandlerBase;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.net.URLEncoder;

/**
 * Created by user on 14.06.2019.
 */
public class GetInfoFromTools extends IntegrateBase  implements CatalogConst {

	private static final String URL = "http://www.tools.by/tools_yml.php?unp=800014103";
	private Content content;

	@Override
	protected boolean makePreparations() throws Exception {
		String url = getVarSingleValue("url")+"?"+getVarSingleValue("name") +"="+ URLEncoder.encode(getVarSingleValue("query"), "UTF-8");
		content = Request.Get(url).execute().returnContent();
		return content != null;
	}

	@Override
	protected void integrate() throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		parser.parse(content.asStream(), new ToolsYMLHandler());
	}

	@Override
	protected void terminate() throws Exception {

	}

	private class ToolsYMLHandler extends HandlerBase {
	}
}
