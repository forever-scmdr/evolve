
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Currency
    extends Item
{

    public final static String _NAME = "currency";

    private Currency(Item item) {
        super(item);
    }

    public static Currency get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'currency' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Currency(item);
    }

    public static Currency newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
