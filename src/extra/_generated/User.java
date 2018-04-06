
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class User
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "user";

    private User(Item item) {
        super(item);
    }

    public static User get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'user' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new User(item);
    }

    public static User newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_login(String value) {
        setValue("login", value);
    }

    public String get_login() {
        return getStringValue("login");
    }

    public String getDefault_login(String defaultVal) {
        return getStringValue("login", defaultVal);
    }

    public boolean contains_login(String value) {
        return containsValue("login", value);
    }

    public void set_password(String value) {
        setValue("password", value);
    }

    public String get_password() {
        return getStringValue("password");
    }

    public String getDefault_password(String defaultVal) {
        return getStringValue("password", defaultVal);
    }

    public boolean contains_password(String value) {
        return containsValue("password", value);
    }

}
