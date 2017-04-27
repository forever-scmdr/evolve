package ecommander.output;

import org.apache.commons.lang3.StringUtils;

/**
 * Выводит единственный элемент с атрибутами и возможностью вложенных элементов
 *
 * @author EEEE
 *
 */
public class BranchMDWriter extends MetaDataWriter {

	private String name;
	private Object[] attributes;

	public BranchMDWriter(String name, Object... attributes) {
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

}
