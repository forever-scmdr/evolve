
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Contacts
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "contacts";

    private Contacts(Item item) {
        super(item);
    }

    public static Contacts get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'contacts' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Contacts(item);
    }

    public static Contacts newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_first_col(String value) {
        setValue("first_col", value);
    }

    public String get_first_col() {
        return getStringValue("first_col");
    }

    public String getDefault_first_col(String defaultVal) {
        return getStringValue("first_col", defaultVal);
    }

    public boolean contains_first_col(String value) {
        return containsValue("first_col", value);
    }

    public void set_second_col(String value) {
        setValue("second_col", value);
    }

    public String get_second_col() {
        return getStringValue("second_col");
    }

    public String getDefault_second_col(String defaultVal) {
        return getStringValue("second_col", defaultVal);
    }

    public boolean contains_second_col(String value) {
        return containsValue("second_col", value);
    }

    public void set_map(String value) {
        setValue("map", value);
    }

    public String get_map() {
        return getStringValue("map");
    }

    public String getDefault_map(String defaultVal) {
        return getStringValue("map", defaultVal);
    }

    public boolean contains_map(String value) {
        return containsValue("map", value);
    }

}
