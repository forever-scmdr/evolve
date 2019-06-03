
package ecommander.extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.io.File;

public class Section
    extends Item
{

    public final static String _NAME = "section";
    public final static String NAME = "name";
    public final static String ICON = "icon";
    public final static String SHOW_SUBS = "show_subs";
    public final static String SUB_VIEW = "sub_view";
    public final static String SHOW_DEVICES = "show_devices";
    public final static String CATEGORY_ID = "category_id";
    public final static String PARENT_ID = "parent_id";
    public final static String MAIN_PIC = "main_pic";
    public final static String PARAMS_FILTER = "params_filter";

    private Section(Item item) {
        super(item);
    }

    public static Section get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'section' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Section(item);
    }

    public static Section newChild(Item parent) {
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

    public void set_icon(File value) {
        setValue("icon", value);
    }

    public File get_icon() {
        return getFileValue("icon", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_icon(File value) {
        return containsValue("icon", value);
    }

    public void set_show_subs(Byte value) {
        setValue("show_subs", value);
    }

    public void setUI_show_subs(String value)
        throws Exception
    {
        setValueUI("show_subs", value);
    }

    public Byte get_show_subs() {
        return getByteValue("show_subs");
    }

    public Byte getDefault_show_subs(Byte defaultVal) {
        return getByteValue("show_subs", defaultVal);
    }

    public boolean contains_show_subs(Byte value) {
        return containsValue("show_subs", value);
    }

    public void set_sub_view(String value) {
        setValue("sub_view", value);
    }

    public String get_sub_view() {
        return getStringValue("sub_view");
    }

    public String getDefault_sub_view(String defaultVal) {
        return getStringValue("sub_view", defaultVal);
    }

    public boolean contains_sub_view(String value) {
        return containsValue("sub_view", value);
    }

    public void set_show_devices(Byte value) {
        setValue("show_devices", value);
    }

    public void setUI_show_devices(String value)
        throws Exception
    {
        setValueUI("show_devices", value);
    }

    public Byte get_show_devices() {
        return getByteValue("show_devices");
    }

    public Byte getDefault_show_devices(Byte defaultVal) {
        return getByteValue("show_devices", defaultVal);
    }

    public boolean contains_show_devices(Byte value) {
        return containsValue("show_devices", value);
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

}
