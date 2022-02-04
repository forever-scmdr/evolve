
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Preorders
    extends Item
{

    public final static String _NAME = "preorders";

    private Preorders(Item item) {
        super(item);
    }

    public static Preorders get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'preorders' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Preorders(item);
    }

    public static Preorders newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
