
package ecommander.extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.io.File;

public class Banner
    extends Item
{

    public final static String _NAME = "banner";
    public final static String BACKGROUND = "background";
    public final static String HEADER = "header";
    public final static String TEXT = "text";
    public final static String IMAGE_PIC = "image_pic";
    public final static String IMAGE_CODE = "image_code";
    public final static String LINK = "link";
    public final static String EXTRA_STYLE = "extra_style";

    private Banner(Item item) {
        super(item);
    }

    public static Banner get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'banner' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Banner(item);
    }

    public static Banner newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_background(String value) {
        setValue("background", value);
    }

    public String get_background() {
        return getStringValue("background");
    }

    public String getDefault_background(String defaultVal) {
        return getStringValue("background", defaultVal);
    }

    public boolean contains_background(String value) {
        return containsValue("background", value);
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

    public void set_image_pic(File value) {
        setValue("image_pic", value);
    }

    public File get_image_pic() {
        return getFileValue("image_pic", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_image_pic(File value) {
        return containsValue("image_pic", value);
    }

    public void set_image_code(String value) {
        setValue("image_code", value);
    }

    public String get_image_code() {
        return getStringValue("image_code");
    }

    public String getDefault_image_code(String defaultVal) {
        return getStringValue("image_code", defaultVal);
    }

    public boolean contains_image_code(String value) {
        return containsValue("image_code", value);
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

}
