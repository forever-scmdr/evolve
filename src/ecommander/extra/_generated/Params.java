
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Params
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "params";

    private Params(Item item) {
        super(item);
    }

    public static Params get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'params' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Params(item);
    }

    public static Params newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
