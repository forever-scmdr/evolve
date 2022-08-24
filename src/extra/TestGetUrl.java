package extra;

import ecommander.fwk.OkWebClient;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.WebClient;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;
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
			//Connection con = Jsoup.connect(url).timeout(5000);
			//Document doc = con.get();
			String html = OkWebClient.getInstance().getString(url);
			if (StringUtils.isNotBlank(html)) {
				return getResult("result").setValue(html);
			}
			return getResult("result").setValue("ERROR empty");
		} catch (Exception e) {
			StringWriter writer = new StringWriter();
			PrintWriter out = new PrintWriter(writer);
			e.printStackTrace(out);
			ServerLogger.error("Connection error", e);
			return getResult("result").setValue(writer.toString());
		}
	}
}
