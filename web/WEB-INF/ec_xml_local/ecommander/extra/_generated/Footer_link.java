
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Footer_link
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "footer_link";

    private Footer_link(Item item) {
        super(item);
    }

    public static Footer_link get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'footer_link' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Footer_link(item);
    }

    public static Footer_link newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_download(String value) {
        setValue("download", value);
    }

    public String get_download() {
        return getStringValue("download");
    }

    public String getDefault_download(String defaultVal) {
        return getStringValue("download", defaultVal);
    }

    public boolean contains_download(String value) {
        return containsValue("download", value);
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

    public void set_file(File value) {
        setValue("file", value);
    }

    public File get_file() {
        return getFileValue("file", AppContext.getFilesDirPath());
    }

    public boolean contains_file(File value) {
        return containsValue("file", value);
    }

}
