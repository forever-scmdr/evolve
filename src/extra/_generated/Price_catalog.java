
package extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Price_catalog
    extends Item
{

    public final static String _NAME = "price_catalog";
    public final static String NAME = "name";
    public final static String QUOTIENT = "quotient";
    public final static String SHIP_TIME = "ship_time";
    public final static String DEFAULT_SHIP_TIME = "default_ship_time";

    private Price_catalog(Item item) {
        super(item);
    }

    public static Price_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'price_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Price_catalog(item);
    }

    public static Price_catalog newChild(Item parent) {
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

    public void set_quotient(BigDecimal value) {
        setValue("quotient", value);
    }

    public void setUI_quotient(String value)
        throws Exception
    {
        setValueUI("quotient", value);
    }

    public BigDecimal get_quotient() {
        return getDecimalValue("quotient");
    }

    public BigDecimal getDefault_quotient(BigDecimal defaultVal) {
        return getDecimalValue("quotient", defaultVal);
    }

    public boolean contains_quotient(BigDecimal value) {
        return containsValue("quotient", value);
    }

    public void set_default_ship_time(Byte value) {
        setValue("default_ship_time", value);
    }

    public void setUI_default_ship_time(String value)
        throws Exception
    {
        setValueUI("default_ship_time", value);
    }

    public Byte get_default_ship_time() {
        return getByteValue("default_ship_time");
    }

    public Byte getDefault_default_ship_time(Byte defaultVal) {
        return getByteValue("default_ship_time", defaultVal);
    }

    public boolean contains_default_ship_time(Byte value) {
        return containsValue("default_ship_time", value);
    }

}
