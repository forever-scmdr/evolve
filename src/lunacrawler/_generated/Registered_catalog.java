
package lunacrawler._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Registered_catalog
    extends Item
{

    public final static String _NAME = "registered_catalog";

    private Registered_catalog(Item item) {
        super(item);
    }

    public static Registered_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'registered_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Registered_catalog(item);
    }

    public static Registered_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
