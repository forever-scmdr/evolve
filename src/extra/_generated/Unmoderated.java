
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Unmoderated
    extends Item
{

    public final static String _NAME = "unmoderated";

    private Unmoderated(Item item) {
        super(item);
    }

    public static Unmoderated get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'unmoderated' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Unmoderated(item);
    }

    public static Unmoderated newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
