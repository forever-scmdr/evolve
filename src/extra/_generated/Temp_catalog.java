
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Temp_catalog
    extends Item
{

    public final static String _NAME = "temp_catalog";

    private Temp_catalog(Item item) {
        super(item);
    }

    public static Temp_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'temp_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Temp_catalog(item);
    }

    public static Temp_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
