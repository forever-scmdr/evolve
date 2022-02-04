
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Display_settings
    extends Item
{

    public final static String _NAME = "display_settings";
    public final static String NAME = "name";
    public final static String SIDE_MENU_PAGES = "side_menu_pages";
    public final static String CATALOG_QUICK_SEARCH = "catalog_quick_search";
    public final static String MANUAL_FILTER_PARAMS = "manual_filter_params";
    public final static String CURRENCY_RATES = "currency_rates";
    public final static String JUR_PRICE = "jur_price";
    public final static String PRODUCT_UPDATE_SUBSCRIBE = "product_update_subscribe";
    public final static String PRODUCT_QUICK_VIEW = "product_quick_view";

    private Display_settings(Item item) {
        super(item);
    }

    public static Display_settings get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'display_settings' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Display_settings(item);
    }

    public static Display_settings newChild(Item parent) {
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

    public void set_side_menu_pages(String value) {
        setValue("side_menu_pages", value);
    }

    public String get_side_menu_pages() {
        return getStringValue("side_menu_pages");
    }

    public String getDefault_side_menu_pages(String defaultVal) {
        return getStringValue("side_menu_pages", defaultVal);
    }

    public boolean contains_side_menu_pages(String value) {
        return containsValue("side_menu_pages", value);
    }

    public void set_catalog_quick_search(String value) {
        setValue("catalog_quick_search", value);
    }

    public String get_catalog_quick_search() {
        return getStringValue("catalog_quick_search");
    }

    public String getDefault_catalog_quick_search(String defaultVal) {
        return getStringValue("catalog_quick_search", defaultVal);
    }

    public boolean contains_catalog_quick_search(String value) {
        return containsValue("catalog_quick_search", value);
    }

    public void set_manual_filter_params(String value) {
        setValue("manual_filter_params", value);
    }

    public String get_manual_filter_params() {
        return getStringValue("manual_filter_params");
    }

    public String getDefault_manual_filter_params(String defaultVal) {
        return getStringValue("manual_filter_params", defaultVal);
    }

    public boolean contains_manual_filter_params(String value) {
        return containsValue("manual_filter_params", value);
    }

    public void set_currency_rates(String value) {
        setValue("currency_rates", value);
    }

    public String get_currency_rates() {
        return getStringValue("currency_rates");
    }

    public String getDefault_currency_rates(String defaultVal) {
        return getStringValue("currency_rates", defaultVal);
    }

    public boolean contains_currency_rates(String value) {
        return containsValue("currency_rates", value);
    }

    public void set_jur_price(String value) {
        setValue("jur_price", value);
    }

    public String get_jur_price() {
        return getStringValue("jur_price");
    }

    public String getDefault_jur_price(String defaultVal) {
        return getStringValue("jur_price", defaultVal);
    }

    public boolean contains_jur_price(String value) {
        return containsValue("jur_price", value);
    }

    public void set_product_update_subscribe(String value) {
        setValue("product_update_subscribe", value);
    }

    public String get_product_update_subscribe() {
        return getStringValue("product_update_subscribe");
    }

    public String getDefault_product_update_subscribe(String defaultVal) {
        return getStringValue("product_update_subscribe", defaultVal);
    }

    public boolean contains_product_update_subscribe(String value) {
        return containsValue("product_update_subscribe", value);
    }

    public void set_product_quick_view(String value) {
        setValue("product_quick_view", value);
    }

    public String get_product_quick_view() {
        return getStringValue("product_quick_view");
    }

    public String getDefault_product_quick_view(String defaultVal) {
        return getStringValue("product_quick_view", defaultVal);
    }

    public boolean contains_product_quick_view(String value) {
        return containsValue("product_quick_view", value);
    }

}
