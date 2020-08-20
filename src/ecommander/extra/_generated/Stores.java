
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Stores
    extends Item
{

    public final static String _NAME = "stores";

    private Stores(Item item) {
        super(item);
    }

    public static Stores get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'stores' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Stores(item);
    }

    public static Stores newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
