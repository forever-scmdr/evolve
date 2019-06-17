
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Catalog_texts
    extends Item
{

    public final static String _NAME = "catalog_texts";
    public final static String PAYMENT = "payment";

    private Catalog_texts(Item item) {
        super(item);
    }

    public static Catalog_texts get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'catalog_texts' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Catalog_texts(item);
    }

    public static Catalog_texts newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_payment(String value) {
        setValue("payment", value);
    }

    public String get_payment() {
        return getStringValue("payment");
    }

    public String getDefault_payment(String defaultVal) {
        return getStringValue("payment", defaultVal);
    }

    public boolean contains_payment(String value) {
        return containsValue("payment", value);
    }

}
