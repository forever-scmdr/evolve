
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Generic_form
    extends Item
{

    public final static String _NAME = "generic_form";

    private Generic_form(Item item) {
        super(item);
    }

    public static Generic_form get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'generic_form' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Generic_form(item);
    }

    public static Generic_form newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
