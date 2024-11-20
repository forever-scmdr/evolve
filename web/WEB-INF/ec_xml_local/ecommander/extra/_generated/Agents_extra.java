
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Agents_extra
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "agents_extra";

    private Agents_extra(Item item) {
        super(item);
    }

    public static Agents_extra get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'agents_extra' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Agents_extra(item);
    }

    public static Agents_extra newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
