
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Dictionary
    extends Item
{

    public final static String _NAME = "dictionary";

    private Dictionary(Item item) {
        super(item);
    }

    public static Dictionary get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'dictionary' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Dictionary(item);
    }

    public static Dictionary newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
