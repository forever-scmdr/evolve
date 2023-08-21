
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Product_extra_file
    extends Item
{

    public final static String _NAME = "product_extra_file";
    public final static String NAME = "name";
    public final static String DESC = "desc";
    public final static String SIZE = "size";
    public final static String FILE = "file";

    private Product_extra_file(Item item) {
        super(item);
    }

    public static Product_extra_file get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'product_extra_file' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Product_extra_file(item);
    }

    public static Product_extra_file newChild(Item parent) {
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

    public void set_desc(String value) {
        setValue("desc", value);
    }

    public String get_desc() {
        return getStringValue("desc");
    }

    public String getDefault_desc(String defaultVal) {
        return getStringValue("desc", defaultVal);
    }

    public boolean contains_desc(String value) {
        return containsValue("desc", value);
    }

    public void set_size(String value) {
        setValue("size", value);
    }

    public String get_size() {
        return getStringValue("size");
    }

    public String getDefault_size(String defaultVal) {
        return getStringValue("size", defaultVal);
    }

    public boolean contains_size(String value) {
        return containsValue("size", value);
    }

    public void set_file(File value) {
        setValue("file", value);
    }

    public File get_file() {
        return getFileValue("file", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_file(File value) {
        return containsValue("file", value);
    }

}
