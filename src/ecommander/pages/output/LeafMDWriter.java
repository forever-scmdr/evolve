package ecommander.pages.output;

import ecommander.fwk.XmlDocumentBuilder;
import org.apache.commons.lang3.StringUtils;
/**
 * Выводит одну строку вида
 * 
 * <name>value</name>
 * 
 * Например
 * 
 * <edit_link>delete_item.admin?id=100</edit_link>
 * 
 * @author EEEE
 *
 */
public class LeafMDWriter extends MetaDataWriter {

	private String value;
	private String name;
	private Object[] attributes;
	
	public LeafMDWriter(String name, Object value, Object... attributes) {
		this.name = name;
		if (value != null)
			this.value = value.toString();
		this.attributes = attributes;
	}
	
	@Override
	public XmlDocumentBuilder write(XmlDocumentBuilder xml) {
		if (StringUtils.isBlank(value))
			xml.addEmptyElement(name, attributes);
		else
			xml.startElement(name, attributes).addText(value).endElement();
		return xml;
	}

	@Override
	public String toString() {
		return name + " : " + value;
	}

}
