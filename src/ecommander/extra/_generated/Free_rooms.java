
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Free_rooms
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "free_rooms";

    private Free_rooms(Item item) {
        super(item);
    }

    public static Free_rooms get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'free_rooms' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Free_rooms(item);
    }

    public static Free_rooms newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
