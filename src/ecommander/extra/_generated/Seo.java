
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Seo
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "seo";

    private Seo(Item item) {
        super(item);
    }

    public static Seo get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'seo' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Seo(item);
    }

    public static Seo newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_h1(String value) {
        setValue("h1", value);
    }

    public String get_h1() {
        return getStringValue("h1");
    }

    public String getDefault_h1(String defaultVal) {
        return getStringValue("h1", defaultVal);
    }

    public boolean contains_h1(String value) {
        return containsValue("h1", value);
    }

    public void set_keywords(String value) {
        setValue("keywords", value);
    }

    public String get_keywords() {
        return getStringValue("keywords");
    }

    public String getDefault_keywords(String defaultVal) {
        return getStringValue("keywords", defaultVal);
    }

    public boolean contains_keywords(String value) {
        return containsValue("keywords", value);
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

    public void set_progon(String value) {
        setValue("progon", value);
    }

    public String get_progon() {
        return getStringValue("progon");
    }

    public String getDefault_progon(String defaultVal) {
        return getStringValue("progon", defaultVal);
    }

    public boolean contains_progon(String value) {
        return containsValue("progon", value);
    }

}
