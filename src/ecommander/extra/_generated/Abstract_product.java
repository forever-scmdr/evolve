
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.math.BigDecimal;

public class Abstract_product
    extends Item
{

    public final static String _NAME = "abstract_product";
    public final static String NAME = "name";
    public final static String CODE = "code";
    public final static String PRICE = "price";
    public final static String PRICE_1 = "price_1";
    public final static String PRICE_2 = "price_2";
    public final static String PRICE_3 = "price_3";
    public final static String QTY = "qty";
    public final static String QTY_1 = "qty_1";
    public final static String QTY_2 = "qty_2";
    public final static String QTY_3 = "qty_3";
    public final static String UNIT = "unit";

    private Abstract_product(Item item) {
        super(item);
    }

    public static Abstract_product get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'abstract_product' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Abstract_product(item);
    }

    public static Abstract_product newChild(Item parent) {
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

    public void set_price_1(BigDecimal value) {
        setValue("price_1", value);
    }

    public void setUI_price_1(String value)
        throws Exception
    {
        setValueUI("price_1", value);
    }

    public BigDecimal get_price_1() {
        return getDecimalValue("price_1");
    }

    public BigDecimal getDefault_price_1(BigDecimal defaultVal) {
        return getDecimalValue("price_1", defaultVal);
    }

    public boolean contains_price_1(BigDecimal value) {
        return containsValue("price_1", value);
    }

    public void set_price_2(BigDecimal value) {
        setValue("price_2", value);
    }

    public void setUI_price_2(String value)
        throws Exception
    {
        setValueUI("price_2", value);
    }

    public BigDecimal get_price_2() {
        return getDecimalValue("price_2");
    }

    public BigDecimal getDefault_price_2(BigDecimal defaultVal) {
        return getDecimalValue("price_2", defaultVal);
    }

    public boolean contains_price_2(BigDecimal value) {
        return containsValue("price_2", value);
    }

    public void set_price_3(BigDecimal value) {
        setValue("price_3", value);
    }

    public void setUI_price_3(String value)
        throws Exception
    {
        setValueUI("price_3", value);
    }

    public BigDecimal get_price_3() {
        return getDecimalValue("price_3");
    }

    public BigDecimal getDefault_price_3(BigDecimal defaultVal) {
        return getDecimalValue("price_3", defaultVal);
    }

    public boolean contains_price_3(BigDecimal value) {
        return containsValue("price_3", value);
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

    public void set_qty_1(Double value) {
        setValue("qty_1", value);
    }

    public void setUI_qty_1(String value)
        throws Exception
    {
        setValueUI("qty_1", value);
    }

    public Double get_qty_1() {
        return getDoubleValue("qty_1");
    }

    public Double getDefault_qty_1(Double defaultVal) {
        return getDoubleValue("qty_1", defaultVal);
    }

    public boolean contains_qty_1(Double value) {
        return containsValue("qty_1", value);
    }

    public void set_qty_2(Double value) {
        setValue("qty_2", value);
    }

    public void setUI_qty_2(String value)
        throws Exception
    {
        setValueUI("qty_2", value);
    }

    public Double get_qty_2() {
        return getDoubleValue("qty_2");
    }

    public Double getDefault_qty_2(Double defaultVal) {
        return getDoubleValue("qty_2", defaultVal);
    }

    public boolean contains_qty_2(Double value) {
        return containsValue("qty_2", value);
    }

    public void set_qty_3(Double value) {
        setValue("qty_3", value);
    }

    public void setUI_qty_3(String value)
        throws Exception
    {
        setValueUI("qty_3", value);
    }

    public Double get_qty_3() {
        return getDoubleValue("qty_3");
    }

    public Double getDefault_qty_3(Double defaultVal) {
        return getDoubleValue("qty_3", defaultVal);
    }

    public boolean contains_qty_3(Double value) {
        return containsValue("qty_3", value);
    }

    public void set_unit(String value) {
        setValue("unit", value);
    }

    public String get_unit() {
        return getStringValue("unit");
    }

    public String getDefault_unit(String defaultVal) {
        return getStringValue("unit", defaultVal);
    }

    public boolean contains_unit(String value) {
        return containsValue("unit", value);
    }

}
