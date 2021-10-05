
package ecommander.extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.io.File;

public class Ad_banner
    extends Item
{

    public final static String _NAME = "ad_banner";
    public final static String NAME = "name";
    public final static String LINK = "link";
    public final static String PIC = "pic";
    public final static String TEXT = "text";
    public final static String CODE = "code";
    public final static String STYLE = "style";

    private Ad_banner(Item item) {
        super(item);
    }

    public static Ad_banner get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'ad_banner' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Ad_banner(item);
    }

    public static Ad_banner newChild(Item parent) {
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

    public void set_pic(File value) {
        setValue("pic", value);
    }

    public File get_pic() {
        return getFileValue("pic", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_pic(File value) {
        return containsValue("pic", value);
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

    public void set_style(String value) {
        setValue("style", value);
    }

    public String get_style() {
        return getStringValue("style");
    }

    public String getDefault_style(String defaultVal) {
        return getStringValue("style", defaultVal);
    }

    public boolean contains_style(String value) {
        return containsValue("style", value);
    }

}
