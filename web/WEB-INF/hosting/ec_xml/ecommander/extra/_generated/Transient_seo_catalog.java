
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Transient_seo_catalog
    extends Item
{

    public final static String _NAME = "transient_seo_catalog";

    private Transient_seo_catalog(Item item) {
        super(item);
    }

    public static Transient_seo_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'transient_seo_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Transient_seo_catalog(item);
    }

    public static Transient_seo_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
