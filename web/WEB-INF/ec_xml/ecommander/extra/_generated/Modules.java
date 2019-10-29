
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Modules
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "modules";

    private Modules(Item item) {
        super(item);
    }

    public static Modules get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'modules' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Modules(item);
    }

    public static Modules newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
