
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Named_text_page
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "named_text_page";

    private Named_text_page(Item item) {
        super(item);
    }

    public static Named_text_page get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'named_text_page' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Named_text_page(item);
    }

    public static Named_text_page newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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
