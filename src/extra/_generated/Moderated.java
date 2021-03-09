
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Moderated
    extends Item
{

    public final static String _NAME = "moderated";

    private Moderated(Item item) {
        super(item);
    }

    public static Moderated get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'moderated' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Moderated(item);
    }

    public static Moderated newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
