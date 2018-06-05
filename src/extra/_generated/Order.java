
package extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Order
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "order";

    private Order(Item item) {
        super(item);
    }

    public static Order get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'order' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Order(item);
    }

    public static Order newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_number(Integer value) {
        setValue("number", value);
    }

    public void setUI_number(String value)
        throws Exception
    {
        setValueUI("number", value);
    }

    public Integer get_number() {
        return getIntValue("number");
    }

    public Integer getDefault_number(Integer defaultVal) {
        return getIntValue("number", defaultVal);
    }

    public boolean contains_number(Integer value) {
        return containsValue("number", value);
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

    public void set_cookie(String value) {
        setValue("cookie", value);
    }

    public String get_cookie() {
        return getStringValue("cookie");
    }

    public String getDefault_cookie(String defaultVal) {
        return getStringValue("cookie", defaultVal);
    }

    public boolean contains_cookie(String value) {
        return containsValue("cookie", value);
    }

    public void set_currency_ratio(Double value) {
        setValue("currency_ratio", value);
    }

    public void setUI_currency_ratio(String value)
        throws Exception
    {
        setValueUI("currency_ratio", value);
    }

    public Double get_currency_ratio() {
        return getDoubleValue("currency_ratio");
    }

    public Double getDefault_currency_ratio(Double defaultVal) {
        return getDoubleValue("currency_ratio", defaultVal);
    }

    public boolean contains_currency_ratio(Double value) {
        return containsValue("currency_ratio", value);
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

    public void set_ship_type(String value) {
        setValue("ship_type", value);
    }

    public String get_ship_type() {
        return getStringValue("ship_type");
    }

    public String getDefault_ship_type(String defaultVal) {
        return getStringValue("ship_type", defaultVal);
    }

    public boolean contains_ship_type(String value) {
        return containsValue("ship_type", value);
    }

}
