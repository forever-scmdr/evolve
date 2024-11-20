
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Room
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "room";

    private Room(Item item) {
        super(item);
    }

    public static Room get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'room' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Room(item);
    }

    public static Room newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_number(String value) {
        setValue("number", value);
    }

    public String get_number() {
        return getStringValue("number");
    }

    public String getDefault_number(String defaultVal) {
        return getStringValue("number", defaultVal);
    }

    public boolean contains_number(String value) {
        return containsValue("number", value);
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

    public void set_space(Double value) {
        setValue("space", value);
    }

    public void setUI_space(String value)
        throws Exception
    {
        setValueUI("space", value);
    }

    public Double get_space() {
        return getDoubleValue("space");
    }

    public Double getDefault_space(Double defaultVal) {
        return getDoubleValue("space", defaultVal);
    }

    public boolean contains_space(Double value) {
        return containsValue("space", value);
    }

    public void set_price(Double value) {
        setValue("price", value);
    }

    public void setUI_price(String value)
        throws Exception
    {
        setValueUI("price", value);
    }

    public Double get_price() {
        return getDoubleValue("price");
    }

    public Double getDefault_price(Double defaultVal) {
        return getDoubleValue("price", defaultVal);
    }

    public boolean contains_price(Double value) {
        return containsValue("price", value);
    }

    public void set_status(String value) {
        setValue("status", value);
    }

    public String get_status() {
        return getStringValue("status");
    }

    public String getDefault_status(String defaultVal) {
        return getStringValue("status", defaultVal);
    }

    public boolean contains_status(String value) {
        return containsValue("status", value);
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

}
