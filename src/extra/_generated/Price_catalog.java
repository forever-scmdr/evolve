
package extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Price_catalog
    extends Item
{

    public final static String _NAME = "price_catalog";
    public final static String QUOTIENT = "quotient";

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

}
