package ecommander.fwk;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;

import java.nio.charset.Charset;

/**
 * Методы для работы с XML документами, разобранными с помощью Jsoup
 * Created by E on 15/2/2018.
 */
public class JsoupUtils {
	public static String nodeText(Element element, String tag) {
		return element.getElementsByTag(tag).first().ownText();
	}

	public static String nodeHtml(Element element, String tag) {
		return element.getElementsByTag(tag).first().html();
	}

	public static String outputDoc(Document jsoupDoc) {
		Document.OutputSettings settings = new Document.OutputSettings();
		settings.charset(Charset.forName("UTF-8"));
		settings.syntax(Document.OutputSettings.Syntax.xml);
		settings.escapeMode(Entities.EscapeMode.xhtml);
		jsoupDoc.outputSettings(settings);
		return jsoupDoc.body().outerHtml();
	}
}
