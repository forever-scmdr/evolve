
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Xml_filter
    extends Item
{

    public final static String _NAME = "xml_filter";
    public final static String XML_FILTER = "xml_filter";

    private Xml_filter(Item item) {
        super(item);
    }

    public static Xml_filter get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'xml_filter' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Xml_filter(item);
    }

    public static Xml_filter newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_xml_filter(String value) {
        setValue("xml_filter", value);
    }

    public String get_xml_filter() {
        return getStringValue("xml_filter");
    }

    public String getDefault_xml_filter(String defaultVal) {
        return getStringValue("xml_filter", defaultVal);
    }

    public boolean contains_xml_filter(String value) {
        return containsValue("xml_filter", value);
    }

}
