
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class User_group
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "user_group";

    private User_group(Item item) {
        super(item);
    }

    public static User_group get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'user_group' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new User_group(item);
    }

    public static User_group newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_name(String value) {
        setValue("name", value);
    }

    public String get_name() {
        return getStringValue("name");
    }

    public String getDefault_name(String defaultVal) {
        return getStringValue("name", defaultVal);
    }

    public boolean contains_name(String value) {
        return containsValue("name", value);
    }

    public void set_user_group_name(String value) {
        setValue("user_group_name", value);
    }

    public String get_user_group_name() {
        return getStringValue("user_group_name");
    }

    public String getDefault_user_group_name(String defaultVal) {
        return getStringValue("user_group_name", defaultVal);
    }

    public boolean contains_user_group_name(String value) {
        return containsValue("user_group_name", value);
    }

}
