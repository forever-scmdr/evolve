
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Topper
    extends Item
{

    public final static String _NAME = "topper";

    private Topper(Item item) {
        super(item);
    }

    public static Topper get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'topper' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Topper(item);
    }

    public static Topper newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
