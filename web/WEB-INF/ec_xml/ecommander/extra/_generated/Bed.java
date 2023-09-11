
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Bed
    extends Item
{

    public final static String _NAME = "bed";

    private Bed(Item item) {
        super(item);
    }

    public static Bed get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'bed' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Bed(item);
    }

    public static Bed newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
