
package lunacrawler._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Price_interval
    extends Item
{

    public final static String _NAME = "price_interval";
    public final static String MIN = "min";
    public final static String MAX = "max";
    public final static String QUOTIENT = "quotient";

    private Price_interval(Item item) {
        super(item);
    }

    public static Price_interval get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'price_interval' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Price_interval(item);
    }

    public static Price_interval newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_min(BigDecimal value) {
        setValue("min", value);
    }

    public void setUI_min(String value)
        throws Exception
    {
        setValueUI("min", value);
    }

    public BigDecimal get_min() {
        return getDecimalValue("min");
    }

    public BigDecimal getDefault_min(BigDecimal defaultVal) {
        return getDecimalValue("min", defaultVal);
    }

    public boolean contains_min(BigDecimal value) {
        return containsValue("min", value);
    }

    public void set_max(BigDecimal value) {
        setValue("max", value);
    }

    public void setUI_max(String value)
        throws Exception
    {
        setValueUI("max", value);
    }

    public BigDecimal get_max() {
        return getDecimalValue("max");
    }

    public BigDecimal getDefault_max(BigDecimal defaultVal) {
        return getDecimalValue("max", defaultVal);
    }

    public boolean contains_max(BigDecimal value) {
        return containsValue("max", value);
    }

    public void set_quotient(BigDecimal value) {
        setValue("quotient", value);
    }

    public void setUI_quotient(String value)
        throws Exception
    {
        setValueUI("quotient", value);
    }

    public BigDecimal get_quotient() {
        return getDecimalValue("quotient");
    }

    public BigDecimal getDefault_quotient(BigDecimal defaultVal) {
        return getDecimalValue("quotient", defaultVal);
    }

    public boolean contains_quotient(BigDecimal value) {
        return containsValue("quotient", value);
    }

}
