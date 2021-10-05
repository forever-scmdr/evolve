
package ecommander.extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Top_gal
    extends Item
{

    public final static String _NAME = "top_gal";
    public final static String MAIN_PIC = "main_pic";
    public final static String MEDIUM_PIC = "medium_pic";
    public final static String SMALL_PIC = "small_pic";

    private Top_gal(Item item) {
        super(item);
    }

    public static Top_gal get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'top_gal' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Top_gal(item);
    }

    public static Top_gal newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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
