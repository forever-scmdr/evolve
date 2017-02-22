
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Feedback_form
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "feedback_form";

    private Feedback_form(Item item) {
        super(item);
    }

    public static Feedback_form get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'feedback_form' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Feedback_form(item);
    }

    public static Feedback_form newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_message(String value) {
        setValue("message", value);
    }

    public String get_message() {
        return getStringValue("message");
    }

    public String getDefault_message(String defaultVal) {
        return getStringValue("message", defaultVal);
    }

    public boolean contains_message(String value) {
        return containsValue("message", value);
    }

}
