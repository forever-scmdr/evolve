
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Analogs
    extends Item
{

    public final static String _NAME = "analogs";

    private Analogs(Item item) {
        super(item);
    }

    public static Analogs get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'analogs' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Analogs(item);
    }

    public static Analogs newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
