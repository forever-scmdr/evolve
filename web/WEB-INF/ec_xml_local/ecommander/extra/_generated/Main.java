
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Main
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "main";

    private Main(Item item) {
        super(item);
    }

    public static Main get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'main' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Main(item);
    }

    public static Main newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
