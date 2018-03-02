
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Common
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "common";

    private Common(Item item) {
        super(item);
    }

    public static Common get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'common' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Common(item);
    }

    public static Common newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_top(String value) {
        setValue("top", value);
    }

    public String get_top() {
        return getStringValue("top");
    }

    public String getDefault_top(String defaultVal) {
        return getStringValue("top", defaultVal);
    }

    public boolean contains_top(String value) {
        return containsValue("top", value);
    }

    public void set_left(String value) {
        setValue("left", value);
    }

    public String get_left() {
        return getStringValue("left");
    }

    public String getDefault_left(String defaultVal) {
        return getStringValue("left", defaultVal);
    }

    public boolean contains_left(String value) {
        return containsValue("left", value);
    }

    public void set_bottom(String value) {
        setValue("bottom", value);
    }

    public String get_bottom() {
        return getStringValue("bottom");
    }

    public String getDefault_bottom(String defaultVal) {
        return getStringValue("bottom", defaultVal);
    }

    public boolean contains_bottom(String value) {
        return containsValue("bottom", value);
    }

    public void set_bottom_address(String value) {
        setValue("bottom_address", value);
    }

    public String get_bottom_address() {
        return getStringValue("bottom_address");
    }

    public String getDefault_bottom_address(String defaultVal) {
        return getStringValue("bottom_address", defaultVal);
    }

    public boolean contains_bottom_address(String value) {
        return containsValue("bottom_address", value);
    }

}
