
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Order_emails
    extends Item
{

    public final static String _NAME = "order_emails";
    public final static String MIN_JUR = "min_jur";
    public final static String MIN_PHYS = "min_phys";
    public final static String MIN_POST = "min_post";
    public final static String SELF = "self";
    public final static String POST = "post";
    public final static String KUR = "kur";
    public final static String CUSTOM = "custom";

    private Order_emails(Item item) {
        super(item);
    }

    public static Order_emails get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'order_emails' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Order_emails(item);
    }

    public static Order_emails newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_min_jur(Double value) {
        setValue("min_jur", value);
    }

    public void setUI_min_jur(String value)
        throws Exception
    {
        setValueUI("min_jur", value);
    }

    public Double get_min_jur() {
        return getDoubleValue("min_jur");
    }

    public Double getDefault_min_jur(Double defaultVal) {
        return getDoubleValue("min_jur", defaultVal);
    }

    public boolean contains_min_jur(Double value) {
        return containsValue("min_jur", value);
    }

    public void set_min_phys(Double value) {
        setValue("min_phys", value);
    }

    public void setUI_min_phys(String value)
        throws Exception
    {
        setValueUI("min_phys", value);
    }

    public Double get_min_phys() {
        return getDoubleValue("min_phys");
    }

    public Double getDefault_min_phys(Double defaultVal) {
        return getDoubleValue("min_phys", defaultVal);
    }

    public boolean contains_min_phys(Double value) {
        return containsValue("min_phys", value);
    }

    public void set_min_post(Double value) {
        setValue("min_post", value);
    }

    public void setUI_min_post(String value)
        throws Exception
    {
        setValueUI("min_post", value);
    }

    public Double get_min_post() {
        return getDoubleValue("min_post");
    }

    public Double getDefault_min_post(Double defaultVal) {
        return getDoubleValue("min_post", defaultVal);
    }

    public boolean contains_min_post(Double value) {
        return containsValue("min_post", value);
    }

    public void set_self(String value) {
        setValue("self", value);
    }

    public String get_self() {
        return getStringValue("self");
    }

    public String getDefault_self(String defaultVal) {
        return getStringValue("self", defaultVal);
    }

    public boolean contains_self(String value) {
        return containsValue("self", value);
    }

    public void set_post(String value) {
        setValue("post", value);
    }

    public String get_post() {
        return getStringValue("post");
    }

    public String getDefault_post(String defaultVal) {
        return getStringValue("post", defaultVal);
    }

    public boolean contains_post(String value) {
        return containsValue("post", value);
    }

    public void set_kur(String value) {
        setValue("kur", value);
    }

    public String get_kur() {
        return getStringValue("kur");
    }

    public String getDefault_kur(String defaultVal) {
        return getStringValue("kur", defaultVal);
    }

    public boolean contains_kur(String value) {
        return containsValue("kur", value);
    }

    public void set_custom(String value) {
        setValue("custom", value);
    }

    public String get_custom() {
        return getStringValue("custom");
    }

    public String getDefault_custom(String defaultVal) {
        return getStringValue("custom", defaultVal);
    }

    public boolean contains_custom(String value) {
        return containsValue("custom", value);
    }

}
