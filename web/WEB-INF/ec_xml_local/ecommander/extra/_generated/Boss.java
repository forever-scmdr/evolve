
package ecommander.extra._generated;

import java.util.List;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Boss
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "boss";

    private Boss(Item item) {
        super(item);
    }

    public static Boss get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'boss' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Boss(item);
    }

    public static Boss newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_position(String value) {
        setValue("position", value);
    }

    public String get_position() {
        return getStringValue("position");
    }

    public String getDefault_position(String defaultVal) {
        return getStringValue("position", defaultVal);
    }

    public boolean contains_position(String value) {
        return containsValue("position", value);
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

    public void add_email(String value) {
        setValue("email", value);
    }

    public List<String> getAll_email() {
        return getStringValues("email");
    }

    public void remove_email(String value) {
        removeEqualValue("email", value);
    }

    public boolean contains_email(String value) {
        return containsValue("email", value);
    }

    public void add_extra(String value) {
        setValue("extra", value);
    }

    public List<String> getAll_extra() {
        return getStringValues("extra");
    }

    public void remove_extra(String value) {
        removeEqualValue("extra", value);
    }

    public boolean contains_extra(String value) {
        return containsValue("extra", value);
    }

}
