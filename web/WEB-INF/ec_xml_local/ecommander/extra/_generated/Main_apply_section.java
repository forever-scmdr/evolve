
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Main_apply_section
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "main_apply_section";

    private Main_apply_section(Item item) {
        super(item);
    }

    public static Main_apply_section get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'main_apply_section' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Main_apply_section(item);
    }

    public static Main_apply_section newChild(Item parent) {
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

    public void set_picture(File value) {
        setValue("picture", value);
    }

    public File get_picture() {
        return getFileValue("picture", AppContext.getFilesDirPath());
    }

    public boolean contains_picture(File value) {
        return containsValue("picture", value);
    }

}
