package ecommander.pages.output;


import ecommander.fwk.XmlDocumentBuilder;

public class LeafMDStringWriter extends MetaDataWriter {

	private String value;
	
	public LeafMDStringWriter(String value) {
		super();
		this.value = value;
	}

	@Override
	public XmlDocumentBuilder write(XmlDocumentBuilder xml) {
		xml.addElements(value);
		return xml;
	}

}
