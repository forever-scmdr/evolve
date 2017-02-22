
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Feedback_params
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "feedback_params";

    private Feedback_params(Item item) {
        super(item);
    }

    public static Feedback_params get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'feedback_params' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Feedback_params(item);
    }

    public static Feedback_params newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_server_from(String value) {
        setValue("server_from", value);
    }

    public String get_server_from() {
        return getStringValue("server_from");
    }

    public String getDefault_server_from(String defaultVal) {
        return getStringValue("server_from", defaultVal);
    }

    public boolean contains_server_from(String value) {
        return containsValue("server_from", value);
    }

    public void set_email_from(String value) {
        setValue("email_from", value);
    }

    public String get_email_from() {
        return getStringValue("email_from");
    }

    public String getDefault_email_from(String defaultVal) {
        return getStringValue("email_from", defaultVal);
    }

    public boolean contains_email_from(String value) {
        return containsValue("email_from", value);
    }

    public void set_email_from_login(String value) {
        setValue("email_from_login", value);
    }

    public String get_email_from_login() {
        return getStringValue("email_from_login");
    }

    public String getDefault_email_from_login(String defaultVal) {
        return getStringValue("email_from_login", defaultVal);
    }

    public boolean contains_email_from_login(String value) {
        return containsValue("email_from_login", value);
    }

    public void set_email_from_password(String value) {
        setValue("email_from_password", value);
    }

    public String get_email_from_password() {
        return getStringValue("email_from_password");
    }

    public String getDefault_email_from_password(String defaultVal) {
        return getStringValue("email_from_password", defaultVal);
    }

    public boolean contains_email_from_password(String value) {
        return containsValue("email_from_password", value);
    }

    public void set_encoding(String value) {
        setValue("encoding", value);
    }

    public String get_encoding() {
        return getStringValue("encoding");
    }

    public String getDefault_encoding(String defaultVal) {
        return getStringValue("encoding", defaultVal);
    }

    public boolean contains_encoding(String value) {
        return containsValue("encoding", value);
    }

}
