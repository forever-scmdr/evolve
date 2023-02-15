
package extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Purchase
    extends Item
{

    public final static String _NAME = "purchase";
    public final static String NUM = "num";
    public final static String DATE = "date";
    public final static String QTY = "qty";
    public final static String QTY_AVAIL = "qty_avail";
    public final static String QTY_TOTAL = "qty_total";
    public final static String SUM = "sum";
    public final static String STATUS = "status";

    private Purchase(Item item) {
        super(item);
    }

    public static Purchase get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'purchase' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Purchase(item);
    }

    public static Purchase newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_num(String value) {
        setValue("num", value);
    }

    public String get_num() {
        return getStringValue("num");
    }

    public String getDefault_num(String defaultVal) {
        return getStringValue("num", defaultVal);
    }

    public boolean contains_num(String value) {
        return containsValue("num", value);
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

    public void set_status(String value) {
        setValue("status", value);
    }

    public String get_status() {
        return getStringValue("status");
    }

    public String getDefault_status(String defaultVal) {
        return getStringValue("status", defaultVal);
    }

    public boolean contains_status(String value) {
        return containsValue("status", value);
    }

}
