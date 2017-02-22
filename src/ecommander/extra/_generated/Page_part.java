
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Page_part
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "page_part";

    private Page_part(Item item) {
        super(item);
    }

    public static Page_part get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'page_part' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Page_part(item);
    }

    public static Page_part newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

}
