
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Shop_catalog
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "shop_catalog";

    private Shop_catalog(Item item) {
        super(item);
    }

    public static Shop_catalog get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'shop_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Shop_catalog(item);
    }

    public static Shop_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
