
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
    public final static String CATALOG_LINK = "catalog_link";
    public final static String CART = "cart";
    public final static String FAVOURITES = "favourites";
    public final static String COMPARE = "compare";
    public final static String PERSONAL = "personal";
    public final static String BOM_SEARCH = "bom_search";
    public final static String EXCEL_SEARCH_RESULTS = "excel_search_results";
    public final static String SEARCH = "search";
    public final static String SEARCH_RESULTS = "search_results";
    public final static String PLAIN_IN_PRODUCT = "plain_in_product";
    public final static String DEFAULT_CURRENCY = "default_currency";

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

    public void set_catalog_link(String value) {
        setValue("catalog_link", value);
    }

    public String get_catalog_link() {
        return getStringValue("catalog_link");
    }

    public String getDefault_catalog_link(String defaultVal) {
        return getStringValue("catalog_link", defaultVal);
    }

    public boolean contains_catalog_link(String value) {
        return containsValue("catalog_link", value);
    }

    public void set_cart(String value) {
        setValue("cart", value);
    }

    public String get_cart() {
        return getStringValue("cart");
    }

    public String getDefault_cart(String defaultVal) {
        return getStringValue("cart", defaultVal);
    }

    public boolean contains_cart(String value) {
        return containsValue("cart", value);
    }

    public void set_favourites(String value) {
        setValue("favourites", value);
    }

    public String get_favourites() {
        return getStringValue("favourites");
    }

    public String getDefault_favourites(String defaultVal) {
        return getStringValue("favourites", defaultVal);
    }

    public boolean contains_favourites(String value) {
        return containsValue("favourites", value);
    }

    public void set_compare(String value) {
        setValue("compare", value);
    }

    public String get_compare() {
        return getStringValue("compare");
    }

    public String getDefault_compare(String defaultVal) {
        return getStringValue("compare", defaultVal);
    }

    public boolean contains_compare(String value) {
        return containsValue("compare", value);
    }

    public void set_personal(String value) {
        setValue("personal", value);
    }

    public String get_personal() {
        return getStringValue("personal");
    }

    public String getDefault_personal(String defaultVal) {
        return getStringValue("personal", defaultVal);
    }

    public boolean contains_personal(String value) {
        return containsValue("personal", value);
    }

    public void set_bom_search(String value) {
        setValue("bom_search", value);
    }

    public String get_bom_search() {
        return getStringValue("bom_search");
    }

    public String getDefault_bom_search(String defaultVal) {
        return getStringValue("bom_search", defaultVal);
    }

    public boolean contains_bom_search(String value) {
        return containsValue("bom_search", value);
    }

    public void set_excel_search_results(String value) {
        setValue("excel_search_results", value);
    }

    public String get_excel_search_results() {
        return getStringValue("excel_search_results");
    }

    public String getDefault_excel_search_results(String defaultVal) {
        return getStringValue("excel_search_results", defaultVal);
    }

    public boolean contains_excel_search_results(String value) {
        return containsValue("excel_search_results", value);
    }

    public void set_search(String value) {
        setValue("search", value);
    }

    public String get_search() {
        return getStringValue("search");
    }

    public String getDefault_search(String defaultVal) {
        return getStringValue("search", defaultVal);
    }

    public boolean contains_search(String value) {
        return containsValue("search", value);
    }

    public void set_search_results(String value) {
        setValue("search_results", value);
    }

    public String get_search_results() {
        return getStringValue("search_results");
    }

    public String getDefault_search_results(String defaultVal) {
        return getStringValue("search_results", defaultVal);
    }

    public boolean contains_search_results(String value) {
        return containsValue("search_results", value);
    }

    public void set_plain_in_product(String value) {
        setValue("plain_in_product", value);
    }

    public String get_plain_in_product() {
        return getStringValue("plain_in_product");
    }

    public String getDefault_plain_in_product(String defaultVal) {
        return getStringValue("plain_in_product", defaultVal);
    }

    public boolean contains_plain_in_product(String value) {
        return containsValue("plain_in_product", value);
    }

    public void set_default_currency(String value) {
        setValue("default_currency", value);
    }

    public String get_default_currency() {
        return getStringValue("default_currency");
    }

    public String getDefault_default_currency(String defaultVal) {
        return getStringValue("default_currency", defaultVal);
    }

    public boolean contains_default_currency(String value) {
        return containsValue("default_currency", value);
    }

}
