
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Url_catalog
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "url_catalog";

    private Url_catalog(Item item) {
        super(item);
    }

    public static Url_catalog get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'url_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Url_catalog(item);
    }

    public static Url_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
