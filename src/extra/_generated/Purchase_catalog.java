
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Purchase_catalog
    extends Item
{

    public final static String _NAME = "purchase_catalog";

    private Purchase_catalog(Item item) {
        super(item);
    }

    public static Purchase_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'purchase_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Purchase_catalog(item);
    }

    public static Purchase_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
