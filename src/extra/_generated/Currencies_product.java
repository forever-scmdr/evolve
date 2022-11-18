
package extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Currencies_product
    extends Item
{

    public final static String _NAME = "currencies_product";
    public final static String PRICE_RUB = "price_RUB";
    public final static String PRICE_RUB_OLD = "price_RUB_old";
    public final static String PRICE_USD = "price_USD";
    public final static String PRICE_USD_OLD = "price_USD_old";
    public final static String PRICE_EUR = "price_EUR";
    public final static String PRICE_EUR_OLD = "price_EUR_old";

    private Currencies_product(Item item) {
        super(item);
    }

    public static Currencies_product get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'currencies_product' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Currencies_product(item);
    }

    public static Currencies_product newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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

    public void set_price_RUB_old(BigDecimal value) {
        setValue("price_RUB_old", value);
    }

    public void setUI_price_RUB_old(String value)
        throws Exception
    {
        setValueUI("price_RUB_old", value);
    }

    public BigDecimal get_price_RUB_old() {
        return getDecimalValue("price_RUB_old");
    }

    public BigDecimal getDefault_price_RUB_old(BigDecimal defaultVal) {
        return getDecimalValue("price_RUB_old", defaultVal);
    }

    public boolean contains_price_RUB_old(BigDecimal value) {
        return containsValue("price_RUB_old", value);
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

    public void set_price_USD_old(BigDecimal value) {
        setValue("price_USD_old", value);
    }

    public void setUI_price_USD_old(String value)
        throws Exception
    {
        setValueUI("price_USD_old", value);
    }

    public BigDecimal get_price_USD_old() {
        return getDecimalValue("price_USD_old");
    }

    public BigDecimal getDefault_price_USD_old(BigDecimal defaultVal) {
        return getDecimalValue("price_USD_old", defaultVal);
    }

    public boolean contains_price_USD_old(BigDecimal value) {
        return containsValue("price_USD_old", value);
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

    public void set_price_EUR_old(BigDecimal value) {
        setValue("price_EUR_old", value);
    }

    public void setUI_price_EUR_old(String value)
        throws Exception
    {
        setValueUI("price_EUR_old", value);
    }

    public BigDecimal get_price_EUR_old() {
        return getDecimalValue("price_EUR_old");
    }

    public BigDecimal getDefault_price_EUR_old(BigDecimal defaultVal) {
        return getDecimalValue("price_EUR_old", defaultVal);
    }

    public boolean contains_price_EUR_old(BigDecimal value) {
        return containsValue("price_EUR_old", value);
    }

}
