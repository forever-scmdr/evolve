package ecommander.fwk;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Парсер XML на основе Streaming API
 */
public class XmlDataSource {

	public static class Node {
		private String tagName;
		private Node parent;
		private XmlDocumentBuilder doc;

		private Node currentChild = null;


		private Node(String tagName, Node parent) {
			this.tagName = tagName;
			this.doc = XmlDocumentBuilder.newDocPart();
			if (parent != null) {
				this.parent = parent;
				parent.currentChild = this;
			}
		}

		public String getTagName() {
			return tagName;
		}

		public Document getDoc() {
			return Jsoup.parse(doc.toString(), "", Parser.xmlParser());
		}

		private Node getLastFound() {
			Node lastFound = this;
			while (lastFound.currentChild != null)
				lastFound = lastFound.currentChild;
			return lastFound;
		}

	}

	private Node path;
	private XMLStreamReader reader;
	private String tagContent;

	public XmlDataSource(String fileName, Charset charset) throws XMLStreamException, IOException {
		Reader fileReader = Files.newBufferedReader(Paths.get(fileName), charset);
		XMLInputFactory factory = XMLInputFactory.newInstance();
		reader = factory.createXMLStreamReader(fileReader);
	}

	/**
	 * Найти первый следующий тэг с заданным названием.
	 * При этом становится известно только название тэга и значения атрибутов.
	 * Для заполнения вложенных тэгов надо вызывать метод nodeStarted
	 *
	 * @param elementName - тэг
	 * @param attrCriterias - критерии атрибутов
	 * @return
	 * @throws XMLStreamException
	 */
	public Node findNextNode(String elementName, String... attrCriterias) throws XMLStreamException {
		while (reader.hasNext()) {
			int event = reader.next();

			switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					Node node = nodeStarted();
					if (StringUtils.equalsIgnoreCase(node.tagName, elementName)) {
                        boolean matches = true;
						if (attrCriterias != null && attrCriterias.length > 0) {
							HashMap<String, String> nodeAttrs = new HashMap<>();
							for (int i = 0; i < reader.getAttributeCount(); i++) {
								nodeAttrs.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
							}
							for (int i = 0; i < attrCriterias.length; i += 2) {
								matches &= nodeAttrs.containsKey(attrCriterias[i]);
								if (attrCriterias.length > i + 1)
									matches &= StringUtils.equals(nodeAttrs.get(attrCriterias[i]), attrCriterias[i + 1]);

							}
						}
						if (matches)
						    return node;
					}
					break;

				case XMLStreamConstants.CHARACTERS:
					tagContent = reader.getText().trim();
					break;

				case XMLStreamConstants.END_ELEMENT:
				case XMLStreamConstants.END_DOCUMENT:
					break;
			}

		}
		return null;
	}

	private Node nodeStarted() {
		Node parent = path == null ? null : path.getLastFound();
		Node newNode = new Node(reader.getLocalName(), parent);
		if (path == null) {
			path = newNode;
		}
		ArrayList<String> attrs = new ArrayList<>(6);
		for (int i = 0; i < reader.getAttributeCount(); i++) {
			attrs.add(reader.getAttributeLocalName(i));
			attrs.add(reader.getAttributeValue(i));
		}
		newNode.doc.startElement(reader.getLocalName(), attrs.toArray(new String[0]));
		return newNode;
	}


	public Node scanCurrentNode() throws XMLStreamException {
		Node currentNode = path.getLastFound();
		int levelCounter = 0;
		while (reader.hasNext()) {
			int event = reader.next();

			switch (event) {

				case XMLStreamConstants.START_ELEMENT:
					ArrayList<String> attrs = new ArrayList<>(6);
					for (int i = 0; i < reader.getAttributeCount(); i++) {
						attrs.add(reader.getAttributeLocalName(i));
						attrs.add(reader.getAttributeValue(i));
					}
					currentNode.doc.startElement(reader.getLocalName(), attrs.toArray(new String[0]));
					levelCounter++;
					break;

				case XMLStreamConstants.CHARACTERS:
					tagContent = reader.getText().trim();
					break;

				case XMLStreamConstants.END_ELEMENT:
					if (StringUtils.isNotBlank(tagContent)) {
						currentNode.doc.addText(tagContent);
					}
					currentNode.doc.endElement();
					tagContent = null;
					levelCounter--;
					if (levelCounter < 0) {
						if (currentNode.parent != null)
							currentNode.parent.currentChild = null;
						return currentNode;
					}
					break;

				case XMLStreamConstants.END_DOCUMENT:
					break;
			}

		}
		return currentNode;
	}

	public void finishDocument() throws XMLStreamException {
		while (reader.hasNext()) {
			if (reader.next() == XMLStreamConstants.END_DOCUMENT)
				return;
		}
		reader.close();
	}
}
