
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Product_extra_xml
    extends Item
{

    public final static String _NAME = "product_extra_xml";
    public final static String NAME = "name";
    public final static String XML = "xml";

    private Product_extra_xml(Item item) {
        super(item);
    }

    public static Product_extra_xml get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'product_extra_xml' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Product_extra_xml(item);
    }

    public static Product_extra_xml newChild(Item parent) {
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
