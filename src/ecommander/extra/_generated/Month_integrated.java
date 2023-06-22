
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Month_integrated
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "month_integrated";

    private Month_integrated(Item item) {
        super(item);
    }

    public static Month_integrated get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'month_integrated' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Month_integrated(item);
    }

    public static Month_integrated newChild(Item parent) {
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

    public void set_table(String value) {
        setValue("table", value);
    }

    public String get_table() {
        return getStringValue("table");
    }

    public String getDefault_table(String defaultVal) {
        return getStringValue("table", defaultVal);
    }

    public boolean contains_table(String value) {
        return containsValue("table", value);
    }

}
