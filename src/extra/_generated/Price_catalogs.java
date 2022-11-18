
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Price_catalogs
    extends Item
{

    public final static String _NAME = "price_catalogs";
    public final static String PRODUCT_CODE_EXTRA = "product_code_extra";

    private Price_catalogs(Item item) {
        super(item);
    }

    public static Price_catalogs get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'price_catalogs' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Price_catalogs(item);
    }

    public static Price_catalogs newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_product_code_extra(String value) {
        setValue("product_code_extra", value);
    }

    public String get_product_code_extra() {
        return getStringValue("product_code_extra");
    }

    public String getDefault_product_code_extra(String defaultVal) {
        return getStringValue("product_code_extra", defaultVal);
    }

    public boolean contains_product_code_extra(String value) {
        return containsValue("product_code_extra", value);
    }

}
