
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class News
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "news";

    private News(Item item) {
        super(item);
    }

    public static News get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'news' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new News(item);
    }

    public static News newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
