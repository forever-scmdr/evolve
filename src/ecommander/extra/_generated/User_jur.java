
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class User_jur
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "user_jur";

    private User_jur(Item item) {
        super(item);
    }

    public static User_jur get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'user_jur' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new User_jur(item);
    }

    public static User_jur newChild(Item parent) {
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

    public void set_contact_name(String value) {
        setValue("contact_name", value);
    }

    public String get_contact_name() {
        return getStringValue("contact_name");
    }

    public String getDefault_contact_name(String defaultVal) {
        return getStringValue("contact_name", defaultVal);
    }

    public boolean contains_contact_name(String value) {
        return containsValue("contact_name", value);
    }

    public void set_contact_phone(String value) {
        setValue("contact_phone", value);
    }

    public String get_contact_phone() {
        return getStringValue("contact_phone");
    }

    public String getDefault_contact_phone(String defaultVal) {
        return getStringValue("contact_phone", defaultVal);
    }

    public boolean contains_contact_phone(String value) {
        return containsValue("contact_phone", value);
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

    public void set_pay_type(String value) {
        setValue("pay_type", value);
    }

    public String get_pay_type() {
        return getStringValue("pay_type");
    }

    public String getDefault_pay_type(String defaultVal) {
        return getStringValue("pay_type", defaultVal);
    }

    public boolean contains_pay_type(String value) {
        return containsValue("pay_type", value);
    }

    public void set_no_account(String value) {
        setValue("no_account", value);
    }

    public String get_no_account() {
        return getStringValue("no_account");
    }

    public String getDefault_no_account(String defaultVal) {
        return getStringValue("no_account", defaultVal);
    }

    public boolean contains_no_account(String value) {
        return containsValue("no_account", value);
    }

    public void set_account(String value) {
        setValue("account", value);
    }

    public String get_account() {
        return getStringValue("account");
    }

    public String getDefault_account(String defaultVal) {
        return getStringValue("account", defaultVal);
    }

    public boolean contains_account(String value) {
        return containsValue("account", value);
    }

    public void set_bank(String value) {
        setValue("bank", value);
    }

    public String get_bank() {
        return getStringValue("bank");
    }

    public String getDefault_bank(String defaultVal) {
        return getStringValue("bank", defaultVal);
    }

    public boolean contains_bank(String value) {
        return containsValue("bank", value);
    }

    public void set_bank_address(String value) {
        setValue("bank_address", value);
    }

    public String get_bank_address() {
        return getStringValue("bank_address");
    }

    public String getDefault_bank_address(String defaultVal) {
        return getStringValue("bank_address", defaultVal);
    }

    public boolean contains_bank_address(String value) {
        return containsValue("bank_address", value);
    }

    public void set_bank_code(String value) {
        setValue("bank_code", value);
    }

    public String get_bank_code() {
        return getStringValue("bank_code");
    }

    public String getDefault_bank_code(String defaultVal) {
        return getStringValue("bank_code", defaultVal);
    }

    public boolean contains_bank_code(String value) {
        return containsValue("bank_code", value);
    }

    public void set_unp(String value) {
        setValue("unp", value);
    }

    public String get_unp() {
        return getStringValue("unp");
    }

    public String getDefault_unp(String defaultVal) {
        return getStringValue("unp", defaultVal);
    }

    public boolean contains_unp(String value) {
        return containsValue("unp", value);
    }

    public void set_director(String value) {
        setValue("director", value);
    }

    public String get_director() {
        return getStringValue("director");
    }

    public String getDefault_director(String defaultVal) {
        return getStringValue("director", defaultVal);
    }

    public boolean contains_director(String value) {
        return containsValue("director", value);
    }

    public void set_base(String value) {
        setValue("base", value);
    }

    public String get_base() {
        return getStringValue("base");
    }

    public String getDefault_base(String defaultVal) {
        return getStringValue("base", defaultVal);
    }

    public boolean contains_base(String value) {
        return containsValue("base", value);
    }

    public void set_base_number(String value) {
        setValue("base_number", value);
    }

    public String get_base_number() {
        return getStringValue("base_number");
    }

    public String getDefault_base_number(String defaultVal) {
        return getStringValue("base_number", defaultVal);
    }

    public boolean contains_base_number(String value) {
        return containsValue("base_number", value);
    }

    public void set_base_date(String value) {
        setValue("base_date", value);
    }

    public String get_base_date() {
        return getStringValue("base_date");
    }

    public String getDefault_base_date(String defaultVal) {
        return getStringValue("base_date", defaultVal);
    }

    public boolean contains_base_date(String value) {
        return containsValue("base_date", value);
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
