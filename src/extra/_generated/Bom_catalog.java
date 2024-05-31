
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Bom_catalog
    extends Item
{

    public final static String _NAME = "bom_catalog";

    private Bom_catalog(Item item) {
        super(item);
    }

    public static Bom_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'bom_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Bom_catalog(item);
    }

    public static Bom_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
