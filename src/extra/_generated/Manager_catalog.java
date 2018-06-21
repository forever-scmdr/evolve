
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Manager_catalog
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "manager_catalog";

    private Manager_catalog(Item item) {
        super(item);
    }

    public static Manager_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'manager_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Manager_catalog(item);
    }

    public static Manager_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
