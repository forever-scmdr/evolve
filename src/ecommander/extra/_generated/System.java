
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class System
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "system";

    private System(Item item) {
        super(item);
    }

    public static System get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'system' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new System(item);
    }

    public static System newChild(Item parent) {
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

    public void set_num(String value) {
        setValue("num", value);
    }

    public String get_num() {
        return getStringValue("num");
    }

    public String getDefault_num(String defaultVal) {
        return getStringValue("num", defaultVal);
    }

    public boolean contains_num(String value) {
        return containsValue("num", value);
    }

    public void set_formula(String value) {
        setValue("formula", value);
    }

    public String get_formula() {
        return getStringValue("formula");
    }

    public String getDefault_formula(String defaultVal) {
        return getStringValue("formula", defaultVal);
    }

    public boolean contains_formula(String value) {
        return containsValue("formula", value);
    }

}
