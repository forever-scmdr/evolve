
package extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Currencies
    extends Item
{

    public final static String _NAME = "currencies";
    public final static String RUB_RATE = "RUB_rate";
    public final static String RUB_SCALE = "RUB_scale";
    public final static String RUB_UPDATE = "RUB_update";
    public final static String USD_RATE = "USD_rate";
    public final static String USD_SCALE = "USD_scale";
    public final static String USD_UPDATE = "USD_update";
    public final static String EUR_RATE = "EUR_rate";
    public final static String EUR_SCALE = "EUR_scale";
    public final static String EUR_UPDATE = "EUR_update";
    public final static String GBP_RATE = "GBP_rate";
    public final static String GBP_SCALE = "GBP_scale";
    public final static String GBP_UPDATE = "GBP_update";
    public final static String BYN_RATE = "BYN_rate";
    public final static String BYN_SCALE = "BYN_scale";
    public final static String BYN_UPDATE = "BYN_update";
    public final static String XML = "xml";

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

    public void set_RUB_update(Byte value) {
        setValue("RUB_update", value);
    }

    public void setUI_RUB_update(String value)
        throws Exception
    {
        setValueUI("RUB_update", value);
    }

    public Byte get_RUB_update() {
        return getByteValue("RUB_update");
    }

    public Byte getDefault_RUB_update(Byte defaultVal) {
        return getByteValue("RUB_update", defaultVal);
    }

    public boolean contains_RUB_update(Byte value) {
        return containsValue("RUB_update", value);
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

    public void set_USD_update(Byte value) {
        setValue("USD_update", value);
    }

    public void setUI_USD_update(String value)
        throws Exception
    {
        setValueUI("USD_update", value);
    }

    public Byte get_USD_update() {
        return getByteValue("USD_update");
    }

    public Byte getDefault_USD_update(Byte defaultVal) {
        return getByteValue("USD_update", defaultVal);
    }

    public boolean contains_USD_update(Byte value) {
        return containsValue("USD_update", value);
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

    public void set_EUR_update(Byte value) {
        setValue("EUR_update", value);
    }

    public void setUI_EUR_update(String value)
        throws Exception
    {
        setValueUI("EUR_update", value);
    }

    public Byte get_EUR_update() {
        return getByteValue("EUR_update");
    }

    public Byte getDefault_EUR_update(Byte defaultVal) {
        return getByteValue("EUR_update", defaultVal);
    }

    public boolean contains_EUR_update(Byte value) {
        return containsValue("EUR_update", value);
    }

    public void set_GBP_rate(BigDecimal value) {
        setValue("GBP_rate", value);
    }

    public void setUI_GBP_rate(String value)
        throws Exception
    {
        setValueUI("GBP_rate", value);
    }

    public BigDecimal get_GBP_rate() {
        return getDecimalValue("GBP_rate");
    }

    public BigDecimal getDefault_GBP_rate(BigDecimal defaultVal) {
        return getDecimalValue("GBP_rate", defaultVal);
    }

    public boolean contains_GBP_rate(BigDecimal value) {
        return containsValue("GBP_rate", value);
    }

    public void set_GBP_scale(BigDecimal value) {
        setValue("GBP_scale", value);
    }

    public void setUI_GBP_scale(String value)
        throws Exception
    {
        setValueUI("GBP_scale", value);
    }

    public BigDecimal get_GBP_scale() {
        return getDecimalValue("GBP_scale");
    }

    public BigDecimal getDefault_GBP_scale(BigDecimal defaultVal) {
        return getDecimalValue("GBP_scale", defaultVal);
    }

    public boolean contains_GBP_scale(BigDecimal value) {
        return containsValue("GBP_scale", value);
    }

    public void set_GBP_update(Byte value) {
        setValue("GBP_update", value);
    }

    public void setUI_GBP_update(String value)
        throws Exception
    {
        setValueUI("GBP_update", value);
    }

    public Byte get_GBP_update() {
        return getByteValue("GBP_update");
    }

    public Byte getDefault_GBP_update(Byte defaultVal) {
        return getByteValue("GBP_update", defaultVal);
    }

    public boolean contains_GBP_update(Byte value) {
        return containsValue("GBP_update", value);
    }

    public void set_BYN_rate(BigDecimal value) {
        setValue("BYN_rate", value);
    }

    public void setUI_BYN_rate(String value)
        throws Exception
    {
        setValueUI("BYN_rate", value);
    }

    public BigDecimal get_BYN_rate() {
        return getDecimalValue("BYN_rate");
    }

    public BigDecimal getDefault_BYN_rate(BigDecimal defaultVal) {
        return getDecimalValue("BYN_rate", defaultVal);
    }

    public boolean contains_BYN_rate(BigDecimal value) {
        return containsValue("BYN_rate", value);
    }

    public void set_BYN_scale(BigDecimal value) {
        setValue("BYN_scale", value);
    }

    public void setUI_BYN_scale(String value)
        throws Exception
    {
        setValueUI("BYN_scale", value);
    }

    public BigDecimal get_BYN_scale() {
        return getDecimalValue("BYN_scale");
    }

    public BigDecimal getDefault_BYN_scale(BigDecimal defaultVal) {
        return getDecimalValue("BYN_scale", defaultVal);
    }

    public boolean contains_BYN_scale(BigDecimal value) {
        return containsValue("BYN_scale", value);
    }

    public void set_BYN_update(Byte value) {
        setValue("BYN_update", value);
    }

    public void setUI_BYN_update(String value)
        throws Exception
    {
        setValueUI("BYN_update", value);
    }

    public Byte get_BYN_update() {
        return getByteValue("BYN_update");
    }

    public Byte getDefault_BYN_update(Byte defaultVal) {
        return getByteValue("BYN_update", defaultVal);
    }

    public boolean contains_BYN_update(Byte value) {
        return containsValue("BYN_update", value);
    }

    public void set_xml(String value) {
        setValue("xml", value);
    }

    public String get_xml() {
        return getStringValue("xml");
    }

    public String getDefault_xml(String defaultVal) {
        return getStringValue("xml", defaultVal);
    }

    public boolean contains_xml(String value) {
        return containsValue("xml", value);
    }

}
