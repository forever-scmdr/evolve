
package lunacrawler._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Seo_catalog
    extends Item
{

    public final static String _NAME = "seo_catalog";

    private Seo_catalog(Item item) {
        super(item);
    }

    public static Seo_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'seo_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Seo_catalog(item);
    }

    public static Seo_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
