
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Modules
    extends Item
{

    public final static String _NAME = "modules";

    private Modules(Item item) {
        super(item);
    }

    public static Modules get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'modules' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Modules(item);
    }

    public static Modules newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
