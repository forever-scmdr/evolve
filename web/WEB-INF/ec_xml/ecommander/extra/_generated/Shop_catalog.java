
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.math.BigDecimal;

public class Shop_catalog
    extends Item
{

    public final static String _NAME = "shop_catalog";
    public final static String C1 = "c1";
    public final static String C2 = "c2";
    public final static String C3 = "c3";
    public final static String C4 = "c4";

    private Shop_catalog(Item item) {
        super(item);
    }

    public static Shop_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'shop_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Shop_catalog(item);
    }

    public static Shop_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_c1(BigDecimal value) {
        setValue("c1", value);
    }

    public void setUI_c1(String value)
        throws Exception
    {
        setValueUI("c1", value);
    }

    public BigDecimal get_c1() {
        return getDecimalValue("c1");
    }

    public BigDecimal getDefault_c1(BigDecimal defaultVal) {
        return getDecimalValue("c1", defaultVal);
    }

    public boolean contains_c1(BigDecimal value) {
        return containsValue("c1", value);
    }

    public void set_c2(BigDecimal value) {
        setValue("c2", value);
    }

    public void setUI_c2(String value)
        throws Exception
    {
        setValueUI("c2", value);
    }

    public BigDecimal get_c2() {
        return getDecimalValue("c2");
    }

    public BigDecimal getDefault_c2(BigDecimal defaultVal) {
        return getDecimalValue("c2", defaultVal);
    }

    public boolean contains_c2(BigDecimal value) {
        return containsValue("c2", value);
    }

    public void set_c3(BigDecimal value) {
        setValue("c3", value);
    }

    public void setUI_c3(String value)
        throws Exception
    {
        setValueUI("c3", value);
    }

    public BigDecimal get_c3() {
        return getDecimalValue("c3");
    }

    public BigDecimal getDefault_c3(BigDecimal defaultVal) {
        return getDecimalValue("c3", defaultVal);
    }

    public boolean contains_c3(BigDecimal value) {
        return containsValue("c3", value);
    }

    public void set_c4(BigDecimal value) {
        setValue("c4", value);
    }

    public void setUI_c4(String value)
        throws Exception
    {
        setValueUI("c4", value);
    }

    public BigDecimal get_c4() {
        return getDecimalValue("c4");
    }

    public BigDecimal getDefault_c4(BigDecimal defaultVal) {
        return getDecimalValue("c4", defaultVal);
    }

    public boolean contains_c4(BigDecimal value) {
        return containsValue("c4", value);
    }

}
