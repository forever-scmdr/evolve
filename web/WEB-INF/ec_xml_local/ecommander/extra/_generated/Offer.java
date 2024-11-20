
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Offer
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "offer";

    private Offer(Item item) {
        super(item);
    }

    public static Offer get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'offer' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Offer(item);
    }

    public static Offer newChild(Item parent) {
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

    public void set_price(String value) {
        setValue("price", value);
    }

    public String get_price() {
        return getStringValue("price");
    }

    public String getDefault_price(String defaultVal) {
        return getStringValue("price", defaultVal);
    }

    public boolean contains_price(String value) {
        return containsValue("price", value);
    }

    public void set_short(String value) {
        setValue("short", value);
    }

    public String get_short() {
        return getStringValue("short");
    }

    public String getDefault_short(String defaultVal) {
        return getStringValue("short", defaultVal);
    }

    public boolean contains_short(String value) {
        return containsValue("short", value);
    }

    public void set_pic_small(File value) {
        setValue("pic_small", value);
    }

    public File get_pic_small() {
        return getFileValue("pic_small", AppContext.getFilesDirPath());
    }

    public boolean contains_pic_small(File value) {
        return containsValue("pic_small", value);
    }

    public void set_pic_big(File value) {
        setValue("pic_big", value);
    }

    public File get_pic_big() {
        return getFileValue("pic_big", AppContext.getFilesDirPath());
    }

    public boolean contains_pic_big(File value) {
        return containsValue("pic_big", value);
    }

}
