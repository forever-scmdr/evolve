
package lunacrawler._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Discounts
    extends Item
{

    public final static String _NAME = "discounts";
    public final static String SELF_DELIVERY = "self_delivery";
    public final static String PAY_FIRST = "pay_first";
    public final static String AUTOLIGHT = "autolight";
    public final static String DELIVERY = "delivery";
    public final static String SUM_DISCOUNT = "sum_discount";
    public final static String SUM_MORE = "sum_more";

    private Discounts(Item item) {
        super(item);
    }

    public static Discounts get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'discounts' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Discounts(item);
    }

    public static Discounts newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_self_delivery(Double value) {
        setValue("self_delivery", value);
    }

    public void setUI_self_delivery(String value)
        throws Exception
    {
        setValueUI("self_delivery", value);
    }

    public Double get_self_delivery() {
        return getDoubleValue("self_delivery");
    }

    public Double getDefault_self_delivery(Double defaultVal) {
        return getDoubleValue("self_delivery", defaultVal);
    }

    public boolean contains_self_delivery(Double value) {
        return containsValue("self_delivery", value);
    }

    public void set_pay_first(Double value) {
        setValue("pay_first", value);
    }

    public void setUI_pay_first(String value)
        throws Exception
    {
        setValueUI("pay_first", value);
    }

    public Double get_pay_first() {
        return getDoubleValue("pay_first");
    }

    public Double getDefault_pay_first(Double defaultVal) {
        return getDoubleValue("pay_first", defaultVal);
    }

    public boolean contains_pay_first(Double value) {
        return containsValue("pay_first", value);
    }

    public void set_autolight(Double value) {
        setValue("autolight", value);
    }

    public void setUI_autolight(String value)
        throws Exception
    {
        setValueUI("autolight", value);
    }

    public Double get_autolight() {
        return getDoubleValue("autolight");
    }

    public Double getDefault_autolight(Double defaultVal) {
        return getDoubleValue("autolight", defaultVal);
    }

    public boolean contains_autolight(Double value) {
        return containsValue("autolight", value);
    }

    public void set_delivery(Double value) {
        setValue("delivery", value);
    }

    public void setUI_delivery(String value)
        throws Exception
    {
        setValueUI("delivery", value);
    }

    public Double get_delivery() {
        return getDoubleValue("delivery");
    }

    public Double getDefault_delivery(Double defaultVal) {
        return getDoubleValue("delivery", defaultVal);
    }

    public boolean contains_delivery(Double value) {
        return containsValue("delivery", value);
    }

    public void set_sum_discount(Double value) {
        setValue("sum_discount", value);
    }

    public void setUI_sum_discount(String value)
        throws Exception
    {
        setValueUI("sum_discount", value);
    }

    public Double get_sum_discount() {
        return getDoubleValue("sum_discount");
    }

    public Double getDefault_sum_discount(Double defaultVal) {
        return getDoubleValue("sum_discount", defaultVal);
    }

    public boolean contains_sum_discount(Double value) {
        return containsValue("sum_discount", value);
    }

    public void set_sum_more(BigDecimal value) {
        setValue("sum_more", value);
    }

    public void setUI_sum_more(String value)
        throws Exception
    {
        setValueUI("sum_more", value);
    }

    public BigDecimal get_sum_more() {
        return getDecimalValue("sum_more");
    }

    public BigDecimal getDefault_sum_more(BigDecimal defaultVal) {
        return getDecimalValue("sum_more", defaultVal);
    }

    public boolean contains_sum_more(BigDecimal value) {
        return containsValue("sum_more", value);
    }

}
