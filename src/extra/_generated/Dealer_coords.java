
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Dealer_coords
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "dealer_coords";

    private Dealer_coords(Item item) {
        super(item);
    }

    public static Dealer_coords get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'dealer_coords' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Dealer_coords(item);
    }

    public static Dealer_coords newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_name(String value) {
        setValue("name", value);
    }

    public String get_name() {
        return getStringValue("name");
    }

    public String getDefault_name(String defaultVal) {
        return getStringValue("name", defaultVal);
    }

    public boolean contains_name(String value) {
        return containsValue("name", value);
    }

    public void set_address(String value) {
        setValue("address", value);
    }

    public String get_address() {
        return getStringValue("address");
    }

    public String getDefault_address(String defaultVal) {
        return getStringValue("address", defaultVal);
    }

    public boolean contains_address(String value) {
        return containsValue("address", value);
    }

    public void set_info(String value) {
        setValue("info", value);
    }

    public String get_info() {
        return getStringValue("info");
    }

    public String getDefault_info(String defaultVal) {
        return getStringValue("info", defaultVal);
    }

    public boolean contains_info(String value) {
        return containsValue("info", value);
    }

}
