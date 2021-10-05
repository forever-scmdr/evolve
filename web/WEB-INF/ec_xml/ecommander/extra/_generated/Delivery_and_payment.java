
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Delivery_and_payment
    extends Item
{

    public final static String _NAME = "delivery_and_payment";

    private Delivery_and_payment(Item item) {
        super(item);
    }

    public static Delivery_and_payment get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'delivery_and_payment' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Delivery_and_payment(item);
    }

    public static Delivery_and_payment newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
