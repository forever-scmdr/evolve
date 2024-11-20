
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Dealer
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "dealer";

    private Dealer(Item item) {
        super(item);
    }

    public static Dealer get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'dealer' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Dealer(item);
    }

    public static Dealer newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_city(String value) {
        setValue("city", value);
    }

    public String get_city() {
        return getStringValue("city");
    }

    public String getDefault_city(String defaultVal) {
        return getStringValue("city", defaultVal);
    }

    public boolean contains_city(String value) {
        return containsValue("city", value);
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

    public void set_phones(String value) {
        setValue("phones", value);
    }

    public String get_phones() {
        return getStringValue("phones");
    }

    public String getDefault_phones(String defaultVal) {
        return getStringValue("phones", defaultVal);
    }

    public boolean contains_phones(String value) {
        return containsValue("phones", value);
    }

    public void set_extra(String value) {
        setValue("extra", value);
    }

    public String get_extra() {
        return getStringValue("extra");
    }

    public String getDefault_extra(String defaultVal) {
        return getStringValue("extra", defaultVal);
    }

    public boolean contains_extra(String value) {
        return containsValue("extra", value);
    }

}
