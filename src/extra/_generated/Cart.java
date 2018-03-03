
package extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Cart
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "cart";

    private Cart(Item item) {
        super(item);
    }

    public static Cart get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'cart' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Cart(item);
    }

    public static Cart newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_sum(BigDecimal value) {
        setValue("sum", value);
    }

    public void setUI_sum(String value)
        throws Exception
    {
        setValueUI("sum", value);
    }

    public BigDecimal get_sum() {
        return getDecimalValue("sum");
    }

    public BigDecimal getDefault_sum(BigDecimal defaultVal) {
        return getDecimalValue("sum", defaultVal);
    }

    public boolean contains_sum(BigDecimal value) {
        return containsValue("sum", value);
    }

    public void set_processed(Byte value) {
        setValue("processed", value);
    }

    public void setUI_processed(String value)
        throws Exception
    {
        setValueUI("processed", value);
    }

    public Byte get_processed() {
        return getByteValue("processed");
    }

    public Byte getDefault_processed(Byte defaultVal) {
        return getByteValue("processed", defaultVal);
    }

    public boolean contains_processed(Byte value) {
        return containsValue("processed", value);
    }

}
