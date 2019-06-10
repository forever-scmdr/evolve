
package lunacrawler._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Manager
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "manager";

    private Manager(Item item) {
        super(item);
    }

    public static Manager get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'manager' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Manager(item);
    }

    public static Manager newChild(Item parent) {
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

    public void set_email(String value) {
        setValue("email", value);
    }

    public String get_email() {
        return getStringValue("email");
    }

    public String getDefault_email(String defaultVal) {
        return getStringValue("email", defaultVal);
    }

    public boolean contains_email(String value) {
        return containsValue("email", value);
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
