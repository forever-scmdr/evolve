
package lunacrawler._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Base_seo
    extends Item
{

    public final static String _NAME = "base_seo";
    public final static String TITLE = "title";
    public final static String H1 = "h1";
    public final static String DESCRIPTION = "description";
    public final static String KEYWORDS = "keywords";
    public final static String META = "meta";
    public final static String TEXT = "text";
    public final static String BOTTOM_TEXT = "bottom_text";
    public final static String TEXT_PIC = "text_pic";
    public final static String EXTRA_STYLE = "extra_style";
    public final static String BODY_CLASS = "body_class";

    private Base_seo(Item item) {
        super(item);
    }

    public static Base_seo get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'base_seo' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Base_seo(item);
    }

    public static Base_seo newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_title(String value) {
        setValue("title", value);
    }

    public String get_title() {
        return getStringValue("title");
    }

    public String getDefault_title(String defaultVal) {
        return getStringValue("title", defaultVal);
    }

    public boolean contains_title(String value) {
        return containsValue("title", value);
    }

    public void set_h1(String value) {
        setValue("h1", value);
    }

    public String get_h1() {
        return getStringValue("h1");
    }

    public String getDefault_h1(String defaultVal) {
        return getStringValue("h1", defaultVal);
    }

    public boolean contains_h1(String value) {
        return containsValue("h1", value);
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

    public void set_keywords(String value) {
        setValue("keywords", value);
    }

    public String get_keywords() {
        return getStringValue("keywords");
    }

    public String getDefault_keywords(String defaultVal) {
        return getStringValue("keywords", defaultVal);
    }

    public boolean contains_keywords(String value) {
        return containsValue("keywords", value);
    }

    public void set_meta(String value) {
        setValue("meta", value);
    }

    public String get_meta() {
        return getStringValue("meta");
    }

    public String getDefault_meta(String defaultVal) {
        return getStringValue("meta", defaultVal);
    }

    public boolean contains_meta(String value) {
        return containsValue("meta", value);
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

    public void set_bottom_text(String value) {
        setValue("bottom_text", value);
    }

    public String get_bottom_text() {
        return getStringValue("bottom_text");
    }

    public String getDefault_bottom_text(String defaultVal) {
        return getStringValue("bottom_text", defaultVal);
    }

    public boolean contains_bottom_text(String value) {
        return containsValue("bottom_text", value);
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

    public void set_extra_style(String value) {
        setValue("extra_style", value);
    }

    public String get_extra_style() {
        return getStringValue("extra_style");
    }

    public String getDefault_extra_style(String defaultVal) {
        return getStringValue("extra_style", defaultVal);
    }

    public boolean contains_extra_style(String value) {
        return containsValue("extra_style", value);
    }

    public void set_body_class(String value) {
        setValue("body_class", value);
    }

    public String get_body_class() {
        return getStringValue("body_class");
    }

    public String getDefault_body_class(String defaultVal) {
        return getStringValue("body_class", defaultVal);
    }

    public boolean contains_body_class(String value) {
        return containsValue("body_class", value);
    }

}
