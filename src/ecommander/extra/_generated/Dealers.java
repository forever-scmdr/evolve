
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Dealers
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "dealers";

    private Dealers(Item item) {
        super(item);
    }

    public static Dealers get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'dealers' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Dealers(item);
    }

    public static Dealers newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
