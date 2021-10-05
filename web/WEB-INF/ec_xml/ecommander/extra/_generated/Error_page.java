
package ecommander.extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.io.File;
import java.util.List;

public class Error_page
    extends Item
{

    public final static String _NAME = "error_page";
    public final static String TEXT = "text";
    public final static String TEXT_PIC = "text_pic";

    private Error_page(Item item) {
        super(item);
    }

    public static Error_page get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'error_page' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Error_page(item);
    }

    public static Error_page newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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

    public void add_text_pic(File value) {
        setValue("text_pic", value);
    }

    public List<File> getAll_text_pic() {
        return getFileValues("text_pic", AppContext.getCommonFilesDirPath());
    }

    public void remove_text_pic(File value) {
        removeEqualValue("text_pic", value);
    }

    public boolean contains_text_pic(File value) {
        return containsValue("text_pic", value);
    }

}
