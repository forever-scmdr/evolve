
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Catalog_meta
    extends Item
{

    public final static String _NAME = "catalog_meta";

    private Catalog_meta(Item item) {
        super(item);
    }

    public static Catalog_meta get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'catalog_meta' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Catalog_meta(item);
    }

    public static Catalog_meta newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
