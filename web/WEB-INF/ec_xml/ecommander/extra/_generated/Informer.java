
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Informer
    extends Item
{

    public final static String _NAME = "informer";
    public final static String NAME = "name";
    public final static String PRO_NAME = "pro_name";

    private Informer(Item item) {
        super(item);
    }

    public static Informer get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'informer' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Informer(item);
    }

    public static Informer newChild(Item parent) {
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

    public void set_pro_name(String value) {
        setValue("pro_name", value);
    }

    public String get_pro_name() {
        return getStringValue("pro_name");
    }

    public String getDefault_pro_name(String defaultVal) {
        return getStringValue("pro_name", defaultVal);
    }

    public boolean contains_pro_name(String value) {
        return containsValue("pro_name", value);
    }

}
