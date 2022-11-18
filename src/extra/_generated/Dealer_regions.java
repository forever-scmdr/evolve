
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Dealer_regions
    extends Item
{

    public final static String _NAME = "dealer_regions";

    private Dealer_regions(Item item) {
        super(item);
    }

    public static Dealer_regions get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'dealer_regions' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Dealer_regions(item);
    }

    public static Dealer_regions newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
