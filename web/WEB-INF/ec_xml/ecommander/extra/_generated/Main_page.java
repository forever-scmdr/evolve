
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Main_page
    extends Item
{

    public final static String _NAME = "main_page";

    private Main_page(Item item) {
        super(item);
    }

    public static Main_page get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'main_page' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Main_page(item);
    }

    public static Main_page newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
