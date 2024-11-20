
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Files
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "files";

    private Files(Item item) {
        super(item);
    }

    public static Files get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'files' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Files(item);
    }

    public static Files newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
