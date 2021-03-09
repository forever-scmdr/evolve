
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Product_subscribe
    extends Item
{

    public final static String _NAME = "product_subscribe";
    public final static String NAME = "name";
    public final static String STATUS = "status";
    public final static String LINK_NAME = "link_name";

    private Product_subscribe(Item item) {
        super(item);
    }

    public static Product_subscribe get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'product_subscribe' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Product_subscribe(item);
    }

    public static Product_subscribe newChild(Item parent) {
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

    public void set_status(String value) {
        setValue("status", value);
    }

    public String get_status() {
        return getStringValue("status");
    }

    public String getDefault_status(String defaultVal) {
        return getStringValue("status", defaultVal);
    }

    public boolean contains_status(String value) {
        return containsValue("status", value);
    }

    public void set_link_name(String value) {
        setValue("link_name", value);
    }

    public String get_link_name() {
        return getStringValue("link_name");
    }

    public String getDefault_link_name(String defaultVal) {
        return getStringValue("link_name", defaultVal);
    }

    public boolean contains_link_name(String value) {
        return containsValue("link_name", value);
    }

}
