
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.util.List;

public class Delivery
    extends Item
{

    public final static String _NAME = "delivery";
    public final static String OPTION = "option";

    private Delivery(Item item) {
        super(item);
    }

    public static Delivery get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'delivery' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Delivery(item);
    }

    public static Delivery newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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
