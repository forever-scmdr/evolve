
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Info
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "info";

    private Info(Item item) {
        super(item);
    }

    public static Info get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'info' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Info(item);
    }

    public static Info newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
