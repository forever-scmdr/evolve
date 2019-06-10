
package lunacrawler._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Soc
    extends Item
{

    public final static String _NAME = "soc";
    public final static String CODE = "code";

    private Soc(Item item) {
        super(item);
    }

    public static Soc get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'soc' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Soc(item);
    }

    public static Soc newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_code(String value) {
        setValue("code", value);
    }

    public String get_code() {
        return getStringValue("code");
    }

    public String getDefault_code(String defaultVal) {
        return getStringValue("code", defaultVal);
    }

    public boolean contains_code(String value) {
        return containsValue("code", value);
    }

}
