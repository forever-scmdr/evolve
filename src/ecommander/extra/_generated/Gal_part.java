
package ecommander.extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.io.File;
import java.util.List;

public class Gal_part
    extends Item
{

    public final static String _NAME = "gal_part";
    public final static String NAME = "name";
    public final static String TEXT = "text";
    public final static String MAIN_PIC = "main_pic";
    public final static String MEDIUM_PIC = "medium_pic";
    public final static String SMALL_PIC = "small_pic";

    private Gal_part(Item item) {
        super(item);
    }

    public static Gal_part get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'gal_part' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Gal_part(item);
    }

    public static Gal_part newChild(Item parent) {
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

    public void add_main_pic(File value) {
        setValue("main_pic", value);
    }

    public List<File> getAll_main_pic() {
        return getFileValues("main_pic", AppContext.getCommonFilesDirPath());
    }

    public void remove_main_pic(File value) {
        removeEqualValue("main_pic", value);
    }

    public boolean contains_main_pic(File value) {
        return containsValue("main_pic", value);
    }

    public void add_medium_pic(File value) {
        setValue("medium_pic", value);
    }

    public List<File> getAll_medium_pic() {
        return getFileValues("medium_pic", AppContext.getCommonFilesDirPath());
    }

    public void remove_medium_pic(File value) {
        removeEqualValue("medium_pic", value);
    }

    public boolean contains_medium_pic(File value) {
        return containsValue("medium_pic", value);
    }

    public void add_small_pic(File value) {
        setValue("small_pic", value);
    }

    public List<File> getAll_small_pic() {
        return getFileValues("small_pic", AppContext.getCommonFilesDirPath());
    }

    public void remove_small_pic(File value) {
        removeEqualValue("small_pic", value);
    }

    public boolean contains_small_pic(File value) {
        return containsValue("small_pic", value);
    }

}
