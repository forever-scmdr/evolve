
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Articles
    extends Item
{

    public final static String _NAME = "articles";

    private Articles(Item item) {
        super(item);
    }

    public static Articles get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'articles' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Articles(item);
    }

    public static Articles newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
