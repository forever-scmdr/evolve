
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Email_queue
    extends Item
{

    public final static String _NAME = "email_queue";

    private Email_queue(Item item) {
        super(item);
    }

    public static Email_queue get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'email_queue' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Email_queue(item);
    }

    public static Email_queue newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
