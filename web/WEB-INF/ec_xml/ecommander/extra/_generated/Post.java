
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Post
    extends Item
{

    public final static String _NAME = "post";

    private Post(Item item) {
        super(item);
    }

    public static Post get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'post' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Post(item);
    }

    public static Post newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
