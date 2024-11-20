
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Footer
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "footer";

    private Footer(Item item) {
        super(item);
    }

    public static Footer get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'footer' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Footer(item);
    }

    public static Footer newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
