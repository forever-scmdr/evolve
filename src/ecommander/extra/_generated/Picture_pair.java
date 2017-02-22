
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Picture_pair
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "picture_pair";

    private Picture_pair(Item item) {
        super(item);
    }

    public static Picture_pair get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'picture_pair' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Picture_pair(item);
    }

    public static Picture_pair newChild(Item parent) {
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

    public void set_small(File value) {
        setValue("small", value);
    }

    public File get_small() {
        return getFileValue("small", AppContext.getFilesDirPath());
    }

    public boolean contains_small(File value) {
        return containsValue("small", value);
    }

    public void set_big(File value) {
        setValue("big", value);
    }

    public File get_big() {
        return getFileValue("big", AppContext.getFilesDirPath());
    }

    public boolean contains_big(File value) {
        return containsValue("big", value);
    }

}
