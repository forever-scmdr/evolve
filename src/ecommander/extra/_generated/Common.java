
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Common
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "common";

    private Common(Item item) {
        super(item);
    }

    public static Common get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'common' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Common(item);
    }

    public static Common newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_phone(String value) {
        setValue("phone", value);
    }

    public String get_phone() {
        return getStringValue("phone");
    }

    public String getDefault_phone(String defaultVal) {
        return getStringValue("phone", defaultVal);
    }

    public boolean contains_phone(String value) {
        return containsValue("phone", value);
    }

    public void set_phone_hidden(String value) {
        setValue("phone_hidden", value);
    }

    public String get_phone_hidden() {
        return getStringValue("phone_hidden");
    }

    public String getDefault_phone_hidden(String defaultVal) {
        return getStringValue("phone_hidden", defaultVal);
    }

    public boolean contains_phone_hidden(String value) {
        return containsValue("phone_hidden", value);
    }

    public void set_copy(String value) {
        setValue("copy", value);
    }

    public String get_copy() {
        return getStringValue("copy");
    }

    public String getDefault_copy(String defaultVal) {
        return getStringValue("copy", defaultVal);
    }

    public boolean contains_copy(String value) {
        return containsValue("copy", value);
    }

    public void set_eur(Double value) {
        setValue("eur", value);
    }

    public void setUI_eur(String value)
        throws Exception
    {
        setValueUI("eur", value);
    }

    public Double get_eur() {
        return getDoubleValue("eur");
    }

    public Double getDefault_eur(Double defaultVal) {
        return getDoubleValue("eur", defaultVal);
    }

    public boolean contains_eur(Double value) {
        return containsValue("eur", value);
    }

}
