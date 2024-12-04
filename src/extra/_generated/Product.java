
package extra._generated;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Product
    extends Item
{

    public final static String _NAME = "product";
    public final static String NAME = "name";
    public final static String CODE = "code";
    public final static String PRICE = "price";
    public final static String QTY = "qty";
    public final static String QTY_OPT = "qty_opt";
    public final static String UNIT = "unit";
    public final static String TYPE = "type";
    public final static String NAME_EXTRA = "name_extra";
    public final static String VENDOR = "vendor";
    public final static String VENDOR_CODE = "vendor_code";
    public final static String IS_SERVICE = "is_service";
    public final static String OFFER_ID = "offer_id";
    public final static String AVAILABLE = "available";
    public final static String AVAILABLE2 = "available2";
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
    public final static String PIC_PATH = "pic_path";
    public final static String SPECIAL_PRICE = "special_price";
    public final static String HIT = "hit";
    public final static String NEW = "new";
    public final static String SOON = "soon";
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
    public final static String PICTURE = "picture";
    public final static String FILE = "file";
    public final static String STRICT_SEARCH = "strict_search";
    public final static String ANALOG_SEARCH = "analog_search";
    public final static String ANALOG_CODE = "analog_code";
    public final static String EXTRA_PIC = "extra_pic";
    public final static String FILEVID = "filevid";
    public final static String REL_CODE = "rel_code";
    public final static String ANALOG = "analog";
    public final static String BARCODE = "barcode";
    public final static String STORE = "store";
    public final static String EXTRA_CODE = "extra_code";
    public final static String GALLERY = "gallery";
    public final static String HAS_LINES = "has_lines";

    private Product(Item item) {
        super(item);
    }

    public static Product get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'product' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Product(item);
    }

    public static Product newChild(Item parent) {
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

    public void set_is_service(Byte value) {
        setValue("is_service", value);
    }

    public void setUI_is_service(String value)
        throws Exception
    {
        setValueUI("is_service", value);
    }

    public Byte get_is_service() {
        return getByteValue("is_service");
    }

    public Byte getDefault_is_service(Byte defaultVal) {
        return getByteValue("is_service", defaultVal);
    }

    public boolean contains_is_service(Byte value) {
        return containsValue("is_service", value);
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

    public void set_available2(Double value) {
        setValue("available2", value);
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

    public void add_search(String value) {
        setValue("search", value);
    }

    public List<String> getAll_search() {
        return getStringValues("search");
    }

    public void remove_search(String value) {
        removeEqualValue("search", value);
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

    public void set_pic_path(String value) {
        setValue("pic_path", value);
    }

    public String get_pic_path() {
        return getStringValue("pic_path");
    }

    public String getDefault_pic_path(String defaultVal) {
        return getStringValue("pic_path", defaultVal);
    }

    public boolean contains_pic_path(String value) {
        return containsValue("pic_path", value);
    }

    public void set_special_price(String value) {
        setValue("special_price", value);
    }

    public String get_special_price() {
        return getStringValue("special_price");
    }

    public String getDefault_special_price(String defaultVal) {
        return getStringValue("special_price", defaultVal);
    }

    public boolean contains_special_price(String value) {
        return containsValue("special_price", value);
    }

    public void set_hit(Byte value) {
        setValue("hit", value);
    }

    public void setUI_hit(String value)
        throws Exception
    {
        setValueUI("hit", value);
    }

    public Byte get_hit() {
        return getByteValue("hit");
    }

    public Byte getDefault_hit(Byte defaultVal) {
        return getByteValue("hit", defaultVal);
    }

    public boolean contains_hit(Byte value) {
        return containsValue("hit", value);
    }

    public void set_new(Byte value) {
        setValue("new", value);
    }

    public void setUI_new(String value)
        throws Exception
    {
        setValueUI("new", value);
    }

    public Byte get_new() {
        return getByteValue("new");
    }

    public Byte getDefault_new(Byte defaultVal) {
        return getByteValue("new", defaultVal);
    }

    public boolean contains_new(Byte value) {
        return containsValue("new", value);
    }

    public void set_soon(Long value) {
        setValue("soon", value);
    }

    public void setUI_soon(String value)
        throws Exception
    {
        setValueUI("soon", value);
    }

    public Long get_soon() {
        return getLongValue("soon");
    }

    public Long getDefault_soon(Long defaultVal) {
        return getLongValue("soon", defaultVal);
    }

    public boolean contains_soon(Long value) {
        return containsValue("soon", value);
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

    public void add_picture(File value) {
        setValue("picture", value);
    }

    public List<File> getAll_picture() {
        return getFileValues("picture", AppContext.getCommonFilesDirPath());
    }

    public void remove_picture(File value) {
        removeEqualValue("picture", value);
    }

    public boolean contains_picture(File value) {
        return containsValue("picture", value);
    }

    public void add_file(String value) {
        setValue("file", value);
    }

    public List<String> getAll_file() {
        return getStringValues("file");
    }

    public void remove_file(String value) {
        removeEqualValue("file", value);
    }

    public boolean contains_file(String value) {
        return containsValue("file", value);
    }

    public void add_strict_search(String value) {
        setValue("strict_search", value);
    }

    public List<String> getAll_strict_search() {
        return getStringValues("strict_search");
    }

    public void remove_strict_search(String value) {
        removeEqualValue("strict_search", value);
    }

    public boolean contains_strict_search(String value) {
        return containsValue("strict_search", value);
    }

    public void add_analog_search(String value) {
        setValue("analog_search", value);
    }

    public List<String> getAll_analog_search() {
        return getStringValues("analog_search");
    }

    public void remove_analog_search(String value) {
        removeEqualValue("analog_search", value);
    }

    public boolean contains_analog_search(String value) {
        return containsValue("analog_search", value);
    }

    public void add_analog_code(String value) {
        setValue("analog_code", value);
    }

    public List<String> getAll_analog_code() {
        return getStringValues("analog_code");
    }

    public void remove_analog_code(String value) {
        removeEqualValue("analog_code", value);
    }

    public boolean contains_analog_code(String value) {
        return containsValue("analog_code", value);
    }

    public void add_extra_pic(String value) {
        setValue("extra_pic", value);
    }

    public List<String> getAll_extra_pic() {
        return getStringValues("extra_pic");
    }

    public void remove_extra_pic(String value) {
        removeEqualValue("extra_pic", value);
    }

    public boolean contains_extra_pic(String value) {
        return containsValue("extra_pic", value);
    }

    public void add_filevid(String value) {
        setValue("filevid", value);
    }

    public List<String> getAll_filevid() {
        return getStringValues("filevid");
    }

    public void remove_filevid(String value) {
        removeEqualValue("filevid", value);
    }

    public boolean contains_filevid(String value) {
        return containsValue("filevid", value);
    }

    public void add_rel_code(String value) {
        setValue("rel_code", value);
    }

    public List<String> getAll_rel_code() {
        return getStringValues("rel_code");
    }

    public void remove_rel_code(String value) {
        removeEqualValue("rel_code", value);
    }

    public boolean contains_rel_code(String value) {
        return containsValue("rel_code", value);
    }

    public void set_analog(String value) {
        setValue("analog", value);
    }

    public String get_analog() {
        return getStringValue("analog");
    }

    public String getDefault_analog(String defaultVal) {
        return getStringValue("analog", defaultVal);
    }

    public boolean contains_analog(String value) {
        return containsValue("analog", value);
    }

    public void set_barcode(String value) {
        setValue("barcode", value);
    }

    public String get_barcode() {
        return getStringValue("barcode");
    }

    public String getDefault_barcode(String defaultVal) {
        return getStringValue("barcode", defaultVal);
    }

    public boolean contains_barcode(String value) {
        return containsValue("barcode", value);
    }

    public void set_store(String value) {
        setValue("store", value);
    }

    public String get_store() {
        return getStringValue("store");
    }

    public String getDefault_store(String defaultVal) {
        return getStringValue("store", defaultVal);
    }

    public boolean contains_store(String value) {
        return containsValue("store", value);
    }

    public void set_extra_code(String value) {
        setValue("extra_code", value);
    }

    public String get_extra_code() {
        return getStringValue("extra_code");
    }

    public String getDefault_extra_code(String defaultVal) {
        return getStringValue("extra_code", defaultVal);
    }

    public boolean contains_extra_code(String value) {
        return containsValue("extra_code", value);
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

    public void set_has_lines(Byte value) {
        setValue("has_lines", value);
    }

    public void setUI_has_lines(String value)
        throws Exception
    {
        setValueUI("has_lines", value);
    }

    public Byte get_has_lines() {
        return getByteValue("has_lines");
    }

    public Byte getDefault_has_lines(Byte defaultVal) {
        return getByteValue("has_lines", defaultVal);
    }

    public boolean contains_has_lines(Byte value) {
        return containsValue("has_lines", value);
    }

}
