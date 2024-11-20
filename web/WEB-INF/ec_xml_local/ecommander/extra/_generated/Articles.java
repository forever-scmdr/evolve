
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Articles
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "articles";

    private Articles(Item item) {
        super(item);
    }

    public static Articles get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'articles' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Articles(item);
    }

    public static Articles newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
