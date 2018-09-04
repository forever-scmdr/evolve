
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Agent_catalog
    extends Item
{

    public final static String _NAME = "agent_catalog";

    private Agent_catalog(Item item) {
        super(item);
    }

    public static Agent_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'agent_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Agent_catalog(item);
    }

    public static Agent_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
