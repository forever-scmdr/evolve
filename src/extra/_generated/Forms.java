
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Forms
    extends Item
{

    public final static String _NAME = "forms";

    private Forms(Item item) {
        super(item);
    }

    public static Forms get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'forms' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Forms(item);
    }

    public static Forms newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
