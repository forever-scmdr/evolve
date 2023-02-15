
package extra._generated;

import java.util.List;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Analog
    extends Item
{

    public final static String _NAME = "analog";
    public final static String NAME = "name";
    public final static String SET = "set";

    private Analog(Item item) {
        super(item);
    }

    public static Analog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'analog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Analog(item);
    }

    public static Analog newChild(Item parent) {
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

    public void add_set(String value) {
        setValue("set", value);
    }

    public List<String> getAll_set() {
        return getStringValues("set");
    }

    public void remove_set(String value) {
        removeEqualValue("set", value);
    }

    public boolean contains_set(String value) {
        return containsValue("set", value);
    }

}
