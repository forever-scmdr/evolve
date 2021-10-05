
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Shop
    extends Item
{

    public final static String _NAME = "shop";
    public final static String NAME = "name";
    public final static String Q = "q";
    public final static String ASSEMBLY_DATE = "assembly_date";
    public final static String DELIVERY_DATE = "delivery_date";
    public final static String DELIVERY_STRING = "delivery_string";

    private Shop(Item item) {
        super(item);
    }

    public static Shop get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'shop' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Shop(item);
    }

    public static Shop newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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

    public void set_q(Double value) {
        setValue("q", value);
    }

    public void setUI_q(String value)
        throws Exception
    {
        setValueUI("q", value);
    }

    public Double get_q() {
        return getDoubleValue("q");
    }

    public Double getDefault_q(Double defaultVal) {
        return getDoubleValue("q", defaultVal);
    }

    public boolean contains_q(Double value) {
        return containsValue("q", value);
    }

    public void set_assembly_date(Long value) {
        setValue("assembly_date", value);
    }

    public void setUI_assembly_date(String value)
        throws Exception
    {
        setValueUI("assembly_date", value);
    }

    public Long get_assembly_date() {
        return getLongValue("assembly_date");
    }

    public Long getDefault_assembly_date(Long defaultVal) {
        return getLongValue("assembly_date", defaultVal);
    }

    public boolean contains_assembly_date(Long value) {
        return containsValue("assembly_date", value);
    }

    public void set_delivery_date(Long value) {
        setValue("delivery_date", value);
    }

    public void setUI_delivery_date(String value)
        throws Exception
    {
        setValueUI("delivery_date", value);
    }

    public Long get_delivery_date() {
        return getLongValue("delivery_date");
    }

    public Long getDefault_delivery_date(Long defaultVal) {
        return getLongValue("delivery_date", defaultVal);
    }

    public boolean contains_delivery_date(Long value) {
        return containsValue("delivery_date", value);
    }

    public void set_delivery_string(String value) {
        setValue("delivery_string", value);
    }

    public String get_delivery_string() {
        return getStringValue("delivery_string");
    }

    public String getDefault_delivery_string(String defaultVal) {
        return getStringValue("delivery_string", defaultVal);
    }

    public boolean contains_delivery_string(String value) {
        return containsValue("delivery_string", value);
    }

}
