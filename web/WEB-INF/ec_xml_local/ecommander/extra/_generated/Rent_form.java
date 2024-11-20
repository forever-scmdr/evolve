
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Rent_form
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "rent_form";

    private Rent_form(Item item) {
        super(item);
    }

    public static Rent_form get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'rent_form' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Rent_form(item);
    }

    public static Rent_form newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_size(String value) {
        setValue("size", value);
    }

    public String get_size() {
        return getStringValue("size");
    }

    public String getDefault_size(String defaultVal) {
        return getStringValue("size", defaultVal);
    }

    public boolean contains_size(String value) {
        return containsValue("size", value);
    }

    public void set_assort(String value) {
        setValue("assort", value);
    }

    public String get_assort() {
        return getStringValue("assort");
    }

    public String getDefault_assort(String defaultVal) {
        return getStringValue("assort", defaultVal);
    }

    public boolean contains_assort(String value) {
        return containsValue("assort", value);
    }

    public void set_brands(String value) {
        setValue("brands", value);
    }

    public String get_brands() {
        return getStringValue("brands");
    }

    public String getDefault_brands(String defaultVal) {
        return getStringValue("brands", defaultVal);
    }

    public boolean contains_brands(String value) {
        return containsValue("brands", value);
    }

    public void set_tc(String value) {
        setValue("tc", value);
    }

    public String get_tc() {
        return getStringValue("tc");
    }

    public String getDefault_tc(String defaultVal) {
        return getStringValue("tc", defaultVal);
    }

    public boolean contains_tc(String value) {
        return containsValue("tc", value);
    }

    public void set_message(String value) {
        setValue("message", value);
    }

    public String get_message() {
        return getStringValue("message");
    }

    public String getDefault_message(String defaultVal) {
        return getStringValue("message", defaultVal);
    }

    public boolean contains_message(String value) {
        return containsValue("message", value);
    }

    public void set_spam(String value) {
        setValue("spam", value);
    }

    public String get_spam() {
        return getStringValue("spam");
    }

    public String getDefault_spam(String defaultVal) {
        return getStringValue("spam", defaultVal);
    }

    public boolean contains_spam(String value) {
        return containsValue("spam", value);
    }

}
