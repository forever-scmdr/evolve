
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class User
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "user";

    private User(Item item) {
        super(item);
    }

    public static User get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'user' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new User(item);
    }

    public static User newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_num(String value) {
        setValue("num", value);
    }

    public String get_num() {
        return getStringValue("num");
    }

    public String getDefault_num(String defaultVal) {
        return getStringValue("num", defaultVal);
    }

    public boolean contains_num(String value) {
        return containsValue("num", value);
    }

    public void set_organization(String value) {
        setValue("organization", value);
    }

    public String get_organization() {
        return getStringValue("organization");
    }

    public String getDefault_organization(String defaultVal) {
        return getStringValue("organization", defaultVal);
    }

    public boolean contains_organization(String value) {
        return containsValue("organization", value);
    }

    public void set_contact(String value) {
        setValue("contact", value);
    }

    public String get_contact() {
        return getStringValue("contact");
    }

    public String getDefault_contact(String defaultVal) {
        return getStringValue("contact", defaultVal);
    }

    public boolean contains_contact(String value) {
        return containsValue("contact", value);
    }

    public void set_position(String value) {
        setValue("position", value);
    }

    public String get_position() {
        return getStringValue("position");
    }

    public String getDefault_position(String defaultVal) {
        return getStringValue("position", defaultVal);
    }

    public boolean contains_position(String value) {
        return containsValue("position", value);
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
