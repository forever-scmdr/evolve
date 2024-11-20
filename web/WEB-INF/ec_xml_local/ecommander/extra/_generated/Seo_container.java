
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Seo_container
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "seo_container";

    private Seo_container(Item item) {
        super(item);
    }

    public static Seo_container get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'seo_container' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Seo_container(item);
    }

    public static Seo_container newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
