
package ecommander.extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Frame
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "frame";

    private Frame(Item item) {
        super(item);
    }

    public static Frame get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'frame' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Frame(item);
    }

    public static Frame newChild(Item parent) {
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

    public void set_img(File value) {
        setValue("img", value);
    }

    public File get_img() {
        return getFileValue("img", AppContext.getFilesDirPath());
    }

    public boolean contains_img(File value) {
        return containsValue("img", value);
    }

    public void add_picture(File value) {
        setValue("picture", value);
    }

    public List<File> getAll_picture() {
        return getFileValues("picture", AppContext.getFilesDirPath());
    }

    public void remove_picture(File value) {
        removeEqualValue("picture", value);
    }

    public boolean contains_picture(File value) {
        return containsValue("picture", value);
    }

}
