
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Defer_form
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "defer_form";

    private Defer_form(Item item) {
        super(item);
    }

    public static Defer_form get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'defer_form' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Defer_form(item);
    }

    public static Defer_form newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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
