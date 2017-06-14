package ecommander.pages.output;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Domain;
/**
 * Выводит домен
 * 
	<domain name="news_tags" view-type="combobox">
		<value>Новости клуба</value>
		<value>Беларусь</value>
		<value>Международные</value>
	</domain>
 * 
 * @author EEEE
 *
 */
public class DomainMDWriter extends MetaDataWriter {

	public static final String DOMAIN_TAG = "domain";
	public static final String VALUE_TAG = "value";
	
	public static final String NAME_ATTRIBUTE = "name";
	public static final String NAME_OLD_ATTRIBUTE = "name-old";
	public static final String VIEW_TYPE = "view-type";
	
	private Domain domain;
	
	public DomainMDWriter(Domain domain) {
		super();
		this.domain = domain;
	}
	
	public XmlDocumentBuilder write(XmlDocumentBuilder xml) {
		xml.startElement(DOMAIN_TAG, NAME_ATTRIBUTE, domain.getName(), VIEW_TYPE, domain.getViewType());
		for (String value : domain.getValues()) {
			xml.startElement(VALUE_TAG).addText(value).endElement();
		}
		xml.endElement();
		return xml;
	}
	
	@Override
	public String toString() {
		return "domain: " + domain.getName();
	}
	
}