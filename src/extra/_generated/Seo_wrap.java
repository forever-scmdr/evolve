
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Seo_wrap
    extends Item
{

    public final static String _NAME = "seo_wrap";
    public final static String MAIN_HOST = "main_host";
    public final static String PRODUCT_PREFIX = "product_prefix";
    public final static String PRODUCT_SUFIX = "product_sufix";

    private Seo_wrap(Item item) {
        super(item);
    }

    public static Seo_wrap get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'seo_wrap' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Seo_wrap(item);
    }

    public static Seo_wrap newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_main_host(String value) {
        setValue("main_host", value);
    }

    public String get_main_host() {
        return getStringValue("main_host");
    }

    public String getDefault_main_host(String defaultVal) {
        return getStringValue("main_host", defaultVal);
    }

    public boolean contains_main_host(String value) {
        return containsValue("main_host", value);
    }

    public void set_product_prefix(String value) {
        setValue("product_prefix", value);
    }

    public String get_product_prefix() {
        return getStringValue("product_prefix");
    }

    public String getDefault_product_prefix(String defaultVal) {
        return getStringValue("product_prefix", defaultVal);
    }

    public boolean contains_product_prefix(String value) {
        return containsValue("product_prefix", value);
    }

    public void set_product_sufix(String value) {
        setValue("product_sufix", value);
    }

    public String get_product_sufix() {
        return getStringValue("product_sufix");
    }

    public String getDefault_product_sufix(String defaultVal) {
        return getStringValue("product_sufix", defaultVal);
    }

    public boolean contains_product_sufix(String value) {
        return containsValue("product_sufix", value);
    }

}
