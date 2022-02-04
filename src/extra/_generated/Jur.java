
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Jur
    extends Item
{

    public final static String _NAME = "jur";

    private Jur(Item item) {
        super(item);
    }

    public static Jur get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'jur' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Jur(item);
    }

    public static Jur newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
