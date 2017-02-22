
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Service_menu_item
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "service_menu_item";

    private Service_menu_item(Item item) {
        super(item);
    }

    public static Service_menu_item get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'service_menu_item' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Service_menu_item(item);
    }

    public static Service_menu_item newChild(Item parent) {
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

}
