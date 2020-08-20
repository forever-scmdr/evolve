
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Links
    extends Item
{

    public final static String _NAME = "links";

    private Links(Item item) {
        super(item);
    }

    public static Links get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'links' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Links(item);
    }

    public static Links newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
