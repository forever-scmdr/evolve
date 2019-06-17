
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Tag_second
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "tag_second";

    private Tag_second(Item item) {
        super(item);
    }

    public static Tag_second get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'tag_second' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Tag_second(item);
    }

    public static Tag_second newChild(Item parent) {
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

    public void set_value(String value) {
        setValue("value", value);
    }

    public String get_value() {
        return getStringValue("value");
    }

    public String getDefault_value(String defaultVal) {
        return getStringValue("value", defaultVal);
    }

    public boolean contains_value(String value) {
        return containsValue("value", value);
    }

    public void set_name_value(String value) {
        setValue("name_value", value);
    }

    public String get_name_value() {
        return getStringValue("name_value");
    }

    public String getDefault_name_value(String defaultVal) {
        return getStringValue("name_value", defaultVal);
    }

    public boolean contains_name_value(String value) {
        return containsValue("name_value", value);
    }

}
