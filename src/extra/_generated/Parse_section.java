
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Parse_section
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "parse_section";

    private Parse_section(Item item) {
        super(item);
    }

    public static Parse_section get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'parse_section' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Parse_section(item);
    }

    public static Parse_section newChild(Item parent) {
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

    public void set_item_urls(String value) {
        setValue("item_urls", value);
    }

    public String get_item_urls() {
        return getStringValue("item_urls");
    }

    public String getDefault_item_urls(String defaultVal) {
        return getStringValue("item_urls", defaultVal);
    }

    public boolean contains_item_urls(String value) {
        return containsValue("item_urls", value);
    }

}
