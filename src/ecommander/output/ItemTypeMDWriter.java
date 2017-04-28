package ecommander.output;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ecommander.model.DataModelXmlElementNames;
import ecommander.model.ItemTypeContainer;

import ecommander.model.ItemType;
/**
 * Выводит определение айтема
 * 
	<itemdesc 
			name="section" ag-id="22" ag-hash-"1234545345" virtual="true" user="false" key="name"
			caption="Раздел каталога" description="Раздел каталога продукции" extends="searchable page_content"
			extendable="true" key-unique="true">
		<child assoc="hierarchy" name="section" quantifier="multiple" virtual="false"/>
		<child assoc="hierarchy" name="product" quantifier="multiple" virtual="false"/>
	</itemdesc>
 *
 * @author EEEE
 *
 */
public class ItemTypeMDWriter extends MetaDataWriter implements DataModelXmlElementNames {


	private ItemType itemType;
	private String mainTag;

	public ItemTypeMDWriter(ItemType itemDesc) {
		super();
		this.itemType = itemDesc;
		mainTag = ITEMDESC;
	}
	
	public ItemTypeMDWriter(ItemType itemDesc, String tag) {
		super();
		this.itemType = itemDesc;
		this.mainTag = tag;
	}
	
	public XmlDocumentBuilder write(XmlDocumentBuilder xml) {
		List<String> attrs = new ArrayList<String>(Arrays.asList(
				NAME, itemType.getName(),
				CAPTION, itemType.getCaption(),
				KEY, itemType.getKey(),
				AG_ID, itemType.getTypeId() + "",
				AG_HASH, itemType.getName().hashCode() + "",
				VIRTUAL, itemType.isVirtual() + "",
				USER_DEF, itemType.isUserDefined() + "",
				DESCRIPTION, itemType.getDescription()
		));
		if (itemType.hasPredecessors()) {
			attrs.add(SUPER);
			attrs.add(itemType.getExtendsString());
		}
		if (itemType.isExtendable()) {
			attrs.add(EXTENDABLE);
			attrs.add(Boolean.TRUE.toString());
		}
		if (itemType.isKeyUnique()) {
			attrs.add(KEY_UNIQUE);
			attrs.add(Boolean.TRUE.toString());
		}
		xml.startElement(mainTag, attrs.toArray(new Object[0]));
		for (ItemTypeContainer.ChildDesc child : itemType.getAllChildren()) {
			if (child.isOwn) {
				xml.addEmptyElement(CHILD,
						ASSOC, child.assocName,
						ITEM, child.itemName,
						VIRTUAL, child.isVirtual,
						SINGLE, child.isSingle);
			}
		}
		writeSubwriters(xml);
		xml.endElement();
		return xml;
	}

	@Override
	public String toString() {
		return "item: " + itemType.getName();
	}
	
}