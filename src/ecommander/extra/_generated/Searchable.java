
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Searchable
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "searchable";

    private Searchable(Item item) {
        super(item);
    }

    public static Searchable get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'searchable' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Searchable(item);
    }

    public static Searchable newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_search_string(String value) {
        setValue("search_string", value);
    }

    public String get_search_string() {
        return getStringValue("search_string");
    }

    public String getDefault_search_string(String defaultVal) {
        return getStringValue("search_string", defaultVal);
    }

    public boolean contains_search_string(String value) {
        return containsValue("search_string", value);
    }

}
