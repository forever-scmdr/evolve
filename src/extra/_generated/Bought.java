
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
    public final static String PRICE = "price";
    public final static String PRICE_RUB = "price_RUB";
    public final static String PRICE_USD = "price_USD";
    public final static String PRICE_EUR = "price_EUR";
    public final static String SUM = "sum";
    public final static String SUM_RUB = "sum_RUB";
    public final static String SUM_USD = "sum_USD";
    public final static String SUM_EUR = "sum_EUR";

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

    public void set_price_RUB(BigDecimal value) {
        setValue("price_RUB", value);
    }

    public void setUI_price_RUB(String value)
        throws Exception
    {
        setValueUI("price_RUB", value);
    }

    public BigDecimal get_price_RUB() {
        return getDecimalValue("price_RUB");
    }

    public BigDecimal getDefault_price_RUB(BigDecimal defaultVal) {
        return getDecimalValue("price_RUB", defaultVal);
    }

    public boolean contains_price_RUB(BigDecimal value) {
        return containsValue("price_RUB", value);
    }

    public void set_price_USD(BigDecimal value) {
        setValue("price_USD", value);
    }

    public void setUI_price_USD(String value)
        throws Exception
    {
        setValueUI("price_USD", value);
    }

    public BigDecimal get_price_USD() {
        return getDecimalValue("price_USD");
    }

    public BigDecimal getDefault_price_USD(BigDecimal defaultVal) {
        return getDecimalValue("price_USD", defaultVal);
    }

    public boolean contains_price_USD(BigDecimal value) {
        return containsValue("price_USD", value);
    }

    public void set_price_EUR(BigDecimal value) {
        setValue("price_EUR", value);
    }

    public void setUI_price_EUR(String value)
        throws Exception
    {
        setValueUI("price_EUR", value);
    }

    public BigDecimal get_price_EUR() {
        return getDecimalValue("price_EUR");
    }

    public BigDecimal getDefault_price_EUR(BigDecimal defaultVal) {
        return getDecimalValue("price_EUR", defaultVal);
    }

    public boolean contains_price_EUR(BigDecimal value) {
        return containsValue("price_EUR", value);
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

}
