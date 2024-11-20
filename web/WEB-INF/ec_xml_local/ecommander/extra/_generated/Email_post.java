
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Email_post
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "email_post";

    private Email_post(Item item) {
        super(item);
    }

    public static Email_post get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'email_post' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Email_post(item);
    }

    public static Email_post newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_topic(String value) {
        setValue("topic", value);
    }

    public String get_topic() {
        return getStringValue("topic");
    }

    public String getDefault_topic(String defaultVal) {
        return getStringValue("topic", defaultVal);
    }

    public boolean contains_topic(String value) {
        return containsValue("topic", value);
    }

    public void set_date(Long value) {
        setValue("date", value);
    }

    public void setUI_date(String value)
        throws Exception
    {
        setValueUI("date", value);
    }

    public Long get_date() {
        return getLongValue("date");
    }

    public Long getDefault_date(Long defaultVal) {
        return getLongValue("date", defaultVal);
    }

    public boolean contains_date(Long value) {
        return containsValue("date", value);
    }

    public void set_number(String value) {
        setValue("number", value);
    }

    public String get_number() {
        return getStringValue("number");
    }

    public String getDefault_number(String defaultVal) {
        return getStringValue("number", defaultVal);
    }

    public boolean contains_number(String value) {
        return containsValue("number", value);
    }

    public void set_template(String value) {
        setValue("template", value);
    }

    public String get_template() {
        return getStringValue("template");
    }

    public String getDefault_template(String defaultVal) {
        return getStringValue("template", defaultVal);
    }

    public boolean contains_template(String value) {
        return containsValue("template", value);
    }

    public void set_header(String value) {
        setValue("header", value);
    }

    public String get_header() {
        return getStringValue("header");
    }

    public String getDefault_header(String defaultVal) {
        return getStringValue("header", defaultVal);
    }

    public boolean contains_header(String value) {
        return containsValue("header", value);
    }

    public void set_body(String value) {
        setValue("body", value);
    }

    public String get_body() {
        return getStringValue("body");
    }

    public String getDefault_body(String defaultVal) {
        return getStringValue("body", defaultVal);
    }

    public boolean contains_body(String value) {
        return containsValue("body", value);
    }

    public void set_main_pic(File value) {
        setValue("main_pic", value);
    }

    public File get_main_pic() {
        return getFileValue("main_pic", AppContext.getFilesDirPath());
    }

    public boolean contains_main_pic(File value) {
        return containsValue("main_pic", value);
    }

    public void set_pic(File value) {
        setValue("pic", value);
    }

    public File get_pic() {
        return getFileValue("pic", AppContext.getFilesDirPath());
    }

    public boolean contains_pic(File value) {
        return containsValue("pic", value);
    }

}
