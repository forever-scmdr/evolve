
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Params_xml
    extends Item
{

    public final static String _NAME = "params_xml";
    public final static String XML = "xml";

    private Params_xml(Item item) {
        super(item);
    }

    public static Params_xml get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'params_xml' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Params_xml(item);
    }

    public static Params_xml newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_xml(String value) {
        setValue("xml", value);
    }

    public String get_xml() {
        return getStringValue("xml");
    }

    public String getDefault_xml(String defaultVal) {
        return getStringValue("xml", defaultVal);
    }

    public boolean contains_xml(String value) {
        return containsValue("xml", value);
    }

}
