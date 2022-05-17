
package extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Opt_product
    extends Item
{

    public final static String _NAME = "opt_product";
    public final static String QTY_OPT = "qty_opt";
    public final static String PRICE_OPT = "price_opt";
    public final static String PRICE_OPT_OLD = "price_opt_old";
    public final static String NEXT_DELIVERY = "next_delivery";

    private Opt_product(Item item) {
        super(item);
    }

    public static Opt_product get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'opt_product' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Opt_product(item);
    }

    public static Opt_product newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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

    public void set_price_opt_old(BigDecimal value) {
        setValue("price_opt_old", value);
    }

    public void setUI_price_opt_old(String value)
        throws Exception
    {
        setValueUI("price_opt_old", value);
    }

    public BigDecimal get_price_opt_old() {
        return getDecimalValue("price_opt_old");
    }

    public BigDecimal getDefault_price_opt_old(BigDecimal defaultVal) {
        return getDecimalValue("price_opt_old", defaultVal);
    }

    public boolean contains_price_opt_old(BigDecimal value) {
        return containsValue("price_opt_old", value);
    }

    public void set_next_delivery(String value) {
        setValue("next_delivery", value);
    }

    public String get_next_delivery() {
        return getStringValue("next_delivery");
    }

    public String getDefault_next_delivery(String defaultVal) {
        return getStringValue("next_delivery", defaultVal);
    }

    public boolean contains_next_delivery(String value) {
        return containsValue("next_delivery", value);
    }

}
