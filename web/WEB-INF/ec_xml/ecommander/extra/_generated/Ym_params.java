
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Ym_params
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "ym_params";

    private Ym_params(Item item) {
        super(item);
    }

    public static Ym_params get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'ym_params' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Ym_params(item);
    }

    public static Ym_params newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
