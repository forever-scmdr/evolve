
package extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Bought
    extends Item
{

    public final static String _NAME = "bought";
    public final static String NAME = "name";
    public final static String CODE = "code";
    public final static String QTY = "qty";
    public final static String QTY_AVAIL = "qty_avail";
    public final static String QTY_TOTAL = "qty_total";
    public final static String QTY_ZERO = "qty_zero";
    public final static String PRICE = "price";
    public final static String NOT_AVAILABLE = "not_available";
    public final static String SUM = "sum";
    public final static String LIMIT_1 = "limit_1";
    public final static String LIMIT_2 = "limit_2";
    public final static String DISCOUNT_1 = "discount_1";
    public final static String DISCOUNT_2 = "discount_2";
    public final static String TYPE = "type";

    private Bought(Item item) {
        super(item);
    }

    public static Bought get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'bought' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Bought(item);
    }

    public static Bought newChild(Item parent) {
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

    public void set_code(String value) {
        setValue("code", value);
    }

    public String get_code() {
        return getStringValue("code");
    }

    public String getDefault_code(String defaultVal) {
        return getStringValue("code", defaultVal);
    }

    public boolean contains_code(String value) {
        return containsValue("code", value);
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

    public void set_qty_avail(Double value) {
        setValue("qty_avail", value);
    }

    public void setUI_qty_avail(String value)
        throws Exception
    {
        setValueUI("qty_avail", value);
    }

    public Double get_qty_avail() {
        return getDoubleValue("qty_avail");
    }

    public Double getDefault_qty_avail(Double defaultVal) {
        return getDoubleValue("qty_avail", defaultVal);
    }

    public boolean contains_qty_avail(Double value) {
        return containsValue("qty_avail", value);
    }

    public void set_qty_total(Double value) {
        setValue("qty_total", value);
    }

    public void setUI_qty_total(String value)
        throws Exception
    {
        setValueUI("qty_total", value);
    }

    public Double get_qty_total() {
        return getDoubleValue("qty_total");
    }

    public Double getDefault_qty_total(Double defaultVal) {
        return getDoubleValue("qty_total", defaultVal);
    }

    public boolean contains_qty_total(Double value) {
        return containsValue("qty_total", value);
    }

    public void set_qty_zero(Double value) {
        setValue("qty_zero", value);
    }

    public void setUI_qty_zero(String value)
        throws Exception
    {
        setValueUI("qty_zero", value);
    }

    public Double get_qty_zero() {
        return getDoubleValue("qty_zero");
    }

    public Double getDefault_qty_zero(Double defaultVal) {
        return getDoubleValue("qty_zero", defaultVal);
    }

    public boolean contains_qty_zero(Double value) {
        return containsValue("qty_zero", value);
    }

    public void set_price(BigDecimal value) {
        setValue("price", value);
    }

    public void setUI_price(String value)
        throws Exception
    {
        setValueUI("price", value);
    }

    public BigDecimal get_price() {
        return getDecimalValue("price");
    }

    public BigDecimal getDefault_price(BigDecimal defaultVal) {
        return getDecimalValue("price", defaultVal);
    }

    public boolean contains_price(BigDecimal value) {
        return containsValue("price", value);
    }

    public void set_not_available(Byte value) {
        setValue("not_available", value);
    }

    public void setUI_not_available(String value)
        throws Exception
    {
        setValueUI("not_available", value);
    }

    public Byte get_not_available() {
        return getByteValue("not_available");
    }

    public Byte getDefault_not_available(Byte defaultVal) {
        return getByteValue("not_available", defaultVal);
    }

    public boolean contains_not_available(Byte value) {
        return containsValue("not_available", value);
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

    public void set_limit_1(Double value) {
        setValue("limit_1", value);
    }

    public void setUI_limit_1(String value)
        throws Exception
    {
        setValueUI("limit_1", value);
    }

    public Double get_limit_1() {
        return getDoubleValue("limit_1");
    }

    public Double getDefault_limit_1(Double defaultVal) {
        return getDoubleValue("limit_1", defaultVal);
    }

    public boolean contains_limit_1(Double value) {
        return containsValue("limit_1", value);
    }

    public void set_limit_2(Double value) {
        setValue("limit_2", value);
    }

    public void setUI_limit_2(String value)
        throws Exception
    {
        setValueUI("limit_2", value);
    }

    public Double get_limit_2() {
        return getDoubleValue("limit_2");
    }

    public Double getDefault_limit_2(Double defaultVal) {
        return getDoubleValue("limit_2", defaultVal);
    }

    public boolean contains_limit_2(Double value) {
        return containsValue("limit_2", value);
    }

    public void set_discount_1(Integer value) {
        setValue("discount_1", value);
    }

    public void setUI_discount_1(String value)
        throws Exception
    {
        setValueUI("discount_1", value);
    }

    public Integer get_discount_1() {
        return getIntValue("discount_1");
    }

    public Integer getDefault_discount_1(Integer defaultVal) {
        return getIntValue("discount_1", defaultVal);
    }

    public boolean contains_discount_1(Integer value) {
        return containsValue("discount_1", value);
    }

    public void set_discount_2(Integer value) {
        setValue("discount_2", value);
    }

    public void setUI_discount_2(String value)
        throws Exception
    {
        setValueUI("discount_2", value);
    }

    public Integer get_discount_2() {
        return getIntValue("discount_2");
    }

    public Integer getDefault_discount_2(Integer defaultVal) {
        return getIntValue("discount_2", defaultVal);
    }

    public boolean contains_discount_2(Integer value) {
        return containsValue("discount_2", value);
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

}
