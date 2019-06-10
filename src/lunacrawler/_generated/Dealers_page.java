
package lunacrawler._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Dealers_page
    extends Item
{

    public final static String _NAME = "dealers_page";

    private Dealers_page(Item item) {
        super(item);
    }

    public static Dealers_page get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'dealers_page' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Dealers_page(item);
    }

    public static Dealers_page newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
