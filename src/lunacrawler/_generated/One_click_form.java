
package lunacrawler._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class One_click_form
    extends Item
{

    public final static String _NAME = "one_click_form";
    public final static String PHONE = "phone";
    public final static String PRODUCT_CODE = "product_code";
    public final static String PRODUCT_NAME = "product_name";

    private One_click_form(Item item) {
        super(item);
    }

    public static One_click_form get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'one_click_form' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new One_click_form(item);
    }

    public static One_click_form newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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

}
