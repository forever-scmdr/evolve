
package extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Page_link
    extends Item
{

    public final static String _NAME = "page_link";
    public final static String NAME = "name";
    public final static String LINK = "link";
    public final static String MAIN_PIC = "main_pic";
    public final static String SHORT = "short";
    public final static String IN_MAIN_MENU = "in_main_menu";

    private Page_link(Item item) {
        super(item);
    }

    public static Page_link get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'page_link' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Page_link(item);
    }

    public static Page_link newChild(Item parent) {
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

    public void set_link(String value) {
        setValue("link", value);
    }

    public String get_link() {
        return getStringValue("link");
    }

    public String getDefault_link(String defaultVal) {
        return getStringValue("link", defaultVal);
    }

    public boolean contains_link(String value) {
        return containsValue("link", value);
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

}
