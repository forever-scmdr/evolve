
package ecommander.extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Brand
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "brand";

    private Brand(Item item) {
        super(item);
    }

    public static Brand get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'brand' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Brand(item);
    }

    public static Brand newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_mask(String value) {
        setValue("mask", value);
    }

    public String get_mask() {
        return getStringValue("mask");
    }

    public String getDefault_mask(String defaultVal) {
        return getStringValue("mask", defaultVal);
    }

    public boolean contains_mask(String value) {
        return containsValue("mask", value);
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

    public void set_warranty(File value) {
        setValue("warranty", value);
    }

    public File get_warranty() {
        return getFileValue("warranty", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_warranty(File value) {
        return containsValue("warranty", value);
    }

    public void set_change_status(String value) {
        setValue("change_status", value);
    }

    public String get_change_status() {
        return getStringValue("change_status");
    }

    public String getDefault_change_status(String defaultVal) {
        return getStringValue("change_status", defaultVal);
    }

    public boolean contains_change_status(String value) {
        return containsValue("change_status", value);
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
