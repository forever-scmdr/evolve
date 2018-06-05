
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class History
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "history";

    private History(Item item) {
        super(item);
    }

    public static History get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'history' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new History(item);
    }

    public static History newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
