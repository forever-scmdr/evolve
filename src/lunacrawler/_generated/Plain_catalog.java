
package lunacrawler._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Plain_catalog
    extends Item
{

    public final static String _NAME = "plain_catalog";

    private Plain_catalog(Item item) {
        super(item);
    }

    public static Plain_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'plain_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Plain_catalog(item);
    }

    public static Plain_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
