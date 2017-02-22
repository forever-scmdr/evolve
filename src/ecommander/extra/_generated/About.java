
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class About
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "about";

    private About(Item item) {
        super(item);
    }

    public static About get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'about' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new About(item);
    }

    public static About newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
