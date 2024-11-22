
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Discounts
    extends Item
{

    public final static String _NAME = "discounts";
    public final static String SECTION_DISCOUNT = "section_discount";

    private Discounts(Item item) {
        super(item);
    }

    public static Discounts get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'discounts' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Discounts(item);
    }

    public static Discounts newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
