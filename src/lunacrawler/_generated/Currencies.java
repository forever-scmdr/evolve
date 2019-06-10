
package lunacrawler._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Currencies
    extends Item
{

    public final static String _NAME = "currencies";
    public final static String RUB_RATE = "RUB_rate";
    public final static String RUB_SCALE = "RUB_scale";
    public final static String RUB_EXTRA = "RUB_extra";
    public final static String USD_RATE = "USD_rate";
    public final static String USD_SCALE = "USD_scale";
    public final static String USD_EXTRA = "USD_extra";
    public final static String EUR_RATE = "EUR_rate";
    public final static String EUR_SCALE = "EUR_scale";
    public final static String EUR_EXTRA = "EUR_extra";

    private Currencies(Item item) {
        super(item);
    }

    public static Currencies get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'currencies' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Currencies(item);
    }

    public static Currencies newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_RUB_rate(BigDecimal value) {
        setValue("RUB_rate", value);
    }

    public void setUI_RUB_rate(String value)
        throws Exception
    {
        setValueUI("RUB_rate", value);
    }

    public BigDecimal get_RUB_rate() {
        return getDecimalValue("RUB_rate");
    }

    public BigDecimal getDefault_RUB_rate(BigDecimal defaultVal) {
        return getDecimalValue("RUB_rate", defaultVal);
    }

    public boolean contains_RUB_rate(BigDecimal value) {
        return containsValue("RUB_rate", value);
    }

    public void set_RUB_scale(BigDecimal value) {
        setValue("RUB_scale", value);
    }

    public void setUI_RUB_scale(String value)
        throws Exception
    {
        setValueUI("RUB_scale", value);
    }

    public BigDecimal get_RUB_scale() {
        return getDecimalValue("RUB_scale");
    }

    public BigDecimal getDefault_RUB_scale(BigDecimal defaultVal) {
        return getDecimalValue("RUB_scale", defaultVal);
    }

    public boolean contains_RUB_scale(BigDecimal value) {
        return containsValue("RUB_scale", value);
    }

    public void set_RUB_extra(BigDecimal value) {
        setValue("RUB_extra", value);
    }

    public void setUI_RUB_extra(String value)
        throws Exception
    {
        setValueUI("RUB_extra", value);
    }

    public BigDecimal get_RUB_extra() {
        return getDecimalValue("RUB_extra");
    }

    public BigDecimal getDefault_RUB_extra(BigDecimal defaultVal) {
        return getDecimalValue("RUB_extra", defaultVal);
    }

    public boolean contains_RUB_extra(BigDecimal value) {
        return containsValue("RUB_extra", value);
    }

    public void set_USD_rate(BigDecimal value) {
        setValue("USD_rate", value);
    }

    public void setUI_USD_rate(String value)
        throws Exception
    {
        setValueUI("USD_rate", value);
    }

    public BigDecimal get_USD_rate() {
        return getDecimalValue("USD_rate");
    }

    public BigDecimal getDefault_USD_rate(BigDecimal defaultVal) {
        return getDecimalValue("USD_rate", defaultVal);
    }

    public boolean contains_USD_rate(BigDecimal value) {
        return containsValue("USD_rate", value);
    }

    public void set_USD_scale(BigDecimal value) {
        setValue("USD_scale", value);
    }

    public void setUI_USD_scale(String value)
        throws Exception
    {
        setValueUI("USD_scale", value);
    }

    public BigDecimal get_USD_scale() {
        return getDecimalValue("USD_scale");
    }

    public BigDecimal getDefault_USD_scale(BigDecimal defaultVal) {
        return getDecimalValue("USD_scale", defaultVal);
    }

    public boolean contains_USD_scale(BigDecimal value) {
        return containsValue("USD_scale", value);
    }

    public void set_USD_extra(BigDecimal value) {
        setValue("USD_extra", value);
    }

    public void setUI_USD_extra(String value)
        throws Exception
    {
        setValueUI("USD_extra", value);
    }

    public BigDecimal get_USD_extra() {
        return getDecimalValue("USD_extra");
    }

    public BigDecimal getDefault_USD_extra(BigDecimal defaultVal) {
        return getDecimalValue("USD_extra", defaultVal);
    }

    public boolean contains_USD_extra(BigDecimal value) {
        return containsValue("USD_extra", value);
    }

    public void set_EUR_rate(BigDecimal value) {
        setValue("EUR_rate", value);
    }

    public void setUI_EUR_rate(String value)
        throws Exception
    {
        setValueUI("EUR_rate", value);
    }

    public BigDecimal get_EUR_rate() {
        return getDecimalValue("EUR_rate");
    }

    public BigDecimal getDefault_EUR_rate(BigDecimal defaultVal) {
        return getDecimalValue("EUR_rate", defaultVal);
    }

    public boolean contains_EUR_rate(BigDecimal value) {
        return containsValue("EUR_rate", value);
    }

    public void set_EUR_scale(BigDecimal value) {
        setValue("EUR_scale", value);
    }

    public void setUI_EUR_scale(String value)
        throws Exception
    {
        setValueUI("EUR_scale", value);
    }

    public BigDecimal get_EUR_scale() {
        return getDecimalValue("EUR_scale");
    }

    public BigDecimal getDefault_EUR_scale(BigDecimal defaultVal) {
        return getDecimalValue("EUR_scale", defaultVal);
    }

    public boolean contains_EUR_scale(BigDecimal value) {
        return containsValue("EUR_scale", value);
    }

    public void set_EUR_extra(BigDecimal value) {
        setValue("EUR_extra", value);
    }

    public void setUI_EUR_extra(String value)
        throws Exception
    {
        setValueUI("EUR_extra", value);
    }

    public BigDecimal get_EUR_extra() {
        return getDecimalValue("EUR_extra");
    }

    public BigDecimal getDefault_EUR_extra(BigDecimal defaultVal) {
        return getDecimalValue("EUR_extra", defaultVal);
    }

    public boolean contains_EUR_extra(BigDecimal value) {
        return containsValue("EUR_extra", value);
    }

}
