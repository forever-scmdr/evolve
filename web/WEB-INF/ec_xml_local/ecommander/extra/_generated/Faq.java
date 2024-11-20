
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Faq
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "faq";

    private Faq(Item item) {
        super(item);
    }

    public static Faq get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'faq' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Faq(item);
    }

    public static Faq newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
