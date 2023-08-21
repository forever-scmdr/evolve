
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Domain_dependent_display
    extends Item
{

    public final static String _NAME = "domain_dependent_display";
    public final static String TYPE = "type";

    private Domain_dependent_display(Item item) {
        super(item);
    }

    public static Domain_dependent_display get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'domain_dependent_display' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Domain_dependent_display(item);
    }

    public static Domain_dependent_display newChild(Item parent) {
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

}
