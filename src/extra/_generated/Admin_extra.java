
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Admin_extra
    extends Item
{

    public final static String _NAME = "admin_extra";
    public final static String NAME = "name";
    public final static String SECTION_LIST = "section_list";
    public final static String MIN_PRICE_LIST = "min_price_list";
    public final static String EXCEL_IMPORT = "excel_import";
    public final static String EXCEL_UPDATE_PRICES = "excel_update_prices";
    public final static String YM_INTEGRATE = "ym_integrate";
    public final static String YM_GENERATE = "ym_generate";
    public final static String GET_PICS = "get_pics";
    public final static String CREATE_USERS = "create_users";
    public final static String CREATE_FILTERS = "create_filters";
    public final static String UPDATE_SITEMAP = "update_sitemap";
    public final static String CLEAR_CACHES = "clear_caches";
    public final static String REINDEX = "reindex";
    public final static String MANAGE_TYPES = "manage_types";
    public final static String CRAWL = "crawl";
    public final static String PUBLISH_CRAWL = "publish_crawl";

    private Admin_extra(Item item) {
        super(item);
    }

    public static Admin_extra get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'admin_extra' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Admin_extra(item);
    }

    public static Admin_extra newChild(Item parent) {
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

    public void set_section_list(Byte value) {
        setValue("section_list", value);
    }

    public void setUI_section_list(String value)
        throws Exception
    {
        setValueUI("section_list", value);
    }

    public Byte get_section_list() {
        return getByteValue("section_list");
    }

    public Byte getDefault_section_list(Byte defaultVal) {
        return getByteValue("section_list", defaultVal);
    }

    public boolean contains_section_list(Byte value) {
        return containsValue("section_list", value);
    }

    public void set_min_price_list(Byte value) {
        setValue("min_price_list", value);
    }

    public void setUI_min_price_list(String value)
        throws Exception
    {
        setValueUI("min_price_list", value);
    }

    public Byte get_min_price_list() {
        return getByteValue("min_price_list");
    }

    public Byte getDefault_min_price_list(Byte defaultVal) {
        return getByteValue("min_price_list", defaultVal);
    }

    public boolean contains_min_price_list(Byte value) {
        return containsValue("min_price_list", value);
    }

    public void set_excel_import(Byte value) {
        setValue("excel_import", value);
    }

    public void setUI_excel_import(String value)
        throws Exception
    {
        setValueUI("excel_import", value);
    }

    public Byte get_excel_import() {
        return getByteValue("excel_import");
    }

    public Byte getDefault_excel_import(Byte defaultVal) {
        return getByteValue("excel_import", defaultVal);
    }

    public boolean contains_excel_import(Byte value) {
        return containsValue("excel_import", value);
    }

    public void set_excel_update_prices(Byte value) {
        setValue("excel_update_prices", value);
    }

    public void setUI_excel_update_prices(String value)
        throws Exception
    {
        setValueUI("excel_update_prices", value);
    }

    public Byte get_excel_update_prices() {
        return getByteValue("excel_update_prices");
    }

    public Byte getDefault_excel_update_prices(Byte defaultVal) {
        return getByteValue("excel_update_prices", defaultVal);
    }

    public boolean contains_excel_update_prices(Byte value) {
        return containsValue("excel_update_prices", value);
    }

    public void set_ym_integrate(Byte value) {
        setValue("ym_integrate", value);
    }

    public void setUI_ym_integrate(String value)
        throws Exception
    {
        setValueUI("ym_integrate", value);
    }

    public Byte get_ym_integrate() {
        return getByteValue("ym_integrate");
    }

    public Byte getDefault_ym_integrate(Byte defaultVal) {
        return getByteValue("ym_integrate", defaultVal);
    }

    public boolean contains_ym_integrate(Byte value) {
        return containsValue("ym_integrate", value);
    }

    public void set_ym_generate(Byte value) {
        setValue("ym_generate", value);
    }

    public void setUI_ym_generate(String value)
        throws Exception
    {
        setValueUI("ym_generate", value);
    }

    public Byte get_ym_generate() {
        return getByteValue("ym_generate");
    }

    public Byte getDefault_ym_generate(Byte defaultVal) {
        return getByteValue("ym_generate", defaultVal);
    }

    public boolean contains_ym_generate(Byte value) {
        return containsValue("ym_generate", value);
    }

    public void set_get_pics(Byte value) {
        setValue("get_pics", value);
    }

    public void setUI_get_pics(String value)
        throws Exception
    {
        setValueUI("get_pics", value);
    }

    public Byte get_get_pics() {
        return getByteValue("get_pics");
    }

    public Byte getDefault_get_pics(Byte defaultVal) {
        return getByteValue("get_pics", defaultVal);
    }

    public boolean contains_get_pics(Byte value) {
        return containsValue("get_pics", value);
    }

    public void set_create_users(Byte value) {
        setValue("create_users", value);
    }

    public void setUI_create_users(String value)
        throws Exception
    {
        setValueUI("create_users", value);
    }

    public Byte get_create_users() {
        return getByteValue("create_users");
    }

    public Byte getDefault_create_users(Byte defaultVal) {
        return getByteValue("create_users", defaultVal);
    }

    public boolean contains_create_users(Byte value) {
        return containsValue("create_users", value);
    }

    public void set_create_filters(Byte value) {
        setValue("create_filters", value);
    }

    public void setUI_create_filters(String value)
        throws Exception
    {
        setValueUI("create_filters", value);
    }

    public Byte get_create_filters() {
        return getByteValue("create_filters");
    }

    public Byte getDefault_create_filters(Byte defaultVal) {
        return getByteValue("create_filters", defaultVal);
    }

    public boolean contains_create_filters(Byte value) {
        return containsValue("create_filters", value);
    }

    public void set_update_sitemap(Byte value) {
        setValue("update_sitemap", value);
    }

    public void setUI_update_sitemap(String value)
        throws Exception
    {
        setValueUI("update_sitemap", value);
    }

    public Byte get_update_sitemap() {
        return getByteValue("update_sitemap");
    }

    public Byte getDefault_update_sitemap(Byte defaultVal) {
        return getByteValue("update_sitemap", defaultVal);
    }

    public boolean contains_update_sitemap(Byte value) {
        return containsValue("update_sitemap", value);
    }

    public void set_clear_caches(Byte value) {
        setValue("clear_caches", value);
    }

    public void setUI_clear_caches(String value)
        throws Exception
    {
        setValueUI("clear_caches", value);
    }

    public Byte get_clear_caches() {
        return getByteValue("clear_caches");
    }

    public Byte getDefault_clear_caches(Byte defaultVal) {
        return getByteValue("clear_caches", defaultVal);
    }

    public boolean contains_clear_caches(Byte value) {
        return containsValue("clear_caches", value);
    }

    public void set_reindex(Byte value) {
        setValue("reindex", value);
    }

    public void setUI_reindex(String value)
        throws Exception
    {
        setValueUI("reindex", value);
    }

    public Byte get_reindex() {
        return getByteValue("reindex");
    }

    public Byte getDefault_reindex(Byte defaultVal) {
        return getByteValue("reindex", defaultVal);
    }

    public boolean contains_reindex(Byte value) {
        return containsValue("reindex", value);
    }

    public void set_manage_types(Byte value) {
        setValue("manage_types", value);
    }

    public void setUI_manage_types(String value)
        throws Exception
    {
        setValueUI("manage_types", value);
    }

    public Byte get_manage_types() {
        return getByteValue("manage_types");
    }

    public Byte getDefault_manage_types(Byte defaultVal) {
        return getByteValue("manage_types", defaultVal);
    }

    public boolean contains_manage_types(Byte value) {
        return containsValue("manage_types", value);
    }

    public void set_crawl(Byte value) {
        setValue("crawl", value);
    }

    public void setUI_crawl(String value)
        throws Exception
    {
        setValueUI("crawl", value);
    }

    public Byte get_crawl() {
        return getByteValue("crawl");
    }

    public Byte getDefault_crawl(Byte defaultVal) {
        return getByteValue("crawl", defaultVal);
    }

    public boolean contains_crawl(Byte value) {
        return containsValue("crawl", value);
    }

    public void set_publish_crawl(Byte value) {
        setValue("publish_crawl", value);
    }

    public void setUI_publish_crawl(String value)
        throws Exception
    {
        setValueUI("publish_crawl", value);
    }

    public Byte get_publish_crawl() {
        return getByteValue("publish_crawl");
    }

    public Byte getDefault_publish_crawl(Byte defaultVal) {
        return getByteValue("publish_crawl", defaultVal);
    }

    public boolean contains_publish_crawl(Byte value) {
        return containsValue("publish_crawl", value);
    }

}
