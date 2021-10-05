
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Currency_ratios
    extends Item
{

    public final static String _NAME = "currency_ratios";

    private Currency_ratios(Item item) {
        super(item);
    }

    public static Currency_ratios get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'currency_ratios' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Currency_ratios(item);
    }

    public static Currency_ratios newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
