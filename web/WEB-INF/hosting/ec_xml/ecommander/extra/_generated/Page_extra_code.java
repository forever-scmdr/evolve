
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Page_extra_code
    extends Item
{

    public final static String _NAME = "page_extra_code";
    public final static String NAME = "name";
    public final static String SPOILER = "spoiler";
    public final static String TEXT = "text";

    private Page_extra_code(Item item) {
        super(item);
    }

    public static Page_extra_code get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'page_extra_code' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Page_extra_code(item);
    }

    public static Page_extra_code newChild(Item parent) {
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

    public void set_text(String value) {
        setValue("text", value);
    }

    public String get_text() {
        return getStringValue("text");
    }

    public String getDefault_text(String defaultVal) {
        return getStringValue("text", defaultVal);
    }

    public boolean contains_text(String value) {
        return containsValue("text", value);
    }

}
