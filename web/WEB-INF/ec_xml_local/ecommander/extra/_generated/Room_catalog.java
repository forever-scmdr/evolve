
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Room_catalog
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "room_catalog";

    private Room_catalog(Item item) {
        super(item);
    }

    public static Room_catalog get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'room_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Room_catalog(item);
    }

    public static Room_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
