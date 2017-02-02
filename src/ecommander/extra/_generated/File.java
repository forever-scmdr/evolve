
package ecommander.extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class File
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "file";

    private File(Item item) {
        super(item);
    }

    public static File get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'file' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new File(item);
    }

    public static File newChild(Item parent) {
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

    public void set_file(java.io.File value) {
        setValue("file", value);
    }

    public java.io.File get_file() {
        return getFileValue("file", AppContext.getFilesDirPath());
    }

    public boolean contains_file(java.io.File value) {
        return containsValue("file", value);
    }

}
