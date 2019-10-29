
package ecommander.extra._generated;

import java.util.List;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Payment
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "payment";

    private Payment(Item item) {
        super(item);
    }

    public static Payment get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'payment' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Payment(item);
    }

    public static Payment newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void add_option(String value) {
        setValue("option", value);
    }

    public List<String> getAll_option() {
        return getStringValues("option");
    }

    public void remove_option(String value) {
        removeEqualValue("option", value);
    }

    public boolean contains_option(String value) {
        return containsValue("option", value);
    }

}
