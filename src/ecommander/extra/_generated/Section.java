
package ecommander.extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.io.File;

public class Section
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "section";

    private Section(Item item) {
        super(item);
    }

    public static Section get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'section' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Section(item);
    }

    public static Section newChild(Item parent) {
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

    public void set_main_pic(File value) {
        setValue("main_pic", value);
    }

    public File get_main_pic() {
        return getFileValue("main_pic", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_main_pic(File value) {
        return containsValue("main_pic", value);
    }

    public void set_assoc_codes(String value) {
        setValue("assoc_codes", value);
    }

    public String get_assoc_codes() {
        return getStringValue("assoc_codes");
    }

    public String getDefault_assoc_codes(String defaultVal) {
        return getStringValue("assoc_codes", defaultVal);
    }

    public boolean contains_assoc_codes(String value) {
        return containsValue("assoc_codes", value);
    }

}
