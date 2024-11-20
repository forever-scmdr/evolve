
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Agent_types
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "agent_types";

    private Agent_types(Item item) {
        super(item);
    }

    public static Agent_types get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'agent_types' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Agent_types(item);
    }

    public static Agent_types newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
