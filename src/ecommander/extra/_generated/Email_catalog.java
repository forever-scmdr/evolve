
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Email_catalog
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "email_catalog";

    private Email_catalog(Item item) {
        super(item);
    }

    public static Email_catalog get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'email_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Email_catalog(item);
    }

    public static Email_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
