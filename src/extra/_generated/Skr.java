
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Skr
    extends Item
{

    public final static String _NAME = "skr";

    private Skr(Item item) {
        super(item);
    }

    public static Skr get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'skr' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Skr(item);
    }

    public static Skr newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
