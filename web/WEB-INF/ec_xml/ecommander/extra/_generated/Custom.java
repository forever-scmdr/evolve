
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Custom
    extends Item
{

    public final static String _NAME = "custom";

    private Custom(Item item) {
        super(item);
    }

    public static Custom get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'custom' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Custom(item);
    }

    public static Custom newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
