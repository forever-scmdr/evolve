
package lunacrawler._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Custom_pages
    extends Item
{

    public final static String _NAME = "custom_pages";

    private Custom_pages(Item item) {
        super(item);
    }

    public static Custom_pages get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'custom_pages' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Custom_pages(item);
    }

    public static Custom_pages newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
