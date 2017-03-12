package ecommander.output;

import ecommander.model.DataModelXmlElementNames;
import ecommander.model.ItemTypeContainer;
import ecommander.model.ItemTypeRegistry;

/**
 * Выводит определение корня айтемов
 * 
	<root>
		<child name="catalog" quantifier="single" virtual="false"/>
		<child name="news" quantifier="single" virtual="false"/>
	</itemdesc>
 * 
 * @author EEEE
 *
 */
public class RootItemMDWriter extends MetaDataWriter implements DataModelXmlElementNames {

	public RootItemMDWriter() {
		super();
	}
	
	@Override
	public XmlDocumentBuilder write(XmlDocumentBuilder xml) {
		xml.startElement(ROOT);
		for (ItemTypeContainer.ChildDesc child : ItemTypeRegistry.getDefaultRoot().getAllChildren()) {
			xml.addEmptyElement(CHILD,
					ITEM, child.itemName,
					VIRTUAL, child.isVirtual,
					SINGLE, child.isSingle);
		}
		for (MetaDataWriter part : additional) {
			part.write(xml);
		}
		xml.endElement();
		return xml;
	}

}
