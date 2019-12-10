
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Price_catalogs
    extends Item
{

    public final static String _NAME = "price_catalogs";

    private Price_catalogs(Item item) {
        super(item);
    }

    public static Price_catalogs get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'price_catalogs' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Price_catalogs(item);
    }

    public static Price_catalogs newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
