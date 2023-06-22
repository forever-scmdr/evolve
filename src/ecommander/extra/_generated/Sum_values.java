
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Sum_values
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "sum_values";

    private Sum_values(Item item) {
        super(item);
    }

    public static Sum_values get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'sum_values' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Sum_values(item);
    }

    public static Sum_values newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_values(String value) {
        setValue("values", value);
    }

    public String get_values() {
        return getStringValue("values");
    }

    public String getDefault_values(String defaultVal) {
        return getStringValue("values", defaultVal);
    }

    public boolean contains_values(String value) {
        return containsValue("values", value);
    }

}
