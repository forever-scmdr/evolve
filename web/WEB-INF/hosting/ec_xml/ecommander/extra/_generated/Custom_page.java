
package ecommander.extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Custom_page
    extends Item
{

    public final static String _NAME = "custom_page";
    public final static String HEADER = "header";
    public final static String MAIN_PIC = "main_pic";
    public final static String SHORT = "short";
    public final static String IN_MAIN_MENU = "in_main_menu";
    public final static String TEXT = "text";
    public final static String TEXT_PIC = "text_pic";

    private Custom_page(Item item) {
        super(item);
    }

    public static Custom_page get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'custom_page' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Custom_page(item);
    }

    public static Custom_page newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_header(String value) {
        setValue("header", value);
    }

    public String get_header() {
        return getStringValue("header");
    }

    public String getDefault_header(String defaultVal) {
        return getStringValue("header", defaultVal);
    }

    public boolean contains_header(String value) {
        return containsValue("header", value);
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

    public void set_short(String value) {
        setValue("short", value);
    }

    public String get_short() {
        return getStringValue("short");
    }

    public String getDefault_short(String defaultVal) {
        return getStringValue("short", defaultVal);
    }

    public boolean contains_short(String value) {
        return containsValue("short", value);
    }

    public void set_in_main_menu(String value) {
        setValue("in_main_menu", value);
    }

    public String get_in_main_menu() {
        return getStringValue("in_main_menu");
    }

    public String getDefault_in_main_menu(String defaultVal) {
        return getStringValue("in_main_menu", defaultVal);
    }

    public boolean contains_in_main_menu(String value) {
        return containsValue("in_main_menu", value);
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
