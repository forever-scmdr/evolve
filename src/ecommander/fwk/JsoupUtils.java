package ecommander.fwk;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

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
		return StringUtils.normalizeSpace(el.ownText());
	}

	/**
	 * Получить значение тэга, заданного селектором. Подразумевается что такой тэг один.
	 * Берется значение первого найденного тэга
	 * @param parent
	 * @param selector
	 * @return
	 */
	public static String getSelectorFirstValue(Element parent, String selector) {
		if (parent == null)
			return null;
		Element el = parent.select(selector).first();
		if (el == null)
			return null;
		return StringUtils.normalizeSpace(el.text());
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

	/**
	 * Преобразовать JSON в XML и потом спарсить его
	 * @param json
	 * @param rootElementName
	 * @return
	 */
	public static Document parseJsonAsXml(String json, String rootElementName) {
		return parseXml(transformJsonToXml(json, rootElementName));
	}

	/**
	 * Преобразовать JSON документ в XML документ с заданным корневым элементом
	 * @param json
	 * @param rootElementName
	 * @param replacements
	 * @return
	 */
	public static String transformJsonToXml(String json, String rootElementName, HashMap<String, String>... replacements) {
		HashMap<String, String> rep;
		if (replacements == null || replacements.length == 0)
			rep = new HashMap<>();
		else
			rep = replacements[0];
		JsonElement element = JsonParser.parseString(json);
		XmlDocumentBuilder xml = XmlDocumentBuilder.newDoc();
		processElement(element, xml, rootElementName, rep);
		return xml.toString();
	}


	private static void processElement(JsonElement element, XmlDocumentBuilder xml, String rootElement, HashMap<String, String> replacements) {
		String tagToCreate = replacements.getOrDefault(rootElement, rootElement);
		if (element.isJsonArray()) {
			JsonArray array = element.getAsJsonArray();
			for (JsonElement arrayItem : array) {
				processElement(arrayItem, xml, rootElement, replacements);
			}
		} else if (element.isJsonObject()) {
			JsonObject object = element.getAsJsonObject();
			Map<String, JsonElement> map = object.asMap();
			xml.startElement(tagToCreate);
			for (String key : map.keySet()) {
				processElement(map.get(key), xml, key, replacements);
			}
			xml.endElement();
		} else if (element.isJsonPrimitive()) {
			xml.addElement(tagToCreate, element.getAsString());
		} else if (element.isJsonNull()) {
			xml.addElement(tagToCreate, "");
		}
	}
}
