
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class External_shops
    extends Item
{

    public final static String _NAME = "external_shops";

    private External_shops(Item item) {
        super(item);
    }

    public static External_shops get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'external_shops' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new External_shops(item);
    }

    public static External_shops newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
