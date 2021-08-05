
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Preorder_catalog
    extends Item
{

    public final static String _NAME = "preorder_catalog";

    private Preorder_catalog(Item item) {
        super(item);
    }

    public static Preorder_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'preorder_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Preorder_catalog(item);
    }

    public static Preorder_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
