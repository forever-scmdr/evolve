
package extra._generated;

import java.util.List;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Type_mask
    extends Item
{

    public final static String _NAME = "type_mask";
    public final static String NAME = "name";
    public final static String MASK = "mask";

    private Type_mask(Item item) {
        super(item);
    }

    public static Type_mask get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'type_mask' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Type_mask(item);
    }

    public static Type_mask newChild(Item parent) {
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

    public void add_mask(String value) {
        setValue("mask", value);
    }

    public List<String> getAll_mask() {
        return getStringValues("mask");
    }

    public void remove_mask(String value) {
        removeEqualValue("mask", value);
    }

    public boolean contains_mask(String value) {
        return containsValue("mask", value);
    }

}
