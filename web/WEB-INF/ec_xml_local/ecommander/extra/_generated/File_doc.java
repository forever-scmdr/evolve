
package ecommander.extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class File_doc
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "file_doc";

    private File_doc(Item item) {
        super(item);
    }

    public static File_doc get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'file_doc' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new File_doc(item);
    }

    public static File_doc newChild(Item parent) {
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

    public void add_scope(String value) {
        setValue("scope", value);
    }

    public List<String> getAll_scope() {
        return getStringValues("scope");
    }

    public void remove_scope(String value) {
        removeEqualValue("scope", value);
    }

    public boolean contains_scope(String value) {
        return containsValue("scope", value);
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

    public void set_small(File value) {
        setValue("small", value);
    }

    public File get_small() {
        return getFileValue("small", AppContext.getFilesDirPath());
    }

    public boolean contains_small(File value) {
        return containsValue("small", value);
    }

}
