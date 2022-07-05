package extra;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.WebClient;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * -Djsse.enableSNIExtension=false
 * -Djavax.net.debug=ssl
 */
public class TestGetUrl extends Command {
	@Override
	public ResultPE execute() throws Exception {
		String url = getVarSingleValue("url");
		//String result = WebClient.getString(url);
		try {
			Connection con = Jsoup.connect(url).timeout(5000);
			Document doc = con.get();
			if (con.response().statusCode() == 200) {
				return getResult("result").setValue(doc.outerHtml());
			}
			return getResult("result").setValue("ERROR: " + con.response().statusCode() + " " + con.response().statusMessage());
		} catch (Exception e) {
			StringWriter writer = new StringWriter();
			PrintWriter out = new PrintWriter(writer);
			e.printStackTrace(out);
			ServerLogger.error("Connection error", e);
			return getResult("result").setValue(writer.toString());
		}
	}
}
