
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Customer
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "customer";

    private Customer(Item item) {
        super(item);
    }

    public static Customer get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'customer' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Customer(item);
    }

    public static Customer newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_user_id(Long value) {
        setValue("user_id", value);
    }

    public void setUI_user_id(String value)
        throws Exception
    {
        setValueUI("user_id", value);
    }

    public Long get_user_id() {
        return getLongValue("user_id");
    }

    public Long getDefault_user_id(Long defaultVal) {
        return getLongValue("user_id", defaultVal);
    }

    public boolean contains_user_id(Long value) {
        return containsValue("user_id", value);
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
