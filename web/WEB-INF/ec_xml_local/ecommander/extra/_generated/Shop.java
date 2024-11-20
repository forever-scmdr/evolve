
package ecommander.extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Shop
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "shop";

    private Shop(Item item) {
        super(item);
    }

    public static Shop get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'shop' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Shop(item);
    }

    public static Shop newChild(Item parent) {
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

    public void set_logo(File value) {
        setValue("logo", value);
    }

    public File get_logo() {
        return getFileValue("logo", AppContext.getFilesDirPath());
    }

    public boolean contains_logo(File value) {
        return containsValue("logo", value);
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

    public void set_head(String value) {
        setValue("head", value);
    }

    public String get_head() {
        return getStringValue("head");
    }

    public String getDefault_head(String defaultVal) {
        return getStringValue("head", defaultVal);
    }

    public boolean contains_head(String value) {
        return containsValue("head", value);
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

    public void set_site(String value) {
        setValue("site", value);
    }

    public String get_site() {
        return getStringValue("site");
    }

    public String getDefault_site(String defaultVal) {
        return getStringValue("site", defaultVal);
    }

    public boolean contains_site(String value) {
        return containsValue("site", value);
    }

    public void set_phones(String value) {
        setValue("phones", value);
    }

    public String get_phones() {
        return getStringValue("phones");
    }

    public String getDefault_phones(String defaultVal) {
        return getStringValue("phones", defaultVal);
    }

    public boolean contains_phones(String value) {
        return containsValue("phones", value);
    }

    public void set_work_time(String value) {
        setValue("work_time", value);
    }

    public String get_work_time() {
        return getStringValue("work_time");
    }

    public String getDefault_work_time(String defaultVal) {
        return getStringValue("work_time", defaultVal);
    }

    public boolean contains_work_time(String value) {
        return containsValue("work_time", value);
    }

    public void set_get_there(String value) {
        setValue("get_there", value);
    }

    public String get_get_there() {
        return getStringValue("get_there");
    }

    public String getDefault_get_there(String defaultVal) {
        return getStringValue("get_there", defaultVal);
    }

    public boolean contains_get_there(String value) {
        return containsValue("get_there", value);
    }

    public void add_room(Long value) {
        setValue("room", value);
    }

    public void addUI_room(String value)
        throws Exception
    {
        setValueUI("room", value);
    }

    public List<Long> getAll_room() {
        return getLongValues("room");
    }

    public void remove_room(Long value) {
        removeEqualValue("room", value);
    }

    public boolean contains_room(Long value) {
        return containsValue("room", value);
    }

}
