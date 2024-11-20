
package ecommander.extra._generated;

import java.util.List;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Subscriber
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "subscriber";

    private Subscriber(Item item) {
        super(item);
    }

    public static Subscriber get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'subscriber' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Subscriber(item);
    }

    public static Subscriber newChild(Item parent) {
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

    public void set_country(String value) {
        setValue("country", value);
    }

    public String get_country() {
        return getStringValue("country");
    }

    public String getDefault_country(String defaultVal) {
        return getStringValue("country", defaultVal);
    }

    public boolean contains_country(String value) {
        return containsValue("country", value);
    }

    public void set_city(String value) {
        setValue("city", value);
    }

    public String get_city() {
        return getStringValue("city");
    }

    public String getDefault_city(String defaultVal) {
        return getStringValue("city", defaultVal);
    }

    public boolean contains_city(String value) {
        return containsValue("city", value);
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

    public void set_firm_type(String value) {
        setValue("firm_type", value);
    }

    public String get_firm_type() {
        return getStringValue("firm_type");
    }

    public String getDefault_firm_type(String defaultVal) {
        return getStringValue("firm_type", defaultVal);
    }

    public boolean contains_firm_type(String value) {
        return containsValue("firm_type", value);
    }

    public void set_site(String value) {
        setValue("site", value);
    }

    public String get_site() {
        return getStringValue("site");
    }

    public String getDefault_site(String defaultVal) {
        return getStringValue("site", defaultVal);
    }

    public boolean contains_site(String value) {
        return containsValue("site", value);
    }

    public void add_tags(String value) {
        setValue("tags", value);
    }

    public List<String> getAll_tags() {
        return getStringValues("tags");
    }

    public void remove_tags(String value) {
        removeEqualValue("tags", value);
    }

    public boolean contains_tags(String value) {
        return containsValue("tags", value);
    }

}
