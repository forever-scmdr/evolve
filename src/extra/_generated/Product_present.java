
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Product_present
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "product_present";

    private Product_present(Item item) {
        super(item);
    }

    public static Product_present get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'product_present' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Product_present(item);
    }

    public static Product_present newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_present_code(String value) {
        setValue("present_code", value);
    }

    public String get_present_code() {
        return getStringValue("present_code");
    }

    public String getDefault_present_code(String defaultVal) {
        return getStringValue("present_code", defaultVal);
    }

    public boolean contains_present_code(String value) {
        return containsValue("present_code", value);
    }

    public void set_qty(String value) {
        setValue("qty", value);
    }

    public String get_qty() {
        return getStringValue("qty");
    }

    public String getDefault_qty(String defaultVal) {
        return getStringValue("qty", defaultVal);
    }

    public boolean contains_qty(String value) {
        return containsValue("qty", value);
    }

}
