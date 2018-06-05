
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Customers
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "customers";

    private Customers(Item item) {
        super(item);
    }

    public static Customers get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'customers' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Customers(item);
    }

    public static Customers newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
