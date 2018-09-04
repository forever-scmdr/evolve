
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Sale_catalog
    extends Item
{

    public final static String _NAME = "sale_catalog";

    private Sale_catalog(Item item) {
        super(item);
    }

    public static Sale_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'sale_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Sale_catalog(item);
    }

    public static Sale_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
