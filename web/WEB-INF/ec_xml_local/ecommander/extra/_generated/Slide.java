
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Slide
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "slide";

    private Slide(Item item) {
        super(item);
    }

    public static Slide get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'slide' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Slide(item);
    }

    public static Slide newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_href(String value) {
        setValue("href", value);
    }

    public String get_href() {
        return getStringValue("href");
    }

    public String getDefault_href(String defaultVal) {
        return getStringValue("href", defaultVal);
    }

    public boolean contains_href(String value) {
        return containsValue("href", value);
    }

    public void set_link_text(String value) {
        setValue("link_text", value);
    }

    public String get_link_text() {
        return getStringValue("link_text");
    }

    public String getDefault_link_text(String defaultVal) {
        return getStringValue("link_text", defaultVal);
    }

    public boolean contains_link_text(String value) {
        return containsValue("link_text", value);
    }

    public void set_pic(File value) {
        setValue("pic", value);
    }

    public File get_pic() {
        return getFileValue("pic", AppContext.getFilesDirPath());
    }

    public boolean contains_pic(File value) {
        return containsValue("pic", value);
    }

    public void set_mobile_pic(File value) {
        setValue("mobile_pic", value);
    }

    public File get_mobile_pic() {
        return getFileValue("mobile_pic", AppContext.getFilesDirPath());
    }

    public boolean contains_mobile_pic(File value) {
        return containsValue("mobile_pic", value);
    }

    public void set_small_pic(File value) {
        setValue("small_pic", value);
    }

    public File get_small_pic() {
        return getFileValue("small_pic", AppContext.getFilesDirPath());
    }

    public boolean contains_small_pic(File value) {
        return containsValue("small_pic", value);
    }

}
