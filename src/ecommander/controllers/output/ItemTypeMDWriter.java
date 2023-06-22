package ecommander.controllers.output;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.ServerLogger;
import ecommander.model.item.ItemType;
/**
 * Выводит определение айтема
 * 
	<itemdesc 
			name="section" id="22" virtual="true" user="false" key="name" 
			caption="Раздел каталога" description="Раздел каталога продукции" extends="searchable,page_content" inline="true"
			extendable="true" key-unique="true">
		<subitem name="section" quantifier="multiple" virtual="false"/>
		<subitem name="product" quantifier="multiple" virtual="false"/>
	</itemdesc>

	возможно использование old-name
	
	<itemdesc name="section" name-old="razdel" ...>
		...
	</itemdesc>

 * 
 * @author EEEE
 *
 */
public class ItemTypeMDWriter extends MetaDataWriter {

	public static final String ITEMDESC_TAG = "itemdesc";
	public static final String SUBITEM_TAG = "subitem";
	
	public static final String NAME_ATTRIBUTE = "name";
	public static final String NAME_OLD_ATTRIBUTE = "name-old";
	public static final String ID_ATTRIBUTE = "id";
	public static final String VIRTUAL_ATTRIBUTE = "virtual";
	public static final String USER_DEF_ATTRIBUTE = "user-def";
	public static final String KEY_ATTRIBUTE = "key";
	public static final String CAPTION_ATTRIBUTE = "caption";
	public static final String DESCRIPTION_ATTRIBUTE = "description";
	public static final String QUANTIFIER_ATTRIBUTE = "quantifier";
	public static final String EXTENDS_ATTRIBUTE = "extends";
	public static final String INLINE_ATTRIBUTE = "inline";
	public static final String EXTENDABLE_ATTRIBUTE = "extendable";
	public static final String KEY_UNIQUE_ATTRIBUTE = "key-unique";
	
	private ItemType itemType;
	private String mainTag;
	private String oldName;
	
	public ItemTypeMDWriter(ItemType itemDesc) {
		super();
		this.itemType = itemDesc;
		mainTag = ITEMDESC_TAG;
	}
	
	public ItemTypeMDWriter(ItemType itemDesc, String tag) {
		super();
		this.itemType = itemDesc;
		this.mainTag = tag;
	}
	
	public XmlDocumentBuilder write(XmlDocumentBuilder xml) {
		List<String> attrs = new ArrayList<String>(Arrays.asList(
				NAME_ATTRIBUTE, itemType.getName(), 
				ID_ATTRIBUTE, itemType.getTypeId() + "", 
				VIRTUAL_ATTRIBUTE, itemType.isVirtual() + "", 
				USER_DEF_ATTRIBUTE, itemType.isUserDefined() + "",
				INLINE_ATTRIBUTE, itemType.isInline() + "",
				KEY_ATTRIBUTE, itemType.getKey(),
				CAPTION_ATTRIBUTE, itemType.getCaption(),
				DESCRIPTION_ATTRIBUTE, itemType.getDescription()
		));
		if (itemType.hasPredecessors()) {
			attrs.add(EXTENDS_ATTRIBUTE);
			attrs.add(itemType.getExtendsString());
		} else {
			ServerLogger.debug(itemType.getName());
		}
		if (!StringUtils.isBlank(oldName)) {
			attrs.add(NAME_OLD_ATTRIBUTE);
			attrs.add(oldName);
		}
		if (itemType.isExtendable()) {
			attrs.add(EXTENDABLE_ATTRIBUTE);
			attrs.add(Boolean.TRUE.toString());
		}
		if (itemType.isKeyUnique()) {
			attrs.add(KEY_UNIQUE_ATTRIBUTE);
			attrs.add(Boolean.TRUE.toString());
		}
		xml.startElement(mainTag, attrs.toArray(new Object[0]));
		for (String subitem : itemType.getAllSubitemNames()) {
			if (itemType.isSubitemOwn(subitem)) {
				String quantifier = "single";
				if (itemType.isSubitemMultiple(subitem))
					quantifier = "multiple";
				xml.addEmptyElement(SUBITEM_TAG, 
						NAME_ATTRIBUTE, subitem, 
						QUANTIFIER_ATTRIBUTE, quantifier, 
						VIRTUAL_ATTRIBUTE, itemType.isSubitemVirtual(subitem));
			}
		}
		for (MetaDataWriter part : additional) {
			part.write(xml);
		}
		xml.endElement();
		return xml;
	}
	/**
	 * Добавить старое название (нужно для изменения названия айтема)
	 * @param nameOld
	 */
	public void setNameOld(String nameOld) {
		this.oldName = nameOld;
	}
	
	@Override
	public String toString() {
		return "item: " + itemType.getName();
	}
	
}