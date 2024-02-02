
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Search_catalog
    extends Item
{

    public final static String _NAME = "search_catalog";

    private Search_catalog(Item item) {
        super(item);
    }

    public static Search_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'search_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Search_catalog(item);
    }

    public static Search_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
