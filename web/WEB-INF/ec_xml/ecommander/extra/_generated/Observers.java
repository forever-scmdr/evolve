
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Observers
    extends Item
{

    public final static String _NAME = "observers";

    private Observers(Item item) {
        super(item);
    }

    public static Observers get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'observers' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Observers(item);
    }

    public static Observers newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
