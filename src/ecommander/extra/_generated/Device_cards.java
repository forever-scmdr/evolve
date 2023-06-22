
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Device_cards
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "device_cards";

    private Device_cards(Item item) {
        super(item);
    }

    public static Device_cards get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'device_cards' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Device_cards(item);
    }

    public static Device_cards newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
