package ecommander.controllers.admin;

import ecommander.controllers.output.MetaDataWriter;
import ecommander.controllers.output.XmlDocumentBuilder;
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
	private long itemRefId;
	private String key;
	private boolean mount;
	private int childWeight;
	private int itemType;
	private String typeName;
	private String typeCaption;
	private boolean inline = false;
	
	public ItemAccessor(int itemType, long itemId, long itemRefId, String key, int childWeight) {
		this(itemType, itemId, itemRefId, key, childWeight, false);
	}

	public ItemAccessor(int itemType, long itemId, long itemRefId, String key, int childWeight, boolean mount) {
		this.itemId = itemId;
		this.itemRefId = itemRefId;
		this.key = key;
		this.mount = mount;
		this.childWeight = childWeight;
		this.itemType = itemType;
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemType);
		if (itemDesc != null) {
			this.typeName = itemDesc.getName();
			this.typeCaption = itemDesc.getCaption();
			this.inline = itemDesc.isInline();
		} else {
			this.typeName = "root";
			this.typeCaption = "Корень";
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
	
	public boolean isMountableAndMoveable() {
		return mount;
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
				AdminXML.TYPE_INLINE_ATTRIBUTE, inline, 
				AdminXML.ID_ATTRIBUTE, itemId,
				AdminXML.REF_ID_ATTRIBUTE, itemRefId,
				AdminXML.CAPTION_ATTRIBUTE, key, 
				AdminXML.WEIGHT_ATTRIBUTE, childWeight,
				AdminXML.COMPATIBLE_ATTRIBUTE, mount);
		writeAdditional(xml);
		xml.endElement();
		return xml;
	}
	
	public void setMountableAndMoveable(boolean mountable) {
		this.mount = mountable;
	}
}
