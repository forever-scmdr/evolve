
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Extra
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "extra";

    private Extra(Item item) {
        super(item);
    }

    public static Extra get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'extra' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Extra(item);
    }

    public static Extra newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
