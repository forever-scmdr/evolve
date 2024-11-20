
package ecommander.extra._generated;

import java.util.List;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

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

    public void set_slogan(String value) {
        setValue("slogan", value);
    }

    public String get_slogan() {
        return getStringValue("slogan");
    }

    public String getDefault_slogan(String defaultVal) {
        return getStringValue("slogan", defaultVal);
    }

    public boolean contains_slogan(String value) {
        return containsValue("slogan", value);
    }

    public void set_address(String value) {
        setValue("address", value);
    }

    public String get_address() {
        return getStringValue("address");
    }

    public String getDefault_address(String defaultVal) {
        return getStringValue("address", defaultVal);
    }

    public boolean contains_address(String value) {
        return containsValue("address", value);
    }

    public void set_email(String value) {
        setValue("email", value);
    }

    public String get_email() {
        return getStringValue("email");
    }

    public String getDefault_email(String defaultVal) {
        return getStringValue("email", defaultVal);
    }

    public boolean contains_email(String value) {
        return containsValue("email", value);
    }

    public void add_phone(String value) {
        setValue("phone", value);
    }

    public List<String> getAll_phone() {
        return getStringValues("phone");
    }

    public void remove_phone(String value) {
        removeEqualValue("phone", value);
    }

    public boolean contains_phone(String value) {
        return containsValue("phone", value);
    }

    public void set_getting_there(String value) {
        setValue("getting_there", value);
    }

    public String get_getting_there() {
        return getStringValue("getting_there");
    }

    public String getDefault_getting_there(String defaultVal) {
        return getStringValue("getting_there", defaultVal);
    }

    public boolean contains_getting_there(String value) {
        return containsValue("getting_there", value);
    }

    public void set_map(String value) {
        setValue("map", value);
    }

    public String get_map() {
        return getStringValue("map");
    }

    public String getDefault_map(String defaultVal) {
        return getStringValue("map", defaultVal);
    }

    public boolean contains_map(String value) {
        return containsValue("map", value);
    }

}
