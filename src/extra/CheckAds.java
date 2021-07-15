package extra;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.utils.ExceptionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;


public class CheckAds extends Command {
	@Override
	public ResultPE execute() throws Exception {
		XmlDocumentBuilder doc = XmlDocumentBuilder.newDoc();
		doc.startElement("root");
		String url  = getVarSingleValue("url").toString();
		try {
			Document ads = Jsoup.parse(new URL(url), 1000);
			Element body = ads.body();
			boolean ok = StringUtils.isNotBlank(body.html());
			doc.addElement("result", ok);
		}catch (Exception e){
			ServerLogger.error(e);
			String stackTrace = ExceptionUtils.getStackTrace(e);
			doc.addElement("exception", stackTrace);
		}
		doc.endElement();
		ResultPE res = getResult("result");
		res.setValue(doc.toString());
		return res;
	}
}
