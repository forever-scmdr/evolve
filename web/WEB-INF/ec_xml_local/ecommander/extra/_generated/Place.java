
package ecommander.extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Place
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "place";

    private Place(Item item) {
        super(item);
    }

    public static Place get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'place' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Place(item);
    }

    public static Place newChild(Item parent) {
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

    public void set_coords(String value) {
        setValue("coords", value);
    }

    public String get_coords() {
        return getStringValue("coords");
    }

    public String getDefault_coords(String defaultVal) {
        return getStringValue("coords", defaultVal);
    }

    public boolean contains_coords(String value) {
        return containsValue("coords", value);
    }

    public void set_body(String value) {
        setValue("body", value);
    }

    public String get_body() {
        return getStringValue("body");
    }

    public String getDefault_body(String defaultVal) {
        return getStringValue("body", defaultVal);
    }

    public boolean contains_body(String value) {
        return containsValue("body", value);
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

    public void set_baloon_img(File value) {
        setValue("baloon_img", value);
    }

    public File get_baloon_img() {
        return getFileValue("baloon_img", AppContext.getFilesDirPath());
    }

    public boolean contains_baloon_img(File value) {
        return containsValue("baloon_img", value);
    }

    public void set_settings(String value) {
        setValue("settings", value);
    }

    public String get_settings() {
        return getStringValue("settings");
    }

    public String getDefault_settings(String defaultVal) {
        return getStringValue("settings", defaultVal);
    }

    public boolean contains_settings(String value) {
        return containsValue("settings", value);
    }

}
