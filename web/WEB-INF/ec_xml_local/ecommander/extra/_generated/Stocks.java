
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Stocks
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "stocks";

    private Stocks(Item item) {
        super(item);
    }

    public static Stocks get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'stocks' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Stocks(item);
    }

    public static Stocks newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
