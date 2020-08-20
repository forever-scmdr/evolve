
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Link
    extends Item
{

    public final static String _NAME = "link";
    public final static String NAME = "name";
    public final static String LINK = "link";
    public final static String STYLE = "style";

    private Link(Item item) {
        super(item);
    }

    public static Link get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'link' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Link(item);
    }

    public static Link newChild(Item parent) {
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

    public void set_style(String value) {
        setValue("style", value);
    }

    public String get_style() {
        return getStringValue("style");
    }

    public String getDefault_style(String defaultVal) {
        return getStringValue("style", defaultVal);
    }

    public boolean contains_style(String value) {
        return containsValue("style", value);
    }

}
