
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Orders
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "orders";

    private Orders(Item item) {
        super(item);
    }

    public static Orders get(Item item) {
        if (item == null) {
            return null;
        }
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
