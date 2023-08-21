
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Block_array
    extends Item
{

    public final static String _NAME = "block_array";

    private Block_array(Item item) {
        super(item);
    }

    public static Block_array get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'block_array' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Block_array(item);
    }

    public static Block_array newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
