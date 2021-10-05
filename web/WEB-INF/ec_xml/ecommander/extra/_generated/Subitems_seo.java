
package ecommander.extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.io.File;
import java.util.List;

public class Subitems_seo
    extends Item
{

    public final static String _NAME = "subitems_seo";
    public final static String TITLE_PREFIX = "title_prefix";
    public final static String TITLE_SUFFIX = "title_suffix";
    public final static String DESCRIPTION_PREFIX = "description_prefix";
    public final static String DESCRIPTION_SUFFIX = "description_suffix";
    public final static String TEXT = "text";
    public final static String TEXT_PIC = "text_pic";

    private Subitems_seo(Item item) {
        super(item);
    }

    public static Subitems_seo get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'subitems_seo' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Subitems_seo(item);
    }

    public static Subitems_seo newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_title_prefix(String value) {
        setValue("title_prefix", value);
    }

    public String get_title_prefix() {
        return getStringValue("title_prefix");
    }

    public String getDefault_title_prefix(String defaultVal) {
        return getStringValue("title_prefix", defaultVal);
    }

    public boolean contains_title_prefix(String value) {
        return containsValue("title_prefix", value);
    }

    public void set_title_suffix(String value) {
        setValue("title_suffix", value);
    }

    public String get_title_suffix() {
        return getStringValue("title_suffix");
    }

    public String getDefault_title_suffix(String defaultVal) {
        return getStringValue("title_suffix", defaultVal);
    }

    public boolean contains_title_suffix(String value) {
        return containsValue("title_suffix", value);
    }

    public void set_description_prefix(String value) {
        setValue("description_prefix", value);
    }

    public String get_description_prefix() {
        return getStringValue("description_prefix");
    }

    public String getDefault_description_prefix(String defaultVal) {
        return getStringValue("description_prefix", defaultVal);
    }

    public boolean contains_description_prefix(String value) {
        return containsValue("description_prefix", value);
    }

    public void set_description_suffix(String value) {
        setValue("description_suffix", value);
    }

    public String get_description_suffix() {
        return getStringValue("description_suffix");
    }

    public String getDefault_description_suffix(String defaultVal) {
        return getStringValue("description_suffix", defaultVal);
    }

    public boolean contains_description_suffix(String value) {
        return containsValue("description_suffix", value);
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

}
