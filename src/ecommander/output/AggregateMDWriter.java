package ecommander.output;

/**
 * Выводит сложную стркутуру со вложенными компонентами
 * 
 * @author EEEE
 *
 */
public class AggregateMDWriter extends MetaDataWriter {

	private String name;
	private Object[] attributes;
	
	public AggregateMDWriter(String name, Object... attributes) {
		super();
		this.name = name;
		this.attributes = attributes;
	}

	@Override
	public XmlDocumentBuilder write(XmlDocumentBuilder xml) {
		xml.startElement(name, attributes);
		writeAdditional(xml);
		xml.endElement();
		return xml;
	}

	@Override
	public String toString() {
		return "aggregate: " + name;
	}

}
