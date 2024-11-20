
package ecommander.extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Section
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "section";

    private Section(Item item) {
        super(item);
    }

    public static Section get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'section' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Section(item);
    }

    public static Section newChild(Item parent) {
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

    public void set_serial_sample(String value) {
        setValue("serial_sample", value);
    }

    public String get_serial_sample() {
        return getStringValue("serial_sample");
    }

    public String getDefault_serial_sample(String defaultVal) {
        return getStringValue("serial_sample", defaultVal);
    }

    public boolean contains_serial_sample(String value) {
        return containsValue("serial_sample", value);
    }

    public void add_filter_tag(String value) {
        setValue("filter_tag", value);
    }

    public List<String> getAll_filter_tag() {
        return getStringValues("filter_tag");
    }

    public void remove_filter_tag(String value) {
        removeEqualValue("filter_tag", value);
    }

    public boolean contains_filter_tag(String value) {
        return containsValue("filter_tag", value);
    }

    public void set_text(String value) {
        setValue("text", value);
    }

    public String get_text() {
        return getStringValue("text");
    }

    public String getDefault_text(String defaultVal) {
        return getStringValue("text", defaultVal);
    }

    public boolean contains_text(String value) {
        return containsValue("text", value);
    }

    public void set_use(String value) {
        setValue("use", value);
    }

    public String get_use() {
        return getStringValue("use");
    }

    public String getDefault_use(String defaultVal) {
        return getStringValue("use", defaultVal);
    }

    public boolean contains_use(String value) {
        return containsValue("use", value);
    }

    public void add_picture(File value) {
        setValue("picture", value);
    }

    public List<File> getAll_picture() {
        return getFileValues("picture", AppContext.getFilesDirPath());
    }

    public void remove_picture(File value) {
        removeEqualValue("picture", value);
    }

    public boolean contains_picture(File value) {
        return containsValue("picture", value);
    }

}
