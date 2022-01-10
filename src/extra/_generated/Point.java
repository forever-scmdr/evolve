
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Point
    extends Item
{

    public final static String _NAME = "point";
    public final static String NAME = "name";
    public final static String ADDRESS = "address";
    public final static String COORDS = "coords";
    public final static String TEXT = "text";

    private Point(Item item) {
        super(item);
    }

    public static Point get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'point' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Point(item);
    }

    public static Point newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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

    public void set_coords(String value) {
        setValue("coords", value);
    }

    public String get_coords() {
        return getStringValue("coords");
    }

    public String getDefault_coords(String defaultVal) {
        return getStringValue("coords", defaultVal);
    }

    public boolean contains_coords(String value) {
        return containsValue("coords", value);
    }

    public void set_text(String value) {
        setValue("text", value);
    }

    public String get_text() {
        return getStringValue("text");
    }

    public String getDefault_text(String defaultVal) {
        return getStringValue("text", defaultVal);
    }

    public boolean contains_text(String value) {
        return containsValue("text", value);
    }

}
