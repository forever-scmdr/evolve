
package extra._generated;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Line_product
    extends Item
{

    public final static String _NAME = "line_product";
    public final static String NAME = "name";
    public final static String CODE = "code";
    public final static String PRICE = "price";
    public final static String QTY = "qty";
    public final static String UNIT = "unit";
    public final static String PRICE_OLD = "price_old";
    public final static String VENDOR = "vendor";
    public final static String VENDOR_CODE = "vendor_code";
    public final static String COUNTRY = "country";
    public final static String MAIN_PIC = "main_pic";
    public final static String SMALL_PIC = "small_pic";
    public final static String DESCRIPTION = "description";
    public final static String TEXT = "text";
    public final static String SEARCH = "search";
    public final static String FILES = "files";
    public final static String TEXT_PICS = "text_pics";
    public final static String EXTRA_XML = "extra_xml";
    public final static String EXTRA_INPUT = "extra_input";
    public final static String GROUP_ID = "group_id";
    public final static String ASSOC_CODE = "assoc_code";
    public final static String MIN_QTY = "min_qty";
    public final static String STEP = "step";
    public final static String SPEC_QTY = "spec_qty";
    public final static String GALLERY = "gallery";
    public final static String TAG = "tag";
    public final static String MARK = "mark";
    public final static String LABEL = "label";
    public final static String OFFER_ID = "offer_id";
    public final static String AVAILABLE = "available";
    public final static String CATEGORY_ID = "category_id";
    public final static String URL = "url";

    private Line_product(Item item) {
        super(item);
    }

    public static Line_product get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'line_product' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Line_product(item);
    }

    public static Line_product newChild(Item parent) {
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

    public void add_group_id(String value) {
        setValue("group_id", value);
    }

    public List<String> getAll_group_id() {
        return getStringValues("group_id");
    }

    public void remove_group_id(String value) {
        removeEqualValue("group_id", value);
    }

    public boolean contains_group_id(String value) {
        return containsValue("group_id", value);
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

}
