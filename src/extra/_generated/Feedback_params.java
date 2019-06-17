
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Feedback_params
    extends Item
{

    public final static String _NAME = "feedback_params";
    public final static String SERVER_FROM = "server_from";
    public final static String EMAIL_FROM = "email_from";
    public final static String EMAIL_FROM_PASSWORD = "email_from_password";

    private Feedback_params(Item item) {
        super(item);
    }

    public static Feedback_params get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'feedback_params' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Feedback_params(item);
    }

    public static Feedback_params newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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

}
