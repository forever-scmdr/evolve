
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Back_catalog
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "back_catalog";

    private Back_catalog(Item item) {
        super(item);
    }

    public static Back_catalog get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'back_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Back_catalog(item);
    }

    public static Back_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
