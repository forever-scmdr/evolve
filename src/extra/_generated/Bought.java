
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
    public final static String PRICE = "price";
    public final static String NOT_AVAILABLE = "not_available";
    public final static String SUM = "sum";
    public final static String AUX = "aux";
    public final static String OUTER_PRODUCT = "outer_product";

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

    public void set_aux(String value) {
        setValue("aux", value);
    }

    public String get_aux() {
        return getStringValue("aux");
    }

    public String getDefault_aux(String defaultVal) {
        return getStringValue("aux", defaultVal);
    }

    public boolean contains_aux(String value) {
        return containsValue("aux", value);
    }

    public void set_outer_product(String value) {
        setValue("outer_product", value);
    }

    public String get_outer_product() {
        return getStringValue("outer_product");
    }

    public String getDefault_outer_product(String defaultVal) {
        return getStringValue("outer_product", defaultVal);
    }

    public boolean contains_outer_product(String value) {
        return containsValue("outer_product", value);
    }

}
