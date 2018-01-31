
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Orders
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "orders";

    private Orders(Item item) {
        super(item);
    }

    public static Orders get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'orders' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Orders(item);
    }

    public static Orders newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
