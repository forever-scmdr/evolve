
package extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Option
    extends Item
{

    public final static String _NAME = "option";
    public final static String NAME = "name";
    public final static String CODE = "code";
    public final static String PRICE = "price";
    public final static String QTY = "qty";
    public final static String QTY_OPT = "qty_opt";
    public final static String UNIT = "unit";
    public final static String PRICE_OPT = "price_opt";
    public final static String GROUP = "group";
    public final static String MAX = "max";
    public final static String MANDATORY = "mandatory";

    private Option(Item item) {
        super(item);
    }

    public static Option get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'option' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Option(item);
    }

    public static Option newChild(Item parent) {
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

    public void set_qty_opt(Double value) {
        setValue("qty_opt", value);
    }

    public void setUI_qty_opt(String value)
        throws Exception
    {
        setValueUI("qty_opt", value);
    }

    public Double get_qty_opt() {
        return getDoubleValue("qty_opt");
    }

    public Double getDefault_qty_opt(Double defaultVal) {
        return getDoubleValue("qty_opt", defaultVal);
    }

    public boolean contains_qty_opt(Double value) {
        return containsValue("qty_opt", value);
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

    public void set_price_opt(BigDecimal value) {
        setValue("price_opt", value);
    }

    public void setUI_price_opt(String value)
        throws Exception
    {
        setValueUI("price_opt", value);
    }

    public BigDecimal get_price_opt() {
        return getDecimalValue("price_opt");
    }

    public BigDecimal getDefault_price_opt(BigDecimal defaultVal) {
        return getDecimalValue("price_opt", defaultVal);
    }

    public boolean contains_price_opt(BigDecimal value) {
        return containsValue("price_opt", value);
    }

    public void set_group(String value) {
        setValue("group", value);
    }

    public String get_group() {
        return getStringValue("group");
    }

    public String getDefault_group(String defaultVal) {
        return getStringValue("group", defaultVal);
    }

    public boolean contains_group(String value) {
        return containsValue("group", value);
    }

    public void set_max(Integer value) {
        setValue("max", value);
    }

    public void setUI_max(String value)
        throws Exception
    {
        setValueUI("max", value);
    }

    public Integer get_max() {
        return getIntValue("max");
    }

    public Integer getDefault_max(Integer defaultVal) {
        return getIntValue("max", defaultVal);
    }

    public boolean contains_max(Integer value) {
        return containsValue("max", value);
    }

    public void set_mandatory(Byte value) {
        setValue("mandatory", value);
    }

    public void setUI_mandatory(String value)
        throws Exception
    {
        setValueUI("mandatory", value);
    }

    public Byte get_mandatory() {
        return getByteValue("mandatory");
    }

    public Byte getDefault_mandatory(Byte defaultVal) {
        return getByteValue("mandatory", defaultVal);
    }

    public boolean contains_mandatory(Byte value) {
        return containsValue("mandatory", value);
    }

}
