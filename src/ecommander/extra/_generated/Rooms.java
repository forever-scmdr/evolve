
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Rooms
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "rooms";

    private Rooms(Item item) {
        super(item);
    }

    public static Rooms get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'rooms' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Rooms(item);
    }

    public static Rooms newChild(Item parent) {
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

    public void set_header_pic(File value) {
        setValue("header_pic", value);
    }

    public File get_header_pic() {
        return getFileValue("header_pic", AppContext.getFilesDirPath());
    }

    public boolean contains_header_pic(File value) {
        return containsValue("header_pic", value);
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

    public void set_extra_quotient(Double value) {
        setValue("extra_quotient", value);
    }

    public void setUI_extra_quotient(String value)
        throws Exception
    {
        setValueUI("extra_quotient", value);
    }

    public Double get_extra_quotient() {
        return getDoubleValue("extra_quotient");
    }

    public Double getDefault_extra_quotient(Double defaultVal) {
        return getDoubleValue("extra_quotient", defaultVal);
    }

    public boolean contains_extra_quotient(Double value) {
        return containsValue("extra_quotient", value);
    }

    public void set_first_start(Long value) {
        setValue("first_start", value);
    }

    public void setUI_first_start(String value)
        throws Exception
    {
        setValueUI("first_start", value);
    }

    public Long get_first_start() {
        return getLongValue("first_start");
    }

    public Long getDefault_first_start(Long defaultVal) {
        return getLongValue("first_start", defaultVal);
    }

    public boolean contains_first_start(Long value) {
        return containsValue("first_start", value);
    }

    public void set_first_end(Long value) {
        setValue("first_end", value);
    }

    public void setUI_first_end(String value)
        throws Exception
    {
        setValueUI("first_end", value);
    }

    public Long get_first_end() {
        return getLongValue("first_end");
    }

    public Long getDefault_first_end(Long defaultVal) {
        return getLongValue("first_end", defaultVal);
    }

    public boolean contains_first_end(Long value) {
        return containsValue("first_end", value);
    }

    public void set_second_start(Long value) {
        setValue("second_start", value);
    }

    public void setUI_second_start(String value)
        throws Exception
    {
        setValueUI("second_start", value);
    }

    public Long get_second_start() {
        return getLongValue("second_start");
    }

    public Long getDefault_second_start(Long defaultVal) {
        return getLongValue("second_start", defaultVal);
    }

    public boolean contains_second_start(Long value) {
        return containsValue("second_start", value);
    }

    public void set_second_end(Long value) {
        setValue("second_end", value);
    }

    public void setUI_second_end(String value)
        throws Exception
    {
        setValueUI("second_end", value);
    }

    public Long get_second_end() {
        return getLongValue("second_end");
    }

    public Long getDefault_second_end(Long defaultVal) {
        return getLongValue("second_end", defaultVal);
    }

    public boolean contains_second_end(Long value) {
        return containsValue("second_end", value);
    }

    public void set_third_start(Long value) {
        setValue("third_start", value);
    }

    public void setUI_third_start(String value)
        throws Exception
    {
        setValueUI("third_start", value);
    }

    public Long get_third_start() {
        return getLongValue("third_start");
    }

    public Long getDefault_third_start(Long defaultVal) {
        return getLongValue("third_start", defaultVal);
    }

    public boolean contains_third_start(Long value) {
        return containsValue("third_start", value);
    }

}
