
package ecommander.extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Text_part
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "text_part";

    private Text_part(Item item) {
        super(item);
    }

    public static Text_part get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'text_part' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Text_part(item);
    }

    public static Text_part newChild(Item parent) {
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

    public void add_file(File value) {
        setValue("file", value);
    }

    public List<File> getAll_file() {
        return getFileValues("file", AppContext.getFilesDirPath());
    }

    public void remove_file(File value) {
        removeEqualValue("file", value);
    }

    public boolean contains_file(File value) {
        return containsValue("file", value);
    }

}
