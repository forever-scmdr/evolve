
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.math.BigDecimal;

public class Price
    extends Item
{

    public final static String _NAME = "price";
    public final static String QTY = "qty";
    public final static String PRICE = "price";

    private Price(Item item) {
        super(item);
    }

    public static Price get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'price' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Price(item);
    }

    public static Price newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_qty(BigDecimal value) {
        setValue("qty", value);
    }

    public void setUI_qty(String value)
        throws Exception
    {
        setValueUI("qty", value);
    }

    public BigDecimal get_qty() {
        return getDecimalValue("qty");
    }

    public BigDecimal getDefault_qty(BigDecimal defaultVal) {
        return getDecimalValue("qty", defaultVal);
    }

    public boolean contains_qty(BigDecimal value) {
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

}
