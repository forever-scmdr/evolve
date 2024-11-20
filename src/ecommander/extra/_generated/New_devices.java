
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class New_devices
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "new_devices";

    private New_devices(Item item) {
        super(item);
    }

    public static New_devices get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'new_devices' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new New_devices(item);
    }

    public static New_devices newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_string(String value) {
        setValue("string", value);
    }

    public String get_string() {
        return getStringValue("string");
    }

    public String getDefault_string(String defaultVal) {
        return getStringValue("string", defaultVal);
    }

    public boolean contains_string(String value) {
        return containsValue("string", value);
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

    public void set_ilink(String value) {
        setValue("ilink", value);
    }

    public String get_ilink() {
        return getStringValue("ilink");
    }

    public String getDefault_ilink(String defaultVal) {
        return getStringValue("ilink", defaultVal);
    }

    public boolean contains_ilink(String value) {
        return containsValue("ilink", value);
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

}
