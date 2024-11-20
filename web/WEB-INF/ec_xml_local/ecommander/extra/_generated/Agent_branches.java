
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Agent_branches
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "agent_branches";

    private Agent_branches(Item item) {
        super(item);
    }

    public static Agent_branches get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'agent_branches' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Agent_branches(item);
    }

    public static Agent_branches newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
