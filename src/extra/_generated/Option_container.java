
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Option_container
    extends Item
{

    public final static String _NAME = "option_container";

    private Option_container(Item item) {
        super(item);
    }

    public static Option_container get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'option_container' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Option_container(item);
    }

    public static Option_container newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
