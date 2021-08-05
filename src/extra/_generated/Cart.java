
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
    public final static String ZERO_QTY = "zero_qty";
    public final static String CUSTOM_QTY = "custom_qty";
    public final static String SUM = "sum";
    public final static String SIMPLE_SUM = "simple_sum";
    public final static String SUM_DISCOUNT = "sum_discount";
    public final static String MARGIN = "margin";
    public final static String PROCESSED = "processed";
    public final static String CURRENCY = "currency";
    public final static String SELECTED_CURRENCY = "selected_currency";

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

    public void set_zero_qty(Double value) {
        setValue("zero_qty", value);
    }

    public void setUI_zero_qty(String value)
        throws Exception
    {
        setValueUI("zero_qty", value);
    }

    public Double get_zero_qty() {
        return getDoubleValue("zero_qty");
    }

    public Double getDefault_zero_qty(Double defaultVal) {
        return getDoubleValue("zero_qty", defaultVal);
    }

    public boolean contains_zero_qty(Double value) {
        return containsValue("zero_qty", value);
    }

    public void set_custom_qty(Double value) {
        setValue("custom_qty", value);
    }

    public void setUI_custom_qty(String value)
        throws Exception
    {
        setValueUI("custom_qty", value);
    }

    public Double get_custom_qty() {
        return getDoubleValue("custom_qty");
    }

    public Double getDefault_custom_qty(Double defaultVal) {
        return getDoubleValue("custom_qty", defaultVal);
    }

    public boolean contains_custom_qty(Double value) {
        return containsValue("custom_qty", value);
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

    public void set_simple_sum(BigDecimal value) {
        setValue("simple_sum", value);
    }

    public void setUI_simple_sum(String value)
        throws Exception
    {
        setValueUI("simple_sum", value);
    }

    public BigDecimal get_simple_sum() {
        return getDecimalValue("simple_sum");
    }

    public BigDecimal getDefault_simple_sum(BigDecimal defaultVal) {
        return getDecimalValue("simple_sum", defaultVal);
    }

    public boolean contains_simple_sum(BigDecimal value) {
        return containsValue("simple_sum", value);
    }

    public void set_sum_discount(BigDecimal value) {
        setValue("sum_discount", value);
    }

    public void setUI_sum_discount(String value)
        throws Exception
    {
        setValueUI("sum_discount", value);
    }

    public BigDecimal get_sum_discount() {
        return getDecimalValue("sum_discount");
    }

    public BigDecimal getDefault_sum_discount(BigDecimal defaultVal) {
        return getDecimalValue("sum_discount", defaultVal);
    }

    public boolean contains_sum_discount(BigDecimal value) {
        return containsValue("sum_discount", value);
    }

    public void set_margin(BigDecimal value) {
        setValue("margin", value);
    }

    public void setUI_margin(String value)
        throws Exception
    {
        setValueUI("margin", value);
    }

    public BigDecimal get_margin() {
        return getDecimalValue("margin");
    }

    public BigDecimal getDefault_margin(BigDecimal defaultVal) {
        return getDecimalValue("margin", defaultVal);
    }

    public boolean contains_margin(BigDecimal value) {
        return containsValue("margin", value);
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

    public void set_currency(String value) {
        setValue("currency", value);
    }

    public String get_currency() {
        return getStringValue("currency");
    }

    public String getDefault_currency(String defaultVal) {
        return getStringValue("currency", defaultVal);
    }

    public boolean contains_currency(String value) {
        return containsValue("currency", value);
    }

    public void set_selected_currency(String value) {
        setValue("selected_currency", value);
    }

    public String get_selected_currency() {
        return getStringValue("selected_currency");
    }

    public String getDefault_selected_currency(String defaultVal) {
        return getStringValue("selected_currency", defaultVal);
    }

    public boolean contains_selected_currency(String value) {
        return containsValue("selected_currency", value);
    }

}
