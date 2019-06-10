
package lunacrawler._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class My_price_form
    extends Item
{

    public final static String _NAME = "my_price_form";
    public final static String NAME = "name";
    public final static String PHONE = "phone";
    public final static String EMAIL = "email";
    public final static String PRODUCT_CODE = "product_code";
    public final static String PRODUCT_NAME = "product_name";
    public final static String PRICE = "price";
    public final static String MESSAGE = "message";

    private My_price_form(Item item) {
        super(item);
    }

    public static My_price_form get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'my_price_form' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new My_price_form(item);
    }

    public static My_price_form newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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

    public void set_product_code(String value) {
        setValue("product_code", value);
    }

    public String get_product_code() {
        return getStringValue("product_code");
    }

    public String getDefault_product_code(String defaultVal) {
        return getStringValue("product_code", defaultVal);
    }

    public boolean contains_product_code(String value) {
        return containsValue("product_code", value);
    }

    public void set_product_name(String value) {
        setValue("product_name", value);
    }

    public String get_product_name() {
        return getStringValue("product_name");
    }

    public String getDefault_product_name(String defaultVal) {
        return getStringValue("product_name", defaultVal);
    }

    public boolean contains_product_name(String value) {
        return containsValue("product_name", value);
    }

    public void set_price(String value) {
        setValue("price", value);
    }

    public String get_price() {
        return getStringValue("price");
    }

    public String getDefault_price(String defaultVal) {
        return getStringValue("price", defaultVal);
    }

    public boolean contains_price(String value) {
        return containsValue("price", value);
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

}
