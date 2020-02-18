
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Seo_container
    extends Item
{

    public final static String _NAME = "seo_container";

    private Seo_container(Item item) {
        super(item);
    }

    public static Seo_container get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'seo_container' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Seo_container(item);
    }

    public static Seo_container newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
