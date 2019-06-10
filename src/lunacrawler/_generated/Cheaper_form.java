
package lunacrawler._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Cheaper_form
    extends Item
{

    public final static String _NAME = "cheaper_form";
    public final static String PRODUCT = "product";
    public final static String NAME = "name";
    public final static String PHONE = "phone";
    public final static String URL = "url";
    public final static String URL_CHECK = "url_check";

    private Cheaper_form(Item item) {
        super(item);
    }

    public static Cheaper_form get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'cheaper_form' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Cheaper_form(item);
    }

    public static Cheaper_form newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_product(String value) {
        setValue("product", value);
    }

    public String get_product() {
        return getStringValue("product");
    }

    public String getDefault_product(String defaultVal) {
        return getStringValue("product", defaultVal);
    }

    public boolean contains_product(String value) {
        return containsValue("product", value);
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

    public void set_url_check(String value) {
        setValue("url_check", value);
    }

    public String get_url_check() {
        return getStringValue("url_check");
    }

    public String getDefault_url_check(String defaultVal) {
        return getStringValue("url_check", defaultVal);
    }

    public boolean contains_url_check(String value) {
        return containsValue("url_check", value);
    }

}
