
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Line_product_container
    extends Item
{

    public final static String _NAME = "line_product_container";
    public final static String HAS_LINES = "has_lines";

    private Line_product_container(Item item) {
        super(item);
    }

    public static Line_product_container get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'line_product_container' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Line_product_container(item);
    }

    public static Line_product_container newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_has_lines(Byte value) {
        setValue("has_lines", value);
    }

    public void setUI_has_lines(String value)
        throws Exception
    {
        setValueUI("has_lines", value);
    }

    public Byte get_has_lines() {
        return getByteValue("has_lines");
    }

    public Byte getDefault_has_lines(Byte defaultVal) {
        return getByteValue("has_lines", defaultVal);
    }

    public boolean contains_has_lines(Byte value) {
        return containsValue("has_lines", value);
    }

}
