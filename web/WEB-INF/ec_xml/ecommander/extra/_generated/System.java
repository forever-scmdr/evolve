
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class System
    extends Item
{

    public final static String _NAME = "system";

    private System(Item item) {
        super(item);
    }

    public static System get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'system' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new System(item);
    }

    public static System newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
