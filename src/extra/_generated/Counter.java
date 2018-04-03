
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Counter
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "counter";

    private Counter(Item item) {
        super(item);
    }

    public static Counter get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'counter' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Counter(item);
    }

    public static Counter newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_count(Integer value) {
        setValue("count", value);
    }

    public void setUI_count(String value)
        throws Exception
    {
        setValueUI("count", value);
    }

    public Integer get_count() {
        return getIntValue("count");
    }

    public Integer getDefault_count(Integer defaultVal) {
        return getIntValue("count", defaultVal);
    }

    public boolean contains_count(Integer value) {
        return containsValue("count", value);
    }

}
