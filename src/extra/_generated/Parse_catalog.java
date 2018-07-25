
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Parse_catalog
    extends Item
{

    public final static String _NAME = "parse_catalog";

    private Parse_catalog(Item item) {
        super(item);
    }

    public static Parse_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'parse_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Parse_catalog(item);
    }

    public static Parse_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
