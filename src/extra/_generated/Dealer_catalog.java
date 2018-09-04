
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Dealer_catalog
    extends Item
{

    public final static String _NAME = "dealer_catalog";

    private Dealer_catalog(Item item) {
        super(item);
    }

    public static Dealer_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'dealer_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Dealer_catalog(item);
    }

    public static Dealer_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
