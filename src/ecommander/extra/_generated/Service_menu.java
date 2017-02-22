
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Service_menu
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "service_menu";

    private Service_menu(Item item) {
        super(item);
    }

    public static Service_menu get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'service_menu' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Service_menu(item);
    }

    public static Service_menu newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
