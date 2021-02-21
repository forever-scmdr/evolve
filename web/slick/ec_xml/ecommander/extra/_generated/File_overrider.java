
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class File_overrider
    extends Item
{

    public final static String _NAME = "file_overrider";
    public final static String NAME = "name";
    public final static String SRC = "src";
    public final static String FILE_CONTENT = "file_content";

    private File_overrider(Item item) {
        super(item);
    }

    public static File_overrider get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'file_overrider' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new File_overrider(item);
    }

    public static File_overrider newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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

    public void set_src(String value) {
        setValue("src", value);
    }

    public String get_src() {
        return getStringValue("src");
    }

    public String getDefault_src(String defaultVal) {
        return getStringValue("src", defaultVal);
    }

    public boolean contains_src(String value) {
        return containsValue("src", value);
    }

    public void set_file_content(File value) {
        setValue("file_content", value);
    }

    public File get_file_content() {
        return getFileValue("file_content", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_file_content(File value) {
        return containsValue("file_content", value);
    }

}
