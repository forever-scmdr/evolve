
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Room_section
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "room_section";

    private Room_section(Item item) {
        super(item);
    }

    public static Room_section get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'room_section' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Room_section(item);
    }

    public static Room_section newChild(Item parent) {
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

    public void set_stage(String value) {
        setValue("stage", value);
    }

    public String get_stage() {
        return getStringValue("stage");
    }

    public String getDefault_stage(String defaultVal) {
        return getStringValue("stage", defaultVal);
    }

    public boolean contains_stage(String value) {
        return containsValue("stage", value);
    }

}
