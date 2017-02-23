package ecommander.model.filter;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ecommander.fwk.EcommanderException;
import ecommander.output.XmlDocumentBuilder;


/**
 * Класс, который представляет описание пользовательского фильтра
 * т.е. параметры, логические знаки, группы и т.д.
 *
 * Фильтр хранится в виде XML



// Корневая группа, представляющая собой весь фильтр
// id корневой группы представляет собой счетчик, в нем хранится наибольшее значение id элементов,
// ID присваивается элементам фильтра автоматически при их добавлении
<filter id="14" sign="AND" name="filter" comment="cool filter" item-desc="20">
	<group name="System" sign="AND" id="1" ...>
		<input type="droplist" domain="systems" caption="system name" description="name of the system" id="2">
			<domain name="system">                 // Все значения домена выводятся сразу
				<value>android</value>
				<value>windows</windows>
				<value>linux</value>
			</domain>
			<criteria sign="like" param="sys" pattern="*v*" id="3" />
		</input>
		<input type="text" caption="version" description="system version" id="4">
			<criteria sign="=" param="ver" id="5" />
		</input>
	</group>
	<input type="text" caption="keyword" id="6">
		<criteria sign="like" param="description" pattern="*v*" id="7" />
		<criteria sign="like" param="name" pattern="*v*" id="8" />
	</input>
	<group name="Price" sign="AND" id="9" ...>
		<input type="checkbox" caption="Discount" id="10">
			<criteria sign="=" param="discount" id="11"/>
		</input>
		<group name="Price direct" sign="OR" id="12" ...>
			<input type="text" caption="Sum" id="13">
				<criteria param="price" sign="<" id="14" />
				<criteria param="conditional_price" sign="<" id="15" />
			</input>
		</group>
	</group>
</filter>


TODO <enhance> Сделать заполнение фильтра значениями. При загрузке страницы если у айтема есть фильтр, то он заполняется значениями, 
которые хранятся в соответствующей переменной страницы
	

 * 
 * @author EEEE
 *
 */
public class FilterDefinition {
	static final String FILTER_ELEMENT = "filter";

	private int idCounter = 0;
	private HashMap<Integer, FilterDefPart> identifiedParts;
	private FilterRootDef rootGroup;
	/**
	 * Чтение фильтра из строки
	 * @author EEEE
	 *
	 */
	private static class FilterXMLHandler extends DefaultHandler {
		private LinkedList<FilterDefPart> elementStack = new LinkedList<FilterDefPart>();
		private FilterDefinition def;
		private static final FilterDefPart fakeElement = new FilterDefPart() {
			@Override
			protected void visit(FilterDefinitionVisitor visitor) throws EcommanderException {}
			@Override
			void outputXML(XmlDocumentBuilder doc) {}
		};
		
		private FilterXMLHandler(FilterDefinition def) {
			this.def = def;
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			FilterDefPart part = FilterDefPartXMLFactory.createPart(qName, attributes);
			if (part != null) {
				if (!elementStack.isEmpty()) {
					((FilterDefPartContainer)elementStack.getFirst()).addPart(part);
				} else {
					def.setRoot((FilterRootDef)part);
				}
				elementStack.push(part);
			} else 
				elementStack.push(fakeElement);
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			elementStack.pop();
		}
	}
	/**
	 * Создание нового чистого фильтра
	 */
	private FilterDefinition() {
		idCounter = 0;
		identifiedParts = new HashMap<Integer, FilterDefPart>();
		rootGroup = new FilterRootDef("");
	}
	/**
	 * Создание фильтра на основе строки XML (загрузка существующего фильтра)
	 * @param xml
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	public static FilterDefinition create(String xml) throws ParserConfigurationException, SAXException, IOException {
		FilterDefinition filterDef = new FilterDefinition();
		if (!StringUtils.isBlank(xml)) {
			XmlDocumentBuilder doc = XmlDocumentBuilder.newDoc();
			doc.addElements(xml);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			InputSource is = new InputSource(new StringReader(doc.toString()));
			FilterXMLHandler handler = new FilterXMLHandler(filterDef);
			parser.parse(is, handler);
		}
		return filterDef;
	}
	/**
	 * Сгенерировать XML фильтра
	 * @return
	 */
	public String generateXML() {
		rootGroup.setPartCount(idCounter);
		XmlDocumentBuilder docBuilder = XmlDocumentBuilder.newDocPart();
		rootGroup.outputXML(docBuilder);
		return docBuilder.toString();
	}
	/**
	 * Зарегистрировать часть фильтра
	 * Этот метод вызывается автоматически (не в ручную)
	 * @param part
	 * @return
	 */
	int registerPart(FilterDefPart part) {
		int partId = part.getId();
		if (partId == FilterDefPart.NO_ID)
			partId = ++idCounter;
		identifiedParts.put(partId, part);
		return partId;
	}
	/**
	 * Удалить часть фильтра
	 * Этот метод вызывается автоматически (не в ручную)
	 * @param partId
	 */
	void unregisterPart(int partId) {
		identifiedParts.remove(partId);
	}
	/**
	 * Добавить часть фильтра
	 * @param part
	 */
	public void addPart(FilterDefPart part) {
		if (rootGroup == null) {
			throw new IllegalStateException("This filter has no root yet, can not add parts");
		}
		rootGroup.addPart(part);
	}
	/**
	 * Установить корень
	 * @param root
	 */
	public void setRoot(String itemBaseName) {
		FilterRootDef root = new FilterRootDef(itemBaseName);
		setRoot(root);
	}
	
	private void setRoot(FilterRootDef root) {
		rootGroup = root;
		rootGroup.register(null, this);
	}
	/**
	 * Пустой ли фильтр
	 * @return
	 */
	public boolean isEmpty() {
		return StringUtils.isBlank(rootGroup.getItemName());
	}
	/**
	 * Получить часть фильтра по ее ID
	 * @param id
	 * @return
	 */
	public FilterDefPart getPart(int id) {
		return identifiedParts.get(id);
	}
	/**
	 * Итерация по всем элементам фильтра для выполнения действий с ними с помощью визитора
	 * @param visitor
	 * @throws EcommanderException 
	 */
	public void iterate(FilterDefinitionVisitor visitor) throws EcommanderException {
		rootGroup.visit(visitor);
	}
	/**
	 * Получить ID айтема, на базе которого сделан фильтр (айтемы, которые этот фильтр фильтрует)
	 * @return
	 */
	public String getBaseItemName() {
		return rootGroup.getItemName();
	}
}