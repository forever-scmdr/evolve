
package extra._generated;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Described_product
    extends Item
{

    public final static String _NAME = "described_product";
    public final static String NAME = "name";
    public final static String CODE = "code";
    public final static String PRICE = "price";
    public final static String QTY = "qty";
    public final static String QTY_OPT = "qty_opt";
    public final static String UNIT = "unit";
    public final static String SECTION_NAME = "section_name";
    public final static String TYPE = "type";
    public final static String NAME_EXTRA = "name_extra";
    public final static String VENDOR = "vendor";
    public final static String VENDOR_CODE = "vendor_code";
    public final static String OFFER_ID = "offer_id";
    public final static String AVAILABLE = "available";
    public final static String GROUP_ID = "group_id";
    public final static String URL = "url";
    public final static String CATEGORY_ID = "category_id";
    public final static String CURRENCY_ID = "currency_id";
    public final static String PRICE_ORIGINAL = "price_original";
    public final static String PRICE_OLD = "price_old";
    public final static String PRICE_OPT = "price_opt";
    public final static String PRICE_OPT_OLD = "price_opt_old";
    public final static String NEXT_DELIVERY = "next_delivery";
    public final static String STATUS = "status";
    public final static String SEARCH = "search";
    public final static String MIN_QTY = "min_qty";
    public final static String STEP = "step";
    public final static String SPEC_QTY = "spec_qty";
    public final static String COUNTRY = "country";
    public final static String MAIN_PIC = "main_pic";
    public final static String SMALL_PIC = "small_pic";
    public final static String DESCRIPTION = "description";
    public final static String TEXT = "text";
    public final static String EXTRA_XML = "extra_xml";
    public final static String FILES = "files";
    public final static String TEXT_PICS = "text_pics";
    public final static String ASSOC_CODE = "assoc_code";
    public final static String TAG = "tag";
    public final static String MARK = "mark";
    public final static String LABEL = "label";
    public final static String EXTRA_INPUT = "extra_input";
    public final static String PRICE_RUB = "price_RUB";
    public final static String PRICE_RUB_OLD = "price_RUB_old";
    public final static String PRICE_USD = "price_USD";
    public final static String PRICE_USD_OLD = "price_USD_old";
    public final static String PRICE_EUR = "price_EUR";
    public final static String PRICE_EUR_OLD = "price_EUR_old";
    public final static String PRICE_BYN = "price_BYN";
    public final static String PRICE_BYN_OLD = "price_BYN_old";
    public final static String GALLERY = "gallery";

    private Described_product(Item item) {
        super(item);
    }

    public static Described_product get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'described_product' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Described_product(item);
    }

    public static Described_product newChild(Item parent) {
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

    public void set_code(String value) {
        setValue("code", value);
    }

    public String get_code() {
        return getStringValue("code");
    }

    public String getDefault_code(String defaultVal) {
        return getStringValue("code", defaultVal);
    }

    public boolean contains_code(String value) {
        return containsValue("code", value);
    }

    public void set_price(BigDecimal value) {
        setValue("price", value);
    }

    public void setUI_price(String value)
        throws Exception
    {
        setValueUI("price", value);
    }

    public BigDecimal get_price() {
        return getDecimalValue("price");
    }

    public BigDecimal getDefault_price(BigDecimal defaultVal) {
        return getDecimalValue("price", defaultVal);
    }

    public boolean contains_price(BigDecimal value) {
        return containsValue("price", value);
    }

    public void set_qty(Double value) {
        setValue("qty", value);
    }

    public void setUI_qty(String value)
        throws Exception
    {
        setValueUI("qty", value);
    }

    public Double get_qty() {
        return getDoubleValue("qty");
    }

    public Double getDefault_qty(Double defaultVal) {
        return getDoubleValue("qty", defaultVal);
    }

    public boolean contains_qty(Double value) {
        return containsValue("qty", value);
    }

    public void set_qty_opt(Double value) {
        setValue("qty_opt", value);
    }

    public void setUI_qty_opt(String value)
        throws Exception
    {
        setValueUI("qty_opt", value);
    }

    public Double get_qty_opt() {
        return getDoubleValue("qty_opt");
    }

    public Double getDefault_qty_opt(Double defaultVal) {
        return getDoubleValue("qty_opt", defaultVal);
    }

    public boolean contains_qty_opt(Double value) {
        return containsValue("qty_opt", value);
    }

    public void set_unit(String value) {
        setValue("unit", value);
    }

    public String get_unit() {
        return getStringValue("unit");
    }

    public String getDefault_unit(String defaultVal) {
        return getStringValue("unit", defaultVal);
    }

    public boolean contains_unit(String value) {
        return containsValue("unit", value);
    }

    public void set_section_name(String value) {
        setValue("section_name", value);
    }

    public String get_section_name() {
        return getStringValue("section_name");
    }

    public String getDefault_section_name(String defaultVal) {
        return getStringValue("section_name", defaultVal);
    }

    public boolean contains_section_name(String value) {
        return containsValue("section_name", value);
    }

    public void set_type(String value) {
        setValue("type", value);
    }

    public String get_type() {
        return getStringValue("type");
    }

    public String getDefault_type(String defaultVal) {
        return getStringValue("type", defaultVal);
    }

    public boolean contains_type(String value) {
        return containsValue("type", value);
    }

    public void set_name_extra(String value) {
        setValue("name_extra", value);
    }

    public String get_name_extra() {
        return getStringValue("name_extra");
    }

    public String getDefault_name_extra(String defaultVal) {
        return getStringValue("name_extra", defaultVal);
    }

    public boolean contains_name_extra(String value) {
        return containsValue("name_extra", value);
    }

    public void set_vendor(String value) {
        setValue("vendor", value);
    }

    public String get_vendor() {
        return getStringValue("vendor");
    }

    public String getDefault_vendor(String defaultVal) {
        return getStringValue("vendor", defaultVal);
    }

    public boolean contains_vendor(String value) {
        return containsValue("vendor", value);
    }

    public void set_vendor_code(String value) {
        setValue("vendor_code", value);
    }

    public String get_vendor_code() {
        return getStringValue("vendor_code");
    }

    public String getDefault_vendor_code(String defaultVal) {
        return getStringValue("vendor_code", defaultVal);
    }

    public boolean contains_vendor_code(String value) {
        return containsValue("vendor_code", value);
    }

    public void set_offer_id(String value) {
        setValue("offer_id", value);
    }

    public String get_offer_id() {
        return getStringValue("offer_id");
    }

    public String getDefault_offer_id(String defaultVal) {
        return getStringValue("offer_id", defaultVal);
    }

    public boolean contains_offer_id(String value) {
        return containsValue("offer_id", value);
    }

    public void set_available(Byte value) {
        setValue("available", value);
    }

    public void setUI_available(String value)
        throws Exception
    {
        setValueUI("available", value);
    }

    public Byte get_available() {
        return getByteValue("available");
    }

    public Byte getDefault_available(Byte defaultVal) {
        return getByteValue("available", defaultVal);
    }

    public boolean contains_available(Byte value) {
        return containsValue("available", value);
    }

    public void set_group_id(String value) {
        setValue("group_id", value);
    }

    public String get_group_id() {
        return getStringValue("group_id");
    }

    public String getDefault_group_id(String defaultVal) {
        return getStringValue("group_id", defaultVal);
    }

    public boolean contains_group_id(String value) {
        return containsValue("group_id", value);
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

    public void set_category_id(String value) {
        setValue("category_id", value);
    }

    public String get_category_id() {
        return getStringValue("category_id");
    }

    public String getDefault_category_id(String defaultVal) {
        return getStringValue("category_id", defaultVal);
    }

    public boolean contains_category_id(String value) {
        return containsValue("category_id", value);
    }

    public void set_currency_id(String value) {
        setValue("currency_id", value);
    }

    public String get_currency_id() {
        return getStringValue("currency_id");
    }

    public String getDefault_currency_id(String defaultVal) {
        return getStringValue("currency_id", defaultVal);
    }

    public boolean contains_currency_id(String value) {
        return containsValue("currency_id", value);
    }

    public void set_price_original(BigDecimal value) {
        setValue("price_original", value);
    }

    public void setUI_price_original(String value)
        throws Exception
    {
        setValueUI("price_original", value);
    }

    public BigDecimal get_price_original() {
        return getDecimalValue("price_original");
    }

    public BigDecimal getDefault_price_original(BigDecimal defaultVal) {
        return getDecimalValue("price_original", defaultVal);
    }

    public boolean contains_price_original(BigDecimal value) {
        return containsValue("price_original", value);
    }

    public void set_price_old(BigDecimal value) {
        setValue("price_old", value);
    }

    public void setUI_price_old(String value)
        throws Exception
    {
        setValueUI("price_old", value);
    }

    public BigDecimal get_price_old() {
        return getDecimalValue("price_old");
    }

    public BigDecimal getDefault_price_old(BigDecimal defaultVal) {
        return getDecimalValue("price_old", defaultVal);
    }

    public boolean contains_price_old(BigDecimal value) {
        return containsValue("price_old", value);
    }

    public void set_price_opt(BigDecimal value) {
        setValue("price_opt", value);
    }

    public void setUI_price_opt(String value)
        throws Exception
    {
        setValueUI("price_opt", value);
    }

    public BigDecimal get_price_opt() {
        return getDecimalValue("price_opt");
    }

    public BigDecimal getDefault_price_opt(BigDecimal defaultVal) {
        return getDecimalValue("price_opt", defaultVal);
    }

    public boolean contains_price_opt(BigDecimal value) {
        return containsValue("price_opt", value);
    }

    public void set_price_opt_old(BigDecimal value) {
        setValue("price_opt_old", value);
    }

    public void setUI_price_opt_old(String value)
        throws Exception
    {
        setValueUI("price_opt_old", value);
    }

    public BigDecimal get_price_opt_old() {
        return getDecimalValue("price_opt_old");
    }

    public BigDecimal getDefault_price_opt_old(BigDecimal defaultVal) {
        return getDecimalValue("price_opt_old", defaultVal);
    }

    public boolean contains_price_opt_old(BigDecimal value) {
        return containsValue("price_opt_old", value);
    }

    public void set_next_delivery(String value) {
        setValue("next_delivery", value);
    }

    public String get_next_delivery() {
        return getStringValue("next_delivery");
    }

    public String getDefault_next_delivery(String defaultVal) {
        return getStringValue("next_delivery", defaultVal);
    }

    public boolean contains_next_delivery(String value) {
        return containsValue("next_delivery", value);
    }

    public void set_status(String value) {
        setValue("status", value);
    }

    public String get_status() {
        return getStringValue("status");
    }

    public String getDefault_status(String defaultVal) {
        return getStringValue("status", defaultVal);
    }

    public boolean contains_status(String value) {
        return containsValue("status", value);
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

    public void set_min_qty(Double value) {
        setValue("min_qty", value);
    }

    public void setUI_min_qty(String value)
        throws Exception
    {
        setValueUI("min_qty", value);
    }

    public Double get_min_qty() {
        return getDoubleValue("min_qty");
    }

    public Double getDefault_min_qty(Double defaultVal) {
        return getDoubleValue("min_qty", defaultVal);
    }

    public boolean contains_min_qty(Double value) {
        return containsValue("min_qty", value);
    }

    public void set_step(Double value) {
        setValue("step", value);
    }

    public void setUI_step(String value)
        throws Exception
    {
        setValueUI("step", value);
    }

    public Double get_step() {
        return getDoubleValue("step");
    }

    public Double getDefault_step(Double defaultVal) {
        return getDoubleValue("step", defaultVal);
    }

    public boolean contains_step(Double value) {
        return containsValue("step", value);
    }

    public void set_spec_qty(Double value) {
        setValue("spec_qty", value);
    }

    public void setUI_spec_qty(String value)
        throws Exception
    {
        setValueUI("spec_qty", value);
    }

    public Double get_spec_qty() {
        return getDoubleValue("spec_qty");
    }

    public Double getDefault_spec_qty(Double defaultVal) {
        return getDoubleValue("spec_qty", defaultVal);
    }

    public boolean contains_spec_qty(Double value) {
        return containsValue("spec_qty", value);
    }

    public void set_country(String value) {
        setValue("country", value);
    }

    public String get_country() {
        return getStringValue("country");
    }

    public String getDefault_country(String defaultVal) {
        return getStringValue("country", defaultVal);
    }

    public boolean contains_country(String value) {
        return containsValue("country", value);
    }

    public void set_main_pic(File value) {
        setValue("main_pic", value);
    }

    public File get_main_pic() {
        return getFileValue("main_pic", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_main_pic(File value) {
        return containsValue("main_pic", value);
    }

    public void set_small_pic(File value) {
        setValue("small_pic", value);
    }

    public File get_small_pic() {
        return getFileValue("small_pic", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_small_pic(File value) {
        return containsValue("small_pic", value);
    }

    public void set_description(String value) {
        setValue("description", value);
    }

    public String get_description() {
        return getStringValue("description");
    }

    public String getDefault_description(String defaultVal) {
        return getStringValue("description", defaultVal);
    }

    public boolean contains_description(String value) {
        return containsValue("description", value);
    }

    public void set_text(String value) {
        setValue("text", value);
    }

    public String get_text() {
        return getStringValue("text");
    }

    public String getDefault_text(String defaultVal) {
        return getStringValue("text", defaultVal);
    }

    public boolean contains_text(String value) {
        return containsValue("text", value);
    }

    public void set_extra_xml(String value) {
        setValue("extra_xml", value);
    }

    public String get_extra_xml() {
        return getStringValue("extra_xml");
    }

    public String getDefault_extra_xml(String defaultVal) {
        return getStringValue("extra_xml", defaultVal);
    }

    public boolean contains_extra_xml(String value) {
        return containsValue("extra_xml", value);
    }

    public void add_files(File value) {
        setValue("files", value);
    }

    public List<File> getAll_files() {
        return getFileValues("files", AppContext.getCommonFilesDirPath());
    }

    public void remove_files(File value) {
        removeEqualValue("files", value);
    }

    public boolean contains_files(File value) {
        return containsValue("files", value);
    }

    public void add_text_pics(File value) {
        setValue("text_pics", value);
    }

    public List<File> getAll_text_pics() {
        return getFileValues("text_pics", AppContext.getCommonFilesDirPath());
    }

    public void remove_text_pics(File value) {
        removeEqualValue("text_pics", value);
    }

    public boolean contains_text_pics(File value) {
        return containsValue("text_pics", value);
    }

    public void add_assoc_code(String value) {
        setValue("assoc_code", value);
    }

    public List<String> getAll_assoc_code() {
        return getStringValues("assoc_code");
    }

    public void remove_assoc_code(String value) {
        removeEqualValue("assoc_code", value);
    }

    public boolean contains_assoc_code(String value) {
        return containsValue("assoc_code", value);
    }

    public void add_tag(String value) {
        setValue("tag", value);
    }

    public List<String> getAll_tag() {
        return getStringValues("tag");
    }

    public void remove_tag(String value) {
        removeEqualValue("tag", value);
    }

    public boolean contains_tag(String value) {
        return containsValue("tag", value);
    }

    public void add_mark(String value) {
        setValue("mark", value);
    }

    public List<String> getAll_mark() {
        return getStringValues("mark");
    }

    public void remove_mark(String value) {
        removeEqualValue("mark", value);
    }

    public boolean contains_mark(String value) {
        return containsValue("mark", value);
    }

    public void add_label(String value) {
        setValue("label", value);
    }

    public List<String> getAll_label() {
        return getStringValues("label");
    }

    public void remove_label(String value) {
        removeEqualValue("label", value);
    }

    public boolean contains_label(String value) {
        return containsValue("label", value);
    }

    public void add_extra_input(String value) {
        setValue("extra_input", value);
    }

    public List<String> getAll_extra_input() {
        return getStringValues("extra_input");
    }

    public void remove_extra_input(String value) {
        removeEqualValue("extra_input", value);
    }

    public boolean contains_extra_input(String value) {
        return containsValue("extra_input", value);
    }

    public void set_price_RUB(BigDecimal value) {
        setValue("price_RUB", value);
    }

    public void setUI_price_RUB(String value)
        throws Exception
    {
        setValueUI("price_RUB", value);
    }

    public BigDecimal get_price_RUB() {
        return getDecimalValue("price_RUB");
    }

    public BigDecimal getDefault_price_RUB(BigDecimal defaultVal) {
        return getDecimalValue("price_RUB", defaultVal);
    }

    public boolean contains_price_RUB(BigDecimal value) {
        return containsValue("price_RUB", value);
    }

    public void set_price_RUB_old(BigDecimal value) {
        setValue("price_RUB_old", value);
    }

    public void setUI_price_RUB_old(String value)
        throws Exception
    {
        setValueUI("price_RUB_old", value);
    }

    public BigDecimal get_price_RUB_old() {
        return getDecimalValue("price_RUB_old");
    }

    public BigDecimal getDefault_price_RUB_old(BigDecimal defaultVal) {
        return getDecimalValue("price_RUB_old", defaultVal);
    }

    public boolean contains_price_RUB_old(BigDecimal value) {
        return containsValue("price_RUB_old", value);
    }

    public void set_price_USD(BigDecimal value) {
        setValue("price_USD", value);
    }

    public void setUI_price_USD(String value)
        throws Exception
    {
        setValueUI("price_USD", value);
    }

    public BigDecimal get_price_USD() {
        return getDecimalValue("price_USD");
    }

    public BigDecimal getDefault_price_USD(BigDecimal defaultVal) {
        return getDecimalValue("price_USD", defaultVal);
    }

    public boolean contains_price_USD(BigDecimal value) {
        return containsValue("price_USD", value);
    }

    public void set_price_USD_old(BigDecimal value) {
        setValue("price_USD_old", value);
    }

    public void setUI_price_USD_old(String value)
        throws Exception
    {
        setValueUI("price_USD_old", value);
    }

    public BigDecimal get_price_USD_old() {
        return getDecimalValue("price_USD_old");
    }

    public BigDecimal getDefault_price_USD_old(BigDecimal defaultVal) {
        return getDecimalValue("price_USD_old", defaultVal);
    }

    public boolean contains_price_USD_old(BigDecimal value) {
        return containsValue("price_USD_old", value);
    }

    public void set_price_EUR(BigDecimal value) {
        setValue("price_EUR", value);
    }

    public void setUI_price_EUR(String value)
        throws Exception
    {
        setValueUI("price_EUR", value);
    }

    public BigDecimal get_price_EUR() {
        return getDecimalValue("price_EUR");
    }

    public BigDecimal getDefault_price_EUR(BigDecimal defaultVal) {
        return getDecimalValue("price_EUR", defaultVal);
    }

    public boolean contains_price_EUR(BigDecimal value) {
        return containsValue("price_EUR", value);
    }

    public void set_price_EUR_old(BigDecimal value) {
        setValue("price_EUR_old", value);
    }

    public void setUI_price_EUR_old(String value)
        throws Exception
    {
        setValueUI("price_EUR_old", value);
    }

    public BigDecimal get_price_EUR_old() {
        return getDecimalValue("price_EUR_old");
    }

    public BigDecimal getDefault_price_EUR_old(BigDecimal defaultVal) {
        return getDecimalValue("price_EUR_old", defaultVal);
    }

    public boolean contains_price_EUR_old(BigDecimal value) {
        return containsValue("price_EUR_old", value);
    }

    public void set_price_BYN(BigDecimal value) {
        setValue("price_BYN", value);
    }

    public void setUI_price_BYN(String value)
        throws Exception
    {
        setValueUI("price_BYN", value);
    }

    public BigDecimal get_price_BYN() {
        return getDecimalValue("price_BYN");
    }

    public BigDecimal getDefault_price_BYN(BigDecimal defaultVal) {
        return getDecimalValue("price_BYN", defaultVal);
    }

    public boolean contains_price_BYN(BigDecimal value) {
        return containsValue("price_BYN", value);
    }

    public void set_price_BYN_old(BigDecimal value) {
        setValue("price_BYN_old", value);
    }

    public void setUI_price_BYN_old(String value)
        throws Exception
    {
        setValueUI("price_BYN_old", value);
    }

    public BigDecimal get_price_BYN_old() {
        return getDecimalValue("price_BYN_old");
    }

    public BigDecimal getDefault_price_BYN_old(BigDecimal defaultVal) {
        return getDecimalValue("price_BYN_old", defaultVal);
    }

    public boolean contains_price_BYN_old(BigDecimal value) {
        return containsValue("price_BYN_old", value);
    }

    public void add_gallery(File value) {
        setValue("gallery", value);
    }

    public List<File> getAll_gallery() {
        return getFileValues("gallery", AppContext.getCommonFilesDirPath());
    }

    public void remove_gallery(File value) {
        removeEqualValue("gallery", value);
    }

    public boolean contains_gallery(File value) {
        return containsValue("gallery", value);
    }

}
