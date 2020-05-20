
package ecommander.extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Simple_gallery
    extends Item
{

    public final static String _NAME = "simple_gallery";
    public final static String NAME = "name";
    public final static String HEIGHT = "height";
    public final static String WIDTH = "width";
    public final static String BORDER = "border";
    public final static String GUTTER = "gutter";
    public final static String PIC = "pic";

    private Simple_gallery(Item item) {
        super(item);
    }

    public static Simple_gallery get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'simple_gallery' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Simple_gallery(item);
    }

    public static Simple_gallery newChild(Item parent) {
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

    public void set_height(String value) {
        setValue("height", value);
    }

    public String get_height() {
        return getStringValue("height");
    }

    public String getDefault_height(String defaultVal) {
        return getStringValue("height", defaultVal);
    }

    public boolean contains_height(String value) {
        return containsValue("height", value);
    }

    public void set_width(String value) {
        setValue("width", value);
    }

    public String get_width() {
        return getStringValue("width");
    }

    public String getDefault_width(String defaultVal) {
        return getStringValue("width", defaultVal);
    }

    public boolean contains_width(String value) {
        return containsValue("width", value);
    }

    public void set_border(String value) {
        setValue("border", value);
    }

    public String get_border() {
        return getStringValue("border");
    }

    public String getDefault_border(String defaultVal) {
        return getStringValue("border", defaultVal);
    }

    public boolean contains_border(String value) {
        return containsValue("border", value);
    }

    public void set_gutter(String value) {
        setValue("gutter", value);
    }

    public String get_gutter() {
        return getStringValue("gutter");
    }

    public String getDefault_gutter(String defaultVal) {
        return getStringValue("gutter", defaultVal);
    }

    public boolean contains_gutter(String value) {
        return containsValue("gutter", value);
    }

    public void add_pic(File value) {
        setValue("pic", value);
    }

    public List<File> getAll_pic() {
        return getFileValues("pic", AppContext.getCommonFilesDirPath());
    }

    public void remove_pic(File value) {
        removeEqualValue("pic", value);
    }

    public boolean contains_pic(File value) {
        return containsValue("pic", value);
    }

}
