
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Group_mask_catalog
    extends Item
{

    public final static String _NAME = "group_mask_catalog";

    private Group_mask_catalog(Item item) {
        super(item);
    }

    public static Group_mask_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'group_mask_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Group_mask_catalog(item);
    }

    public static Group_mask_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
