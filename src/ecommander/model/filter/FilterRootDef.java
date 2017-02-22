package ecommander.model.filter;

import ecommander.controllers.output.XmlDocumentBuilder;

public class FilterRootDef extends CriteriaGroupDef {

	static final String FILTER_ELEMENT = "filter";
	static final String ITEM_DESC_ATTRIBUTE = "item-desc";
	static final String COUNT_ATTRIBUTE = "count";
	
	private String itemName;
	private int partCount;
	
	FilterRootDef(String itemName) {
		super("", "", "AND");
		this.itemName = itemName;
	}
	
	@Override
	protected void outputStartTag(XmlDocumentBuilder doc) {
		doc.startElement(FILTER_ELEMENT, ITEM_DESC_ATTRIBUTE, itemName, ID_ATTRIBUTE, getId(), COUNT_ATTRIBUTE, partCount);
	}

	public void setPartCount(int partCount) {
		this.partCount = partCount;
	}
	
	public int getPartCount() {
		return partCount;
	}
	
	public String getItemName() {
		return itemName;
	}
}