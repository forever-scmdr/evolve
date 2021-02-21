
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Rss
    extends Item
{

    public final static String _NAME = "rss";
    public final static String TITLE = "title";
    public final static String DESCRIPTION = "description";
    public final static String LINK = "link";

    private Rss(Item item) {
        super(item);
    }

    public static Rss get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'rss' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Rss(item);
    }

    public static Rss newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_title(String value) {
        setValue("title", value);
    }

    public String get_title() {
        return getStringValue("title");
    }

    public String getDefault_title(String defaultVal) {
        return getStringValue("title", defaultVal);
    }

    public boolean contains_title(String value) {
        return containsValue("title", value);
    }

    public void set_description(String value) {
        setValue("description", value);
    }

    public String get_description() {
        return getStringValue("description");
    }

    public String getDefault_description(String defaultVal) {
        return getStringValue("description", defaultVal);
    }

    public boolean contains_description(String value) {
        return containsValue("description", value);
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

}
