
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class All_emails
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "all_emails";

    private All_emails(Item item) {
        super(item);
    }

    public static All_emails get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'all_emails' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new All_emails(item);
    }

    public static All_emails newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
