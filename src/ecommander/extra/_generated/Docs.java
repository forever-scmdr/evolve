
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Docs
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "docs";

    private Docs(Item item) {
        super(item);
    }

    public static Docs get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'docs' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Docs(item);
    }

    public static Docs newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
