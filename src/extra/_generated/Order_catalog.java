
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Order_catalog
    extends Item
{

    public final static String _NAME = "order_catalog";

    private Order_catalog(Item item) {
        super(item);
    }

    public static Order_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'order_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Order_catalog(item);
    }

    public static Order_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
