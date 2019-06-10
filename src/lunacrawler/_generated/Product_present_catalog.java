
package lunacrawler._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Product_present_catalog
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "product_present_catalog";

    private Product_present_catalog(Item item) {
        super(item);
    }

    public static Product_present_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'product_present_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Product_present_catalog(item);
    }

    public static Product_present_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
