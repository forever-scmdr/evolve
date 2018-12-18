package ecommander.fwk;

import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

/**
 * Экземпляр этого класса передается по цепочке всем методам, которые осуществляют
 * вывод XML текста. Он хранит следующую информацию
 * 1) Сам XML текст
 * 2) Текущий отступ первого символа строки
 * 3) Стек открытых тэгов и текущий открытый тэг
 * @author EEEE
 *
 */
public class XmlDocumentBuilder {
	//private static Pattern NL = Pattern.compile("\n");
	
	private static class TagDesc {
		private String tag;
		private boolean hasSubelements = false;
		private TagDesc(String tag) {
			this.tag = tag;
		}
	}
	
	private Stack<TagDesc> openTags;
	private StringBuilder xml;
	
	private XmlDocumentBuilder(StringBuilder docBase) {
		openTags = new Stack<>();
		xml = docBase;
		openTags.add(new TagDesc("root_fake"));
	}
	
	public static XmlDocumentBuilder newDoc() {
		return new XmlDocumentBuilder(new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
	}
	
	public static XmlDocumentBuilder newDocPart() {
		return new XmlDocumentBuilder(new StringBuilder());
	}
	
	public static XmlDocumentBuilder newDocFull(CharSequence document) {
		return new XmlDocumentBuilder(new StringBuilder(document));
	}
	/**
	 * добавляет начальный тэг (открывающий) и его атрибуты
	 * Атрибуты представляют собой массив с порядом следования: название 1, значение 1, название 2, значение 2, ...
	 * @param tagName
	 * @param attributes
	 */
	public final XmlDocumentBuilder startElement(String tagName, Object... attributes) {
		openTags.peek().hasSubelements = true;
		String prefix = StringUtils.rightPad("", openTags.size() - 1, '\t');
		if (xml.length() > 0) xml.append('\n');
		xml.append(prefix).append('<').append(tagName);
		if (attributes.length > 1)
			for (int i = 0; i < attributes.length; i += 2) {
				if (attributes[i] != null && attributes[i + 1] != null)
					xml.append(' ').append(attributes[i]).append("=\"").append(StringEscapeUtils.escapeXml10(attributes[i + 1].toString()))
							.append("\"");
			}
		xml.append('>');
		openTags.push(new TagDesc(tagName));
		return this;
	}
	/**
	 * Вставить дополнительные атрибуты в уже существующий последний добавленный тэг.
	 * Тэг может быть как открытым, так и закрытым.
	 * @param attributes
	 * @return
	 */
	public final XmlDocumentBuilder insertAttributes(String... attributes) {
		if (attributes.length == 0)	return this;
		int lastSymbol = xml.length() - 1;
		if (xml.charAt(lastSymbol) == '>' && attributes.length > 1) {
			xml.deleteCharAt(lastSymbol);
			boolean isClosed = xml.charAt(lastSymbol - 1) == '/';
			if (isClosed)
				xml.deleteCharAt(lastSymbol - 1); 
			for (int i = 0; i < attributes.length; i += 2) {
				xml.append(' ').append(attributes[i]).append("=\"").append(StringEscapeUtils.escapeXml10(attributes[i + 1])).append("\"");
			}
			if (isClosed)
				xml.append('/');
			xml.append('>');
			return this;
		}
		throw new RuntimeException("Bad usage of XML document builder");
	}
	/**
	 * добавляет пустой тэг и его атрибуты
	 * Атрибуты представляют собой массив с порядом следования: название 1, значение 1, название 2, значение 2, ...
	 * @param tagName
	 * @param attributes
	 */
	public final XmlDocumentBuilder addEmptyElement(String tagName, Object... attributes) {
		openTags.peek().hasSubelements = true;
		String prefix = StringUtils.rightPad("", openTags.size() - 1, '\t');
		if (xml.length() > 0) xml.append('\n');
		xml.append(prefix).append('<').append(tagName);
		for (int i = 0; i < attributes.length; i += 2) {
			xml.append(' ').append(attributes[i]).append("=\"").append(StringEscapeUtils.escapeXml10(attributes[i + 1].toString())).append("\"");
		}
		xml.append("/>");
		return this;
	}
	/**
	 * Добавить комментарий, который расположен на отдельной строке
	 * Знаки начала и конца комментария надо указвать явно
	 * @param comment
	 * @return
	 */
	public final XmlDocumentBuilder addComment(String comment) {
		if (xml.length() > 0) xml.append('\n');
		xml.append(comment);
		return this;
	}
	/**
	 * Закрывает текущий открытый элемент
	 */
	public final XmlDocumentBuilder endElement() {
		if (openTags.size() > 1) { // root fake не считается элементом
			if (openTags.peek().hasSubelements)
				xml.append(StringUtils.rightPad("\n", openTags.size() - 1, '\t'));
			xml.append("</").append(openTags.pop().tag).append('>');
		}
		return this;
	}
	/**
	 * Добавляет текст к текущему открытому элементу
	 * @param text
	 */
	public XmlDocumentBuilder addText(String text) {
		xml.append(StringEscapeUtils.escapeXml10(text));
		return this;
	}
	/**
	 * Добавляет текст к текущему открытому элементу
	 * @param text
	 */
	public XmlDocumentBuilder addText(Object text) {
		if (text != null)
			xml.append(StringEscapeUtils.escapeXml10(text.toString()));
		return this;
	}
	/**
	 * Добавляет тэги к текущему открытому элементу (кусок XML)
	 * @param tags
	 */
	public XmlDocumentBuilder addElements(CharSequence tags) {
		//xml.append('\n').append(NL_PATTERN.matcher(tags).replaceAll(StringUtils.rightPad("\n", openTags.size(), '\t')));
		if (StringUtils.isBlank(tags))
			return this;
		openTags.peek().hasSubelements = true;
		xml.append('\n').append(tags);
		return this;
	}
	
	/**
	 * Добавляет тэги к текущему открытому элементу (кусок XML)
	 * но делает это без перехода на новую строку
	 * @return
	 */
	public XmlDocumentBuilder addElementsInline(CharSequence tags) {
		if (StringUtils.isNotBlank(tags))
			xml.append(tags);
		return this;
	}

	@Override
	public String toString() {
		return xml.toString();
	}

	public StringBuilder getXmlStringSB() {
		return xml;
	}
	
	public static void main(String[] args) {
		String part = 
				"<cool>\n" +
				"	<eeee>\n" +
				"		<vvvv></vvvv>\n" +
				"	</eeee>\n" +
				"	<eeee>rrrr</eeee>\n" +
				"</cool>\n" +
				"<cool>ffff</cool>";
		XmlDocumentBuilder context = XmlDocumentBuilder.newDoc();
		context.startElement("item", "id", "1");
		context.insertAttributes("cool", "mega");
		context.startElement("item");
		context.startElement("filter", "direction", "desc");
		context.endElement();
		context.startElement("sorting");
		context.addText("dddddddffffffffffgggggggggg");
		context.endElement();
		context.startElement("cooling", "mega", "ultra");
		context.addElements(part);
		context.endElement();
		context.addEmptyElement("empty", "1", "2", "3", "4", "5", "6", "7", "8");
		context.insertAttributes("9", "10", "11", "12");
		context.endElement();
		context.endElement();
		System.out.println(context);
	}
}
