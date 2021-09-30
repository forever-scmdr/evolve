
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Main_page
    extends Item
{

    public final static String _NAME = "main_page";
    public final static String TIMEOUT = "timeout";

    private Main_page(Item item) {
        super(item);
    }

    public static Main_page get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'main_page' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Main_page(item);
    }

    public static Main_page newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_timeout(Integer value) {
        setValue("timeout", value);
    }

    public void setUI_timeout(String value)
        throws Exception
    {
        setValueUI("timeout", value);
    }

    public Integer get_timeout() {
        return getIntValue("timeout");
    }

    public Integer getDefault_timeout(Integer defaultVal) {
        return getIntValue("timeout", defaultVal);
    }

    public boolean contains_timeout(Integer value) {
        return containsValue("timeout", value);
    }

}
