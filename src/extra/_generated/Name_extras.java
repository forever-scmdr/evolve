
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Name_extras
    extends Item
{

    public final static String _NAME = "name_extras";
    public final static String TYPE = "type";
    public final static String NAME_EXTRA = "name_extra";

    private Name_extras(Item item) {
        super(item);
    }

    public static Name_extras get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'name_extras' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Name_extras(item);
    }

    public static Name_extras newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_type(String value) {
        setValue("type", value);
    }

    public String get_type() {
        return getStringValue("type");
    }

    public String getDefault_type(String defaultVal) {
        return getStringValue("type", defaultVal);
    }

    public boolean contains_type(String value) {
        return containsValue("type", value);
    }

    public void set_name_extra(String value) {
        setValue("name_extra", value);
    }

    public String get_name_extra() {
        return getStringValue("name_extra");
    }

    public String getDefault_name_extra(String defaultVal) {
        return getStringValue("name_extra", defaultVal);
    }

    public boolean contains_name_extra(String value) {
        return containsValue("name_extra", value);
    }

}
