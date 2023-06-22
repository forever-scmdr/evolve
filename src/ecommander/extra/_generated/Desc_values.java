
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Desc_values
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "desc_values";

    private Desc_values(Item item) {
        super(item);
    }

    public static Desc_values get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'desc_values' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Desc_values(item);
    }

    public static Desc_values newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_table(String value) {
        setValue("table", value);
    }

    public String get_table() {
        return getStringValue("table");
    }

    public String getDefault_table(String defaultVal) {
        return getStringValue("table", defaultVal);
    }

    public boolean contains_table(String value) {
        return containsValue("table", value);
    }

}
