
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Small_news
    extends Item
{

    public final static String _NAME = "small_news";

    private Small_news(Item item) {
        super(item);
    }

    public static Small_news get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'small_news' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Small_news(item);
    }

    public static Small_news newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
