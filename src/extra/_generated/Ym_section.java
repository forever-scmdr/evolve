
package extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Ym_section
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "ym_section";

    private Ym_section(Item item) {
        super(item);
    }

    public static Ym_section get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'ym_section' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Ym_section(item);
    }

    public static Ym_section newChild(Item parent) {
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

    public void set_category_id(String value) {
        setValue("category_id", value);
    }

    public String get_category_id() {
        return getStringValue("category_id");
    }

    public String getDefault_category_id(String defaultVal) {
        return getStringValue("category_id", defaultVal);
    }

    public boolean contains_category_id(String value) {
        return containsValue("category_id", value);
    }

    public void set_parent_id(String value) {
        setValue("parent_id", value);
    }

    public String get_parent_id() {
        return getStringValue("parent_id");
    }

    public String getDefault_parent_id(String defaultVal) {
        return getStringValue("parent_id", defaultVal);
    }

    public boolean contains_parent_id(String value) {
        return containsValue("parent_id", value);
    }

    public void set_main_pic(File value) {
        setValue("main_pic", value);
    }

    public File get_main_pic() {
        return getFileValue("main_pic", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_main_pic(File value) {
        return containsValue("main_pic", value);
    }

}
