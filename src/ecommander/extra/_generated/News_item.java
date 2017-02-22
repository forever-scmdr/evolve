
package ecommander.extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class News_item
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "news_item";

    private News_item(Item item) {
        super(item);
    }

    public static News_item get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'news_item' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new News_item(item);
    }

    public static News_item newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_header(String value) {
        setValue("header", value);
    }

    public String get_header() {
        return getStringValue("header");
    }

    public String getDefault_header(String defaultVal) {
        return getStringValue("header", defaultVal);
    }

    public boolean contains_header(String value) {
        return containsValue("header", value);
    }

    public void set_date(Long value) {
        setValue("date", value);
    }

    public void setUI_date(String value)
        throws Exception
    {
        setValueUI("date", value);
    }

    public Long get_date() {
        return getLongValue("date");
    }

    public Long getDefault_date(Long defaultVal) {
        return getLongValue("date", defaultVal);
    }

    public boolean contains_date(Long value) {
        return containsValue("date", value);
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

}
