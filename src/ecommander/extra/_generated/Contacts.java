
package ecommander.extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Contacts
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "contacts";

    private Contacts(Item item) {
        super(item);
    }

    public static Contacts get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'contacts' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Contacts(item);
    }

    public static Contacts newChild(Item parent) {
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

    public void set_header_pic(File value) {
        setValue("header_pic", value);
    }

    public File get_header_pic() {
        return getFileValue("header_pic", AppContext.getFilesDirPath());
    }

    public boolean contains_header_pic(File value) {
        return containsValue("header_pic", value);
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
