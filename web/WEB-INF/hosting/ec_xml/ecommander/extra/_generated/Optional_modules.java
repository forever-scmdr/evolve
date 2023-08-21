
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Optional_modules
    extends Item
{

    public final static String _NAME = "optional_modules";

    private Optional_modules(Item item) {
        super(item);
    }

    public static Optional_modules get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'optional_modules' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Optional_modules(item);
    }

    public static Optional_modules newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
