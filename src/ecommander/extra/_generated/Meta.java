
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Meta
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "meta";

    private Meta(Item item) {
        super(item);
    }

    public static Meta get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'meta' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Meta(item);
    }

    public static Meta newChild(Item parent) {
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

}
