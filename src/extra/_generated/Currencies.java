
package extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Currencies
    extends Item
{

    public final static String _NAME = "currencies";
    public final static String BYN_CAPTION = "BYN_caption";
    public final static String RUB_CAPTION = "RUB_caption";
    public final static String RUB_RATE = "RUB_rate";
    public final static String RUB_SCALE = "RUB_scale";
    public final static String RUB_CEIL = "RUB_ceil";
    public final static String RUB_EXTRA_QUOTIENT = "RUB_extra_quotient";
    public final static String RUB_UPDATE = "RUB_update";
    public final static String RUB_SHOW = "RUB_show";
    public final static String USD_CAPTION = "USD_caption";
    public final static String USD_RATE = "USD_rate";
    public final static String USD_SCALE = "USD_scale";
    public final static String USD_CEIL = "USD_ceil";
    public final static String USD_EXTRA_QUOTIENT = "USD_extra_quotient";
    public final static String USD_UPDATE = "USD_update";
    public final static String USD_SHOW = "USD_show";
    public final static String EUR_CAPTION = "EUR_caption";
    public final static String EUR_RATE = "EUR_rate";
    public final static String EUR_SCALE = "EUR_scale";
    public final static String EUR_CEIL = "EUR_ceil";
    public final static String EUR_EXTRA_QUOTIENT = "EUR_extra_quotient";
    public final static String EUR_UPDATE = "EUR_update";
    public final static String EUR_SHOW = "EUR_show";

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

    public void set_BYN_caption(String value) {
        setValue("BYN_caption", value);
    }

    public String get_BYN_caption() {
        return getStringValue("BYN_caption");
    }

    public String getDefault_BYN_caption(String defaultVal) {
        return getStringValue("BYN_caption", defaultVal);
    }

    public boolean contains_BYN_caption(String value) {
        return containsValue("BYN_caption", value);
    }

    public void set_RUB_caption(String value) {
        setValue("RUB_caption", value);
    }

    public String get_RUB_caption() {
        return getStringValue("RUB_caption");
    }

    public String getDefault_RUB_caption(String defaultVal) {
        return getStringValue("RUB_caption", defaultVal);
    }

    public boolean contains_RUB_caption(String value) {
        return containsValue("RUB_caption", value);
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

    public void set_RUB_ceil(Byte value) {
        setValue("RUB_ceil", value);
    }

    public void setUI_RUB_ceil(String value)
        throws Exception
    {
        setValueUI("RUB_ceil", value);
    }

    public Byte get_RUB_ceil() {
        return getByteValue("RUB_ceil");
    }

    public Byte getDefault_RUB_ceil(Byte defaultVal) {
        return getByteValue("RUB_ceil", defaultVal);
    }

    public boolean contains_RUB_ceil(Byte value) {
        return containsValue("RUB_ceil", value);
    }

    public void set_RUB_extra_quotient(Double value) {
        setValue("RUB_extra_quotient", value);
    }

    public void setUI_RUB_extra_quotient(String value)
        throws Exception
    {
        setValueUI("RUB_extra_quotient", value);
    }

    public Double get_RUB_extra_quotient() {
        return getDoubleValue("RUB_extra_quotient");
    }

    public Double getDefault_RUB_extra_quotient(Double defaultVal) {
        return getDoubleValue("RUB_extra_quotient", defaultVal);
    }

    public boolean contains_RUB_extra_quotient(Double value) {
        return containsValue("RUB_extra_quotient", value);
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

    public void set_RUB_show(Byte value) {
        setValue("RUB_show", value);
    }

    public void setUI_RUB_show(String value)
        throws Exception
    {
        setValueUI("RUB_show", value);
    }

    public Byte get_RUB_show() {
        return getByteValue("RUB_show");
    }

    public Byte getDefault_RUB_show(Byte defaultVal) {
        return getByteValue("RUB_show", defaultVal);
    }

    public boolean contains_RUB_show(Byte value) {
        return containsValue("RUB_show", value);
    }

    public void set_USD_caption(String value) {
        setValue("USD_caption", value);
    }

    public String get_USD_caption() {
        return getStringValue("USD_caption");
    }

    public String getDefault_USD_caption(String defaultVal) {
        return getStringValue("USD_caption", defaultVal);
    }

    public boolean contains_USD_caption(String value) {
        return containsValue("USD_caption", value);
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

    public void set_USD_ceil(Byte value) {
        setValue("USD_ceil", value);
    }

    public void setUI_USD_ceil(String value)
        throws Exception
    {
        setValueUI("USD_ceil", value);
    }

    public Byte get_USD_ceil() {
        return getByteValue("USD_ceil");
    }

    public Byte getDefault_USD_ceil(Byte defaultVal) {
        return getByteValue("USD_ceil", defaultVal);
    }

    public boolean contains_USD_ceil(Byte value) {
        return containsValue("USD_ceil", value);
    }

    public void set_USD_extra_quotient(Double value) {
        setValue("USD_extra_quotient", value);
    }

    public void setUI_USD_extra_quotient(String value)
        throws Exception
    {
        setValueUI("USD_extra_quotient", value);
    }

    public Double get_USD_extra_quotient() {
        return getDoubleValue("USD_extra_quotient");
    }

    public Double getDefault_USD_extra_quotient(Double defaultVal) {
        return getDoubleValue("USD_extra_quotient", defaultVal);
    }

    public boolean contains_USD_extra_quotient(Double value) {
        return containsValue("USD_extra_quotient", value);
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

    public void set_USD_show(Byte value) {
        setValue("USD_show", value);
    }

    public void setUI_USD_show(String value)
        throws Exception
    {
        setValueUI("USD_show", value);
    }

    public Byte get_USD_show() {
        return getByteValue("USD_show");
    }

    public Byte getDefault_USD_show(Byte defaultVal) {
        return getByteValue("USD_show", defaultVal);
    }

    public boolean contains_USD_show(Byte value) {
        return containsValue("USD_show", value);
    }

    public void set_EUR_caption(String value) {
        setValue("EUR_caption", value);
    }

    public String get_EUR_caption() {
        return getStringValue("EUR_caption");
    }

    public String getDefault_EUR_caption(String defaultVal) {
        return getStringValue("EUR_caption", defaultVal);
    }

    public boolean contains_EUR_caption(String value) {
        return containsValue("EUR_caption", value);
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

    public void set_EUR_ceil(Byte value) {
        setValue("EUR_ceil", value);
    }

    public void setUI_EUR_ceil(String value)
        throws Exception
    {
        setValueUI("EUR_ceil", value);
    }

    public Byte get_EUR_ceil() {
        return getByteValue("EUR_ceil");
    }

    public Byte getDefault_EUR_ceil(Byte defaultVal) {
        return getByteValue("EUR_ceil", defaultVal);
    }

    public boolean contains_EUR_ceil(Byte value) {
        return containsValue("EUR_ceil", value);
    }

    public void set_EUR_extra_quotient(Double value) {
        setValue("EUR_extra_quotient", value);
    }

    public void setUI_EUR_extra_quotient(String value)
        throws Exception
    {
        setValueUI("EUR_extra_quotient", value);
    }

    public Double get_EUR_extra_quotient() {
        return getDoubleValue("EUR_extra_quotient");
    }

    public Double getDefault_EUR_extra_quotient(Double defaultVal) {
        return getDoubleValue("EUR_extra_quotient", defaultVal);
    }

    public boolean contains_EUR_extra_quotient(Double value) {
        return containsValue("EUR_extra_quotient", value);
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

    public void set_EUR_show(Byte value) {
        setValue("EUR_show", value);
    }

    public void setUI_EUR_show(String value)
        throws Exception
    {
        setValueUI("EUR_show", value);
    }

    public Byte get_EUR_show() {
        return getByteValue("EUR_show");
    }

    public Byte getDefault_EUR_show(Byte defaultVal) {
        return getByteValue("EUR_show", defaultVal);
    }

    public boolean contains_EUR_show(Byte value) {
        return containsValue("EUR_show", value);
    }

}
