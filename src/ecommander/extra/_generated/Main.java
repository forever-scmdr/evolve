
package ecommander.extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Main
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "main";

    private Main(Item item) {
        super(item);
    }

    public static Main get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'main' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Main(item);
    }

    public static Main newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_pic_1(File value) {
        setValue("pic_1", value);
    }

    public File get_pic_1() {
        return getFileValue("pic_1", AppContext.getFilesDirPath());
    }

    public boolean contains_pic_1(File value) {
        return containsValue("pic_1", value);
    }

    public void set_link_1(String value) {
        setValue("link_1", value);
    }

    public String get_link_1() {
        return getStringValue("link_1");
    }

    public String getDefault_link_1(String defaultVal) {
        return getStringValue("link_1", defaultVal);
    }

    public boolean contains_link_1(String value) {
        return containsValue("link_1", value);
    }

    public void set_pic_2(File value) {
        setValue("pic_2", value);
    }

    public File get_pic_2() {
        return getFileValue("pic_2", AppContext.getFilesDirPath());
    }

    public boolean contains_pic_2(File value) {
        return containsValue("pic_2", value);
    }

    public void set_link_2(String value) {
        setValue("link_2", value);
    }

    public String get_link_2() {
        return getStringValue("link_2");
    }

    public String getDefault_link_2(String defaultVal) {
        return getStringValue("link_2", defaultVal);
    }

    public boolean contains_link_2(String value) {
        return containsValue("link_2", value);
    }

    public void set_pic_3(File value) {
        setValue("pic_3", value);
    }

    public File get_pic_3() {
        return getFileValue("pic_3", AppContext.getFilesDirPath());
    }

    public boolean contains_pic_3(File value) {
        return containsValue("pic_3", value);
    }

    public void set_link_3(String value) {
        setValue("link_3", value);
    }

    public String get_link_3() {
        return getStringValue("link_3");
    }

    public String getDefault_link_3(String defaultVal) {
        return getStringValue("link_3", defaultVal);
    }

    public boolean contains_link_3(String value) {
        return containsValue("link_3", value);
    }

    public void set_banner(String value) {
        setValue("banner", value);
    }

    public String get_banner() {
        return getStringValue("banner");
    }

    public String getDefault_banner(String defaultVal) {
        return getStringValue("banner", defaultVal);
    }

    public boolean contains_banner(String value) {
        return containsValue("banner", value);
    }

    public void add_file(File value) {
        setValue("file", value);
    }

    public List<File> getAll_file() {
        return getFileValues("file", AppContext.getFilesDirPath());
    }

    public void remove_file(File value) {
        removeEqualValue("file", value);
    }

    public boolean contains_file(File value) {
        return containsValue("file", value);
    }

}
