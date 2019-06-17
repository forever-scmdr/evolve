
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Present_catalog
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "present_catalog";

    private Present_catalog(Item item) {
        super(item);
    }

    public static Present_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'present_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Present_catalog(item);
    }

    public static Present_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
