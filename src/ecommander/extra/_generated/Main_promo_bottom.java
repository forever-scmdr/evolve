
package ecommander.extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.io.File;

public class Main_promo_bottom
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "main_promo_bottom";

    private Main_promo_bottom(Item item) {
        super(item);
    }

    public static Main_promo_bottom get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'main_promo_bottom' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Main_promo_bottom(item);
    }

    public static Main_promo_bottom newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_text_big(String value) {
        setValue("text_big", value);
    }

    public String get_text_big() {
        return getStringValue("text_big");
    }

    public String getDefault_text_big(String defaultVal) {
        return getStringValue("text_big", defaultVal);
    }

    public boolean contains_text_big(String value) {
        return containsValue("text_big", value);
    }

    public void set_text_small(String value) {
        setValue("text_small", value);
    }

    public String get_text_small() {
        return getStringValue("text_small");
    }

    public String getDefault_text_small(String defaultVal) {
        return getStringValue("text_small", defaultVal);
    }

    public boolean contains_text_small(String value) {
        return containsValue("text_small", value);
    }

    public void set_pic(File value) {
        setValue("pic", value);
    }

    public File get_pic() {
        return getFileValue("pic", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_pic(File value) {
        return containsValue("pic", value);
    }

    public void set_link(String value) {
        setValue("link", value);
    }

    public String get_link() {
        return getStringValue("link");
    }

    public String getDefault_link(String defaultVal) {
        return getStringValue("link", defaultVal);
    }

    public boolean contains_link(String value) {
        return containsValue("link", value);
    }

}
