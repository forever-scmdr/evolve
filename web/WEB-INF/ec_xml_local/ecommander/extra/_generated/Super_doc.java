
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Super_doc
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "super_doc";

    private Super_doc(Item item) {
        super(item);
    }

    public static Super_doc get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'super_doc' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Super_doc(item);
    }

    public static Super_doc newChild(Item parent) {
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

    public void set_icon(File value) {
        setValue("icon", value);
    }

    public File get_icon() {
        return getFileValue("icon", AppContext.getFilesDirPath());
    }

    public boolean contains_icon(File value) {
        return containsValue("icon", value);
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

    public void set_scope(String value) {
        setValue("scope", value);
    }

    public String get_scope() {
        return getStringValue("scope");
    }

    public String getDefault_scope(String defaultVal) {
        return getStringValue("scope", defaultVal);
    }

    public boolean contains_scope(String value) {
        return containsValue("scope", value);
    }

}
