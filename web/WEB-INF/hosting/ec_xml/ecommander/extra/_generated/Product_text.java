
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Product_text
    extends Item
{

    public final static String _NAME = "product_text";
    public final static String NAME = "name";
    public final static String ICON = "icon";
    public final static String LINK = "link";
    public final static String TEXT = "text";

    private Product_text(Item item) {
        super(item);
    }

    public static Product_text get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'product_text' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Product_text(item);
    }

    public static Product_text newChild(Item parent) {
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

    public void set_icon(String value) {
        setValue("icon", value);
    }

    public String get_icon() {
        return getStringValue("icon");
    }

    public String getDefault_icon(String defaultVal) {
        return getStringValue("icon", defaultVal);
    }

    public boolean contains_icon(String value) {
        return containsValue("icon", value);
    }

    public void set_link(String value) {
        setValue("link", value);
    }

    public String get_link() {
        return getStringValue("link");
    }

    public String getDefault_link(String defaultVal) {
        return getStringValue("link", defaultVal);
    }

    public boolean contains_link(String value) {
        return containsValue("link", value);
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
