
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Stock
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "stock";

    private Stock(Item item) {
        super(item);
    }

    public static Stock get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'stock' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Stock(item);
    }

    public static Stock newChild(Item parent) {
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

    public void set_start(Long value) {
        setValue("start", value);
    }

    public void setUI_start(String value)
        throws Exception
    {
        setValueUI("start", value);
    }

    public Long get_start() {
        return getLongValue("start");
    }

    public Long getDefault_start(Long defaultVal) {
        return getLongValue("start", defaultVal);
    }

    public boolean contains_start(Long value) {
        return containsValue("start", value);
    }

    public void set_end(Long value) {
        setValue("end", value);
    }

    public void setUI_end(String value)
        throws Exception
    {
        setValueUI("end", value);
    }

    public Long get_end() {
        return getLongValue("end");
    }

    public Long getDefault_end(Long defaultVal) {
        return getLongValue("end", defaultVal);
    }

    public boolean contains_end(Long value) {
        return containsValue("end", value);
    }

    public void set_pic(File value) {
        setValue("pic", value);
    }

    public File get_pic() {
        return getFileValue("pic", AppContext.getFilesDirPath());
    }

    public boolean contains_pic(File value) {
        return containsValue("pic", value);
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

}
