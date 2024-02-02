
package extra._generated;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Book
    extends Item
{

    public final static String _NAME = "book";
    public final static String NAME = "name";
    public final static String CODE = "code";
    public final static String PRICE = "price";
    public final static String QTY = "qty";
    public final static String UNIT = "unit";
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
    public final static String PRICE_OLD_ORIGINAL = "price_old_original";
    public final static String MAIN_PIC = "main_pic";
    public final static String SMALL_PIC = "small_pic";
    public final static String DESCRIPTION = "description";
    public final static String TEXT = "text";
    public final static String EXTRA_XML = "extra_xml";
    public final static String TEXT_PICS = "text_pics";
    public final static String ASSOC_CODE = "assoc_code";
    public final static String TAG = "tag";
    public final static String GALLERY = "gallery";
    public final static String HAS_LINES = "has_lines";
    public final static String ARTIST = "artist";
    public final static String STARRING = "starring";
    public final static String DIRECTOR = "director";
    public final static String AUTHOR = "author";
    public final static String SERIES = "series";
    public final static String MEDIA = "media";
    public final static String YEAR = "year";
    public final static String ORIGINALNAME = "originalName";
    public final static String COUNTRY_OF_ORIGIN = "country_of_origin";
    public final static String COUNTRY = "country";
    public final static String PUBLISHER = "publisher";
    public final static String PAGE_EXTENT = "page_extent";
    public final static String LANGUAGE = "language";
    public final static String ISBN = "ISBN";
    public final static String PICTURE = "picture";
    public final static String PARENT_ID = "parent_id";

    private Book(Item item) {
        super(item);
    }

    public static Book get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'book' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Book(item);
    }

    public static Book newChild(Item parent) {
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

    public void set_price_old_original(BigDecimal value) {
        setValue("price_old_original", value);
    }

    public void setUI_price_old_original(String value)
        throws Exception
    {
        setValueUI("price_old_original", value);
    }

    public BigDecimal get_price_old_original() {
        return getDecimalValue("price_old_original");
    }

    public BigDecimal getDefault_price_old_original(BigDecimal defaultVal) {
        return getDecimalValue("price_old_original", defaultVal);
    }

    public boolean contains_price_old_original(BigDecimal value) {
        return containsValue("price_old_original", value);
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

    public void set_artist(String value) {
        setValue("artist", value);
    }

    public String get_artist() {
        return getStringValue("artist");
    }

    public String getDefault_artist(String defaultVal) {
        return getStringValue("artist", defaultVal);
    }

    public boolean contains_artist(String value) {
        return containsValue("artist", value);
    }

    public void set_starring(String value) {
        setValue("starring", value);
    }

    public String get_starring() {
        return getStringValue("starring");
    }

    public String getDefault_starring(String defaultVal) {
        return getStringValue("starring", defaultVal);
    }

    public boolean contains_starring(String value) {
        return containsValue("starring", value);
    }

    public void set_director(String value) {
        setValue("director", value);
    }

    public String get_director() {
        return getStringValue("director");
    }

    public String getDefault_director(String defaultVal) {
        return getStringValue("director", defaultVal);
    }

    public boolean contains_director(String value) {
        return containsValue("director", value);
    }

    public void set_author(String value) {
        setValue("author", value);
    }

    public String get_author() {
        return getStringValue("author");
    }

    public String getDefault_author(String defaultVal) {
        return getStringValue("author", defaultVal);
    }

    public boolean contains_author(String value) {
        return containsValue("author", value);
    }

    public void set_series(String value) {
        setValue("series", value);
    }

    public String get_series() {
        return getStringValue("series");
    }

    public String getDefault_series(String defaultVal) {
        return getStringValue("series", defaultVal);
    }

    public boolean contains_series(String value) {
        return containsValue("series", value);
    }

    public void set_media(String value) {
        setValue("media", value);
    }

    public String get_media() {
        return getStringValue("media");
    }

    public String getDefault_media(String defaultVal) {
        return getStringValue("media", defaultVal);
    }

    public boolean contains_media(String value) {
        return containsValue("media", value);
    }

    public void set_year(String value) {
        setValue("year", value);
    }

    public String get_year() {
        return getStringValue("year");
    }

    public String getDefault_year(String defaultVal) {
        return getStringValue("year", defaultVal);
    }

    public boolean contains_year(String value) {
        return containsValue("year", value);
    }

    public void set_originalName(String value) {
        setValue("originalName", value);
    }

    public String get_originalName() {
        return getStringValue("originalName");
    }

    public String getDefault_originalName(String defaultVal) {
        return getStringValue("originalName", defaultVal);
    }

    public boolean contains_originalName(String value) {
        return containsValue("originalName", value);
    }

    public void set_country_of_origin(String value) {
        setValue("country_of_origin", value);
    }

    public String get_country_of_origin() {
        return getStringValue("country_of_origin");
    }

    public String getDefault_country_of_origin(String defaultVal) {
        return getStringValue("country_of_origin", defaultVal);
    }

    public boolean contains_country_of_origin(String value) {
        return containsValue("country_of_origin", value);
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

    public void set_publisher(String value) {
        setValue("publisher", value);
    }

    public String get_publisher() {
        return getStringValue("publisher");
    }

    public String getDefault_publisher(String defaultVal) {
        return getStringValue("publisher", defaultVal);
    }

    public boolean contains_publisher(String value) {
        return containsValue("publisher", value);
    }

    public void set_page_extent(String value) {
        setValue("page_extent", value);
    }

    public String get_page_extent() {
        return getStringValue("page_extent");
    }

    public String getDefault_page_extent(String defaultVal) {
        return getStringValue("page_extent", defaultVal);
    }

    public boolean contains_page_extent(String value) {
        return containsValue("page_extent", value);
    }

    public void set_language(String value) {
        setValue("language", value);
    }

    public String get_language() {
        return getStringValue("language");
    }

    public String getDefault_language(String defaultVal) {
        return getStringValue("language", defaultVal);
    }

    public boolean contains_language(String value) {
        return containsValue("language", value);
    }

    public void set_ISBN(String value) {
        setValue("ISBN", value);
    }

    public String get_ISBN() {
        return getStringValue("ISBN");
    }

    public String getDefault_ISBN(String defaultVal) {
        return getStringValue("ISBN", defaultVal);
    }

    public boolean contains_ISBN(String value) {
        return containsValue("ISBN", value);
    }

    public void add_picture(String value) {
        setValue("picture", value);
    }

    public List<String> getAll_picture() {
        return getStringValues("picture");
    }

    public void remove_picture(String value) {
        removeEqualValue("picture", value);
    }

    public boolean contains_picture(String value) {
        return containsValue("picture", value);
    }

    public void set_parent_id(String value) {
        setValue("parent_id", value);
    }

    public String get_parent_id() {
        return getStringValue("parent_id");
    }

    public String getDefault_parent_id(String defaultVal) {
        return getStringValue("parent_id", defaultVal);
    }

    public boolean contains_parent_id(String value) {
        return containsValue("parent_id", value);
    }

}
