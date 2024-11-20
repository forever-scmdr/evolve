
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Email_queue
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "email_queue";

    private Email_queue(Item item) {
        super(item);
    }

    public static Email_queue get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'email_queue' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Email_queue(item);
    }

    public static Email_queue newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
