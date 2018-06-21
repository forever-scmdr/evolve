
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Seo
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "seo";

    private Seo(Item item) {
        super(item);
    }

    public static Seo get(Item item) {
        if (item == null) {
            return null;
        }
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

    public void set_meta(String value) {
        setValue("meta", value);
    }

    public String get_meta() {
        return getStringValue("meta");
    }

    public String getDefault_meta(String defaultVal) {
        return getStringValue("meta", defaultVal);
    }

    public boolean contains_meta(String value) {
        return containsValue("meta", value);
    }

}
