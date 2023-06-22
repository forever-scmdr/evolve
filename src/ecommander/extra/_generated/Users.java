
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Users
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "users";

    private Users(Item item) {
        super(item);
    }

    public static Users get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'users' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Users(item);
    }

    public static Users newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
