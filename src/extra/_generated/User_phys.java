
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class User_phys
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "user_phys";

    private User_phys(Item item) {
        super(item);
    }

    public static User_phys get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'user_phys' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new User_phys(item);
    }

    public static User_phys newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_phone(String value) {
        setValue("phone", value);
    }

    public String get_phone() {
        return getStringValue("phone");
    }

    public String getDefault_phone(String defaultVal) {
        return getStringValue("phone", defaultVal);
    }

    public boolean contains_phone(String value) {
        return containsValue("phone", value);
    }

    public void set_address(String value) {
        setValue("address", value);
    }

    public String get_address() {
        return getStringValue("address");
    }

    public String getDefault_address(String defaultVal) {
        return getStringValue("address", defaultVal);
    }

    public boolean contains_address(String value) {
        return containsValue("address", value);
    }

    public void set_ship_type(String value) {
        setValue("ship_type", value);
    }

    public String get_ship_type() {
        return getStringValue("ship_type");
    }

    public String getDefault_ship_type(String defaultVal) {
        return getStringValue("ship_type", defaultVal);
    }

    public boolean contains_ship_type(String value) {
        return containsValue("ship_type", value);
    }

    public void set_comment(String value) {
        setValue("comment", value);
    }

    public String get_comment() {
        return getStringValue("comment");
    }

    public String getDefault_comment(String defaultVal) {
        return getStringValue("comment", defaultVal);
    }

    public boolean contains_comment(String value) {
        return containsValue("comment", value);
    }

}
