
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Product_with_params
    extends Item
{

    public final static String _NAME = "product_with_params";

    private Product_with_params(Item item) {
        super(item);
    }

    public static Product_with_params get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'product_with_params' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Product_with_params(item);
    }

    public static Product_with_params newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
