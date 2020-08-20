
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Advertisement
    extends Item
{

    public final static String _NAME = "advertisement";

    private Advertisement(Item item) {
        super(item);
    }

    public static Advertisement get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'advertisement' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Advertisement(item);
    }

    public static Advertisement newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
