
package ecommander.extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.io.File;
import java.util.List;

public class Small_news_item
    extends Item
{

    public final static String _NAME = "small_news_item";
    public final static String NAME = "name";
    public final static String TEXT = "text";
    public final static String MAIN_PIC = "main_pic";
    public final static String MEDIUM_PIC = "medium_pic";
    public final static String SMALL_PIC = "small_pic";
    public final static String DATE = "date";
    public final static String UPDATE = "update";
    public final static String AUDIO = "audio";
    public final static String SOC_IMAGE = "soc_image";
    public final static String SOURCE = "source";
    public final static String SOURCE_LINK = "source_link";
    public final static String AUTHOR = "author";
    public final static String COMPLEXITY = "complexity";
    public final static String READ_TIME = "read_time";
    public final static String TWITTER_DESCRIPTION = "twitter_description";
    public final static String SIZE = "size";
    public final static String TEXT_PIC = "text_pic";
    public final static String TAG = "tag";

    private Small_news_item(Item item) {
        super(item);
    }

    public static Small_news_item get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'small_news_item' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Small_news_item(item);
    }

    public static Small_news_item newChild(Item parent) {
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

    public void set_main_pic(File value) {
        setValue("main_pic", value);
    }

    public File get_main_pic() {
        return getFileValue("main_pic", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_main_pic(File value) {
        return containsValue("main_pic", value);
    }

    public void set_medium_pic(File value) {
        setValue("medium_pic", value);
    }

    public File get_medium_pic() {
        return getFileValue("medium_pic", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_medium_pic(File value) {
        return containsValue("medium_pic", value);
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

    public void set_date(Long value) {
        setValue("date", value);
    }

    public void setUI_date(String value)
        throws Exception
    {
        setValueUI("date", value);
    }

    public Long get_date() {
        return getLongValue("date");
    }

    public Long getDefault_date(Long defaultVal) {
        return getLongValue("date", defaultVal);
    }

    public boolean contains_date(Long value) {
        return containsValue("date", value);
    }

    public void set_update(String value) {
        setValue("update", value);
    }

    public String get_update() {
        return getStringValue("update");
    }

    public String getDefault_update(String defaultVal) {
        return getStringValue("update", defaultVal);
    }

    public boolean contains_update(String value) {
        return containsValue("update", value);
    }

    public void set_audio(File value) {
        setValue("audio", value);
    }

    public File get_audio() {
        return getFileValue("audio", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_audio(File value) {
        return containsValue("audio", value);
    }

    public void set_soc_image(File value) {
        setValue("soc_image", value);
    }

    public File get_soc_image() {
        return getFileValue("soc_image", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_soc_image(File value) {
        return containsValue("soc_image", value);
    }

    public void set_source(String value) {
        setValue("source", value);
    }

    public String get_source() {
        return getStringValue("source");
    }

    public String getDefault_source(String defaultVal) {
        return getStringValue("source", defaultVal);
    }

    public boolean contains_source(String value) {
        return containsValue("source", value);
    }

    public void set_source_link(String value) {
        setValue("source_link", value);
    }

    public String get_source_link() {
        return getStringValue("source_link");
    }

    public String getDefault_source_link(String defaultVal) {
        return getStringValue("source_link", defaultVal);
    }

    public boolean contains_source_link(String value) {
        return containsValue("source_link", value);
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

    public void set_complexity(String value) {
        setValue("complexity", value);
    }

    public String get_complexity() {
        return getStringValue("complexity");
    }

    public String getDefault_complexity(String defaultVal) {
        return getStringValue("complexity", defaultVal);
    }

    public boolean contains_complexity(String value) {
        return containsValue("complexity", value);
    }

    public void set_read_time(String value) {
        setValue("read_time", value);
    }

    public String get_read_time() {
        return getStringValue("read_time");
    }

    public String getDefault_read_time(String defaultVal) {
        return getStringValue("read_time", defaultVal);
    }

    public boolean contains_read_time(String value) {
        return containsValue("read_time", value);
    }

    public void set_twitter_description(String value) {
        setValue("twitter_description", value);
    }

    public String get_twitter_description() {
        return getStringValue("twitter_description");
    }

    public String getDefault_twitter_description(String defaultVal) {
        return getStringValue("twitter_description", defaultVal);
    }

    public boolean contains_twitter_description(String value) {
        return containsValue("twitter_description", value);
    }

    public void set_size(String value) {
        setValue("size", value);
    }

    public String get_size() {
        return getStringValue("size");
    }

    public String getDefault_size(String defaultVal) {
        return getStringValue("size", defaultVal);
    }

    public boolean contains_size(String value) {
        return containsValue("size", value);
    }

    public void add_text_pic(File value) {
        setValue("text_pic", value);
    }

    public List<File> getAll_text_pic() {
        return getFileValues("text_pic", AppContext.getCommonFilesDirPath());
    }

    public void remove_text_pic(File value) {
        removeEqualValue("text_pic", value);
    }

    public boolean contains_text_pic(File value) {
        return containsValue("text_pic", value);
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

}
