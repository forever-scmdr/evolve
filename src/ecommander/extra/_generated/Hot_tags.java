
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Hot_tags
    extends Item
{

    public final static String _NAME = "hot_tags";

    private Hot_tags(Item item) {
        super(item);
    }

    public static Hot_tags get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'hot_tags' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Hot_tags(item);
    }

    public static Hot_tags newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
