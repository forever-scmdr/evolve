
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Gallery_part
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "gallery_part";

    private Gallery_part(Item item) {
        super(item);
    }

    public static Gallery_part get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'gallery_part' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Gallery_part(item);
    }

    public static Gallery_part newChild(Item parent) {
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
