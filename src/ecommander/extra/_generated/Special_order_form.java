
package ecommander.extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.io.File;

public class Special_order_form
    extends Item
{

    public final static String _NAME = "special_order_form";
    public final static String NAME = "name";
    public final static String PHONE = "phone";
    public final static String EMAIL = "email";
    public final static String FILE = "file";
    public final static String MESSAGE = "message";

    private Special_order_form(Item item) {
        super(item);
    }

    public static Special_order_form get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'special_order_form' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Special_order_form(item);
    }

    public static Special_order_form newChild(Item parent) {
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

    public void set_file(File value) {
        setValue("file", value);
    }

    public File get_file() {
        return getFileValue("file", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_file(File value) {
        return containsValue("file", value);
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
