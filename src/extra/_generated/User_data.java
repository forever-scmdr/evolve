
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class User_data
    extends Item
{

    public final static String _NAME = "user_data";

    private User_data(Item item) {
        super(item);
    }

    public static User_data get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'user_data' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new User_data(item);
    }

    public static User_data newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
