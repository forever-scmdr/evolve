
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Product_container
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "product_container";

    private Product_container(Item item) {
        super(item);
    }

    public static Product_container get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'product_container' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Product_container(item);
    }

    public static Product_container newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
