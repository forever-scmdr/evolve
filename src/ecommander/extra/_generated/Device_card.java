
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Device_card
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "device_card";

    private Device_card(Item item) {
        super(item);
    }

    public static Device_card get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'device_card' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Device_card(item);
    }

    public static Device_card newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_type(String value) {
        setValue("type", value);
    }

    public String get_type() {
        return getStringValue("type");
    }

    public String getDefault_type(String defaultVal) {
        return getStringValue("type", defaultVal);
    }

    public boolean contains_type(String value) {
        return containsValue("type", value);
    }

    public void set_main_img(File value) {
        setValue("main_img", value);
    }

    public File get_main_img() {
        return getFileValue("main_img", AppContext.getFilesDirPath());
    }

    public boolean contains_main_img(File value) {
        return containsValue("main_img", value);
    }

}
