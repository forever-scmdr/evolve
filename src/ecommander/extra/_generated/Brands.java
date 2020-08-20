
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Brands
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "brands";

    private Brands(Item item) {
        super(item);
    }

    public static Brands get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'brands' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Brands(item);
    }

    public static Brands newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
