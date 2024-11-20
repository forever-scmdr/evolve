
package ecommander.extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Img_doc
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "img_doc";

    private Img_doc(Item item) {
        super(item);
    }

    public static Img_doc get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'img_doc' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Img_doc(item);
    }

    public static Img_doc newChild(Item parent) {
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

    public void set_pic(File value) {
        setValue("pic", value);
    }

    public File get_pic() {
        return getFileValue("pic", AppContext.getFilesDirPath());
    }

    public boolean contains_pic(File value) {
        return containsValue("pic", value);
    }

    public void set_back(File value) {
        setValue("back", value);
    }

    public File get_back() {
        return getFileValue("back", AppContext.getFilesDirPath());
    }

    public boolean contains_back(File value) {
        return containsValue("back", value);
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
