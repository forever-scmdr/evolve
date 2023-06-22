package ecommander.controllers.output;

import ecommander.model.item.RootItemType;
/**
 * Выводит определение корня айтемов
 * 
	<root group="common" persistence="database">
		<subitem name="catalog" quantifier="single" virtual="false"/>
		<subitem name="news" quantifier="single" virtual="false"/>
	</itemdesc>
 * 
 * @author EEEE
 *
 */
public class RootItemMDWriter extends MetaDataWriter {

	public static final String ROOT_TAG = "root";
	public static final String SUBITEM_TAG = "subitem";
	
	public static final String GROUP_ATTRIBUTE = "group";
	
	public static final String NAME_ATTRIBUTE = "name";
	public static final String ID_ATTRIBUTE = "id";
	public static final String VIRTUAL_ATTRIBUTE = "virtual";
	public static final String QUANTIFIER_ATTRIBUTE = "quantifier";
	
	private RootItemType root;
	
	public RootItemMDWriter(RootItemType root) {
		super();
		this.root = root;
	}
	
	@Override
	public XmlDocumentBuilder write(XmlDocumentBuilder xml) {
		xml.startElement(ROOT_TAG, GROUP_ATTRIBUTE, root.getGroup());
		for (String subitem : root.getAllSubitemNames()) {
			if (root.isSubitemOwn(subitem)) {
				String quantifier = "single";
				if (root.isSubitemMultiple(subitem))
					quantifier = "multiple";
				xml.addEmptyElement(SUBITEM_TAG, 
						NAME_ATTRIBUTE, subitem, 
						QUANTIFIER_ATTRIBUTE, quantifier, 
						VIRTUAL_ATTRIBUTE, root.isSubitemVirtual(subitem));
			}
		}
		for (MetaDataWriter part : additional) {
			part.write(xml);
		}
		xml.endElement();
		return xml;
	}

}
