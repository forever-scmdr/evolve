
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Dealer_info
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "dealer_info";

    private Dealer_info(Item item) {
        super(item);
    }

    public static Dealer_info get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'dealer_info' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Dealer_info(item);
    }

    public static Dealer_info newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
