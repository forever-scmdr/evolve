package ecommander.fwk;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.nio.charset.Charset;

/**
 * Методы для работы с XML документами, разобранными с помощью Jsoup
 * Created by E on 15/2/2018.
 */
public class JsoupUtils {
	public static String nodeText(Element element, String tag) {
		Elements nodes = element.getElementsByTag(tag);
		if (nodes.isEmpty())
			return null;
		return nodes.first().ownText();
	}

	public static String nodeHtml(Element element, String tag) {
		Elements nodes = element.getElementsByTag(tag);
		if (nodes.isEmpty())
			return null;
		return nodes.first().html();
	}

	public static String outputHtmlDoc(Document jsoupDoc) {
		setSettings(jsoupDoc);
		return jsoupDoc.outerHtml();
	}

	public static String outputXmlDoc(Document jsoupDoc) {
		setSettings(jsoupDoc);
		return jsoupDoc.outerHtml();
	}

	private static void setSettings(Document jsoupDoc) {
		Document.OutputSettings settings = new Document.OutputSettings();
		settings.charset(Charset.forName("UTF-8"));
		settings.syntax(Document.OutputSettings.Syntax.xml);
		settings.escapeMode(Entities.EscapeMode.xhtml);
		jsoupDoc.outputSettings(settings);
	}

	public static String prepareValidXml(String unknownXml) {
		Document doc = Jsoup.parse(unknownXml, "", Parser.xmlParser());
		return JsoupUtils.outputXmlDoc(doc);
	}

	public static Document parseXml(String unknownXml) {
		return Jsoup.parse(unknownXml, "", Parser.xmlParser());
	}

	/**
	 * Получить значение тэга. Подразумевается что такой тэг один.
	 * Берется значение первого найденного тэга
	 * @param parent
	 * @param tag
	 * @return
	 */
	public static String getTagFirstValue(Element parent, String tag) {
		if (parent == null)
			return null;
		Element el = parent.getElementsByTag(tag).first();
		if (el == null)
			return null;
		return el.ownText();
	}

	/**
	 * Получить значение атрибута тэга. Подразумевается что такой тэг один.
	 * Берется значение атрибута первого найденного тэга
	 * @param node
	 * @param tag
	 * @param attr
	 * @return
	 */
	public static String getTagAttr(Element node, String tag, String attr) {
		if (node == null)
			return null;
		Element el = node.getElementsByTag(tag).first();
		if (el == null)
			return null;
		return el.attr(attr);
	}
}
