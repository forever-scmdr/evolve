
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Phys
    extends Item
{

    public final static String _NAME = "phys";

    private Phys(Item item) {
        super(item);
    }

    public static Phys get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'phys' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Phys(item);
    }

    public static Phys newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
