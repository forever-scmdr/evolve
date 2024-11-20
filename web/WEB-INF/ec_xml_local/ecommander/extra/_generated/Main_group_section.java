
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Main_group_section
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "main_group_section";

    private Main_group_section(Item item) {
        super(item);
    }

    public static Main_group_section get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'main_group_section' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Main_group_section(item);
    }

    public static Main_group_section newChild(Item parent) {
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

    public void set_number(String value) {
        setValue("number", value);
    }

    public String get_number() {
        return getStringValue("number");
    }

    public String getDefault_number(String defaultVal) {
        return getStringValue("number", defaultVal);
    }

    public boolean contains_number(String value) {
        return containsValue("number", value);
    }

}
