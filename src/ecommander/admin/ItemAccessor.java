package ecommander.admin;

import ecommander.model.Assoc;
import ecommander.output.MetaDataWriter;
import ecommander.output.XmlDocumentBuilder;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
/**
 * Упрощенный айтем без параметров для упрощенного представления в админской части
 * Он также может выводиться в виде XML в следующем формате
 * 
 * Есть атрибуты id и ref-id. У айтемов, которые являются ссылками, эти атрибуты различаются (у обычных айтемов совпадают)
	<item type-name="section" type-caption="Раздел" type-id="10" id="33" ref-id="33" caption="Телевизоры" inline="false">
		<edit_link>admin.admin?command=admin_set_item&id=33?itemName</edit_link> // Ссылка для выбора айтема для редактирования
	</item>
			
 *
 * Note: this class has a natural ordering that is inconsistent with equals.
 * 
 * @author EEEE
 *
 */
public class ItemAccessor extends MetaDataWriter implements Comparable<ItemAccessor> {
	
	private long itemId;
	private String key;
	private int childWeight;
	private int itemType;
	private String typeName;
	private String typeCaption;
	private byte contextAssoc;
	private String assocName;
	private String assocCaption;
	private boolean isParentCompatible;
	private boolean inline = false;
	
	public ItemAccessor(int itemType, long itemId, String key, int childWeight, byte contextAssoc, boolean isParentCompatible) {
		this.itemId = itemId;
		this.key = key;
		this.childWeight = childWeight;
		this.itemType = itemType;
		this.contextAssoc = contextAssoc;
		this.isParentCompatible = isParentCompatible;
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemType);
		Assoc assoc = ItemTypeRegistry.getAssoc(contextAssoc);
		if (itemDesc != null) {
			this.typeName = itemDesc.getName();
			this.typeCaption = itemDesc.getCaption();
			this.inline = itemDesc.isInline();
		} else {
			this.typeName = "root";
			this.typeCaption = "Корень";
		}
		if (assoc != null) {
			assocName = assoc.getName();
			assocCaption = assoc.getCaption();
		}
	}

	public String getItemName() {
		return typeName;
	}
	
	public int getTypeId() {
		return itemType;
	}

	public long getItemId() {
		return itemId;
	}
	
	public boolean isParentCompatible() {
		return isParentCompatible;
	}

	public int compareTo(ItemAccessor o) {
		return childWeight - o.childWeight;
	}

	@Override
	public boolean equals(Object obj) {
		return itemId == ((ItemAccessor) obj).itemId;
	}

	@Override
	public XmlDocumentBuilder write(XmlDocumentBuilder xml) {
		xml.startElement(AdminXML.ITEM_ELEMENT, 
				AdminXML.TYPE_NAME_ATTRIBUTE, typeName, 
				AdminXML.TYPE_ID_ATTRIBUTE, itemType, 
				AdminXML.TYPE_CAPTION_ATTRIBUTE, typeCaption,
				AdminXML.ASSOC_NAME_ATTRIBUTE, assocName,
				AdminXML.ASSOC_CAPTION_ATTRIBUTE, assocCaption,
				AdminXML.TYPE_INLINE_ATTRIBUTE, inline, 
				AdminXML.ID_ATTRIBUTE, itemId,
				AdminXML.CAPTION_ATTRIBUTE, key,
				AdminXML.WEIGHT_ATTRIBUTE, childWeight,
				AdminXML.COMPATIBLE_ATTRIBUTE, isParentCompatible);
		writeAdditional(xml);
		xml.endElement();
		return xml;
	}
	
	public void setParentCompatible(boolean compatible) {
		this.isParentCompatible = compatible;
	}
}
