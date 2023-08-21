
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Page_extra
    extends Item
{

    public final static String _NAME = "page_extra";
    public final static String NAME = "name";
    public final static String SPOILER = "spoiler";

    private Page_extra(Item item) {
        super(item);
    }

    public static Page_extra get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'page_extra' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Page_extra(item);
    }

    public static Page_extra newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_name(String value) {
        setValue("name", value);
    }

    public String get_name() {
        return getStringValue("name");
    }

    public String getDefault_name(String defaultVal) {
        return getStringValue("name", defaultVal);
    }

    public boolean contains_name(String value) {
        return containsValue("name", value);
    }

    public void set_spoiler(Byte value) {
        setValue("spoiler", value);
    }

    public void setUI_spoiler(String value)
        throws Exception
    {
        setValueUI("spoiler", value);
    }

    public Byte get_spoiler() {
        return getByteValue("spoiler");
    }

    public Byte getDefault_spoiler(Byte defaultVal) {
        return getByteValue("spoiler", defaultVal);
    }

    public boolean contains_spoiler(Byte value) {
        return containsValue("spoiler", value);
    }

}
