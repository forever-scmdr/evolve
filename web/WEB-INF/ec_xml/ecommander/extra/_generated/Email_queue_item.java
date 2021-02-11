
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Email_queue_item
    extends Item
{

    public final static String _NAME = "email_queue_item";
    public final static String DATE_ADDED = "date_added";
    public final static String ADDRESS_TO = "address_to";
    public final static String EMAIL_URL = "email_url";

    private Email_queue_item(Item item) {
        super(item);
    }

    public static Email_queue_item get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'email_queue_item' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Email_queue_item(item);
    }

    public static Email_queue_item newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_date_added(Long value) {
        setValue("date_added", value);
    }

    public void setUI_date_added(String value)
        throws Exception
    {
        setValueUI("date_added", value);
    }

    public Long get_date_added() {
        return getLongValue("date_added");
    }

    public Long getDefault_date_added(Long defaultVal) {
        return getLongValue("date_added", defaultVal);
    }

    public boolean contains_date_added(Long value) {
        return containsValue("date_added", value);
    }

    public void set_address_to(String value) {
        setValue("address_to", value);
    }

    public String get_address_to() {
        return getStringValue("address_to");
    }

    public String getDefault_address_to(String defaultVal) {
        return getStringValue("address_to", defaultVal);
    }

    public boolean contains_address_to(String value) {
        return containsValue("address_to", value);
    }

    public void set_email_url(String value) {
        setValue("email_url", value);
    }

    public String get_email_url() {
        return getStringValue("email_url");
    }

    public String getDefault_email_url(String defaultVal) {
        return getStringValue("email_url", defaultVal);
    }

    public boolean contains_email_url(String value) {
        return containsValue("email_url", value);
    }

}
