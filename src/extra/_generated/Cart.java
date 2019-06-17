
package extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Cart
    extends Item
{

    public final static String _NAME = "cart";
    public final static String ORDER_NUM = "order_num";
    public final static String QTY = "qty";
    public final static String SUM = "sum";
    public final static String SUM_RUB = "sum_RUB";
    public final static String SUM_USD = "sum_USD";
    public final static String SUM_EUR = "sum_EUR";
    public final static String PROCESSED = "processed";

    private Cart(Item item) {
        super(item);
    }

    public static Cart get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'cart' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Cart(item);
    }

    public static Cart newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_order_num(String value) {
        setValue("order_num", value);
    }

    public String get_order_num() {
        return getStringValue("order_num");
    }

    public String getDefault_order_num(String defaultVal) {
        return getStringValue("order_num", defaultVal);
    }

    public boolean contains_order_num(String value) {
        return containsValue("order_num", value);
    }

    public void set_qty(Double value) {
        setValue("qty", value);
    }

    public void setUI_qty(String value)
        throws Exception
    {
        setValueUI("qty", value);
    }

    public Double get_qty() {
        return getDoubleValue("qty");
    }

    public Double getDefault_qty(Double defaultVal) {
        return getDoubleValue("qty", defaultVal);
    }

    public boolean contains_qty(Double value) {
        return containsValue("qty", value);
    }

    public void set_sum(BigDecimal value) {
        setValue("sum", value);
    }

    public void setUI_sum(String value)
        throws Exception
    {
        setValueUI("sum", value);
    }

    public BigDecimal get_sum() {
        return getDecimalValue("sum");
    }

    public BigDecimal getDefault_sum(BigDecimal defaultVal) {
        return getDecimalValue("sum", defaultVal);
    }

    public boolean contains_sum(BigDecimal value) {
        return containsValue("sum", value);
    }

    public void set_sum_RUB(BigDecimal value) {
        setValue("sum_RUB", value);
    }

    public void setUI_sum_RUB(String value)
        throws Exception
    {
        setValueUI("sum_RUB", value);
    }

    public BigDecimal get_sum_RUB() {
        return getDecimalValue("sum_RUB");
    }

    public BigDecimal getDefault_sum_RUB(BigDecimal defaultVal) {
        return getDecimalValue("sum_RUB", defaultVal);
    }

    public boolean contains_sum_RUB(BigDecimal value) {
        return containsValue("sum_RUB", value);
    }

    public void set_sum_USD(BigDecimal value) {
        setValue("sum_USD", value);
    }

    public void setUI_sum_USD(String value)
        throws Exception
    {
        setValueUI("sum_USD", value);
    }

    public BigDecimal get_sum_USD() {
        return getDecimalValue("sum_USD");
    }

    public BigDecimal getDefault_sum_USD(BigDecimal defaultVal) {
        return getDecimalValue("sum_USD", defaultVal);
    }

    public boolean contains_sum_USD(BigDecimal value) {
        return containsValue("sum_USD", value);
    }

    public void set_sum_EUR(BigDecimal value) {
        setValue("sum_EUR", value);
    }

    public void setUI_sum_EUR(String value)
        throws Exception
    {
        setValueUI("sum_EUR", value);
    }

    public BigDecimal get_sum_EUR() {
        return getDecimalValue("sum_EUR");
    }

    public BigDecimal getDefault_sum_EUR(BigDecimal defaultVal) {
        return getDecimalValue("sum_EUR", defaultVal);
    }

    public boolean contains_sum_EUR(BigDecimal value) {
        return containsValue("sum_EUR", value);
    }

    public void set_processed(Byte value) {
        setValue("processed", value);
    }

    public void setUI_processed(String value)
        throws Exception
    {
        setValueUI("processed", value);
    }

    public Byte get_processed() {
        return getByteValue("processed");
    }

    public Byte getDefault_processed(Byte defaultVal) {
        return getByteValue("processed", defaultVal);
    }

    public boolean contains_processed(Byte value) {
        return containsValue("processed", value);
    }

}
