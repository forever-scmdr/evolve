
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Named_code
    extends Item
{

    public final static String _NAME = "named_code";
    public final static String NAME = "name";
    public final static String URL = "url";
    public final static String CODE = "code";
    public final static String PLACE = "place";

    private Named_code(Item item) {
        super(item);
    }

    public static Named_code get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'named_code' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Named_code(item);
    }

    public static Named_code newChild(Item parent) {
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

    public void set_url(String value) {
        setValue("url", value);
    }

    public String get_url() {
        return getStringValue("url");
    }

    public String getDefault_url(String defaultVal) {
        return getStringValue("url", defaultVal);
    }

    public boolean contains_url(String value) {
        return containsValue("url", value);
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

    public void set_place(String value) {
        setValue("place", value);
    }

    public String get_place() {
        return getStringValue("place");
    }

    public String getDefault_place(String defaultVal) {
        return getStringValue("place", defaultVal);
    }

    public boolean contains_place(String value) {
        return containsValue("place", value);
    }

}
