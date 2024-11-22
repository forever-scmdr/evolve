
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Warranty_form
    extends Item
{

    public final static String _NAME = "warranty_form";
    public final static String CODE = "code";
    public final static String SERIAL = "serial";
    public final static String DATE = "date";
    public final static String SELLER = "seller";
    public final static String OWNER = "owner";
    public final static String EMAIL = "email";
    public final static String PHONE = "phone";
    public final static String URL = "url";

    private Warranty_form(Item item) {
        super(item);
    }

    public static Warranty_form get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'warranty_form' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Warranty_form(item);
    }

    public static Warranty_form newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_code(String value) {
        setValue("code", value);
    }

    public String get_code() {
        return getStringValue("code");
    }

    public String getDefault_code(String defaultVal) {
        return getStringValue("code", defaultVal);
    }

    public boolean contains_code(String value) {
        return containsValue("code", value);
    }

    public void set_serial(String value) {
        setValue("serial", value);
    }

    public String get_serial() {
        return getStringValue("serial");
    }

    public String getDefault_serial(String defaultVal) {
        return getStringValue("serial", defaultVal);
    }

    public boolean contains_serial(String value) {
        return containsValue("serial", value);
    }

    public void set_date(Long value) {
        setValue("date", value);
    }

    public void setUI_date(String value)
        throws Exception
    {
        setValueUI("date", value);
    }

    public Long get_date() {
        return getLongValue("date");
    }

    public Long getDefault_date(Long defaultVal) {
        return getLongValue("date", defaultVal);
    }

    public boolean contains_date(Long value) {
        return containsValue("date", value);
    }

    public void set_seller(String value) {
        setValue("seller", value);
    }

    public String get_seller() {
        return getStringValue("seller");
    }

    public String getDefault_seller(String defaultVal) {
        return getStringValue("seller", defaultVal);
    }

    public boolean contains_seller(String value) {
        return containsValue("seller", value);
    }

    public void set_owner(String value) {
        setValue("owner", value);
    }

    public String get_owner() {
        return getStringValue("owner");
    }

    public String getDefault_owner(String defaultVal) {
        return getStringValue("owner", defaultVal);
    }

    public boolean contains_owner(String value) {
        return containsValue("owner", value);
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

    public void set_url(String value) {
        setValue("url", value);
    }

    public String get_url() {
        return getStringValue("url");
    }

    public String getDefault_url(String defaultVal) {
        return getStringValue("url", defaultVal);
    }

    public boolean contains_url(String value) {
        return containsValue("url", value);
    }

}
