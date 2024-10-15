
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Gifts
    extends Item
{

    public final static String _NAME = "gifts";

    private Gifts(Item item) {
        super(item);
    }

    public static Gifts get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'gifts' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Gifts(item);
    }

    public static Gifts newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
