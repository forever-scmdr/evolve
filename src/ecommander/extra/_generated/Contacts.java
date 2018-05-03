
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
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'contacts' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Contacts(item);
    }

    public static Contacts newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_map(String value) {
        setValue("map", value);
    }

    public String get_map() {
        return getStringValue("map");
    }

    public String getDefault_map(String defaultVal) {
        return getStringValue("map", defaultVal);
    }

    public boolean contains_map(String value) {
        return containsValue("map", value);
    }

    public void set_bottom_text(String value) {
        setValue("bottom_text", value);
    }

    public String get_bottom_text() {
        return getStringValue("bottom_text");
    }

    public String getDefault_bottom_text(String defaultVal) {
        return getStringValue("bottom_text", defaultVal);
    }

    public boolean contains_bottom_text(String value) {
        return containsValue("bottom_text", value);
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
