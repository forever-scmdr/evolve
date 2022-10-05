
package extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Gallery
    extends Item
{

    public final static String _NAME = "gallery";
    public final static String GALLERY = "gallery";
    public final static String GALLERY_PATH = "gallery_path";

    private Gallery(Item item) {
        super(item);
    }

    public static Gallery get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'gallery' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Gallery(item);
    }

    public static Gallery newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void add_gallery(File value) {
        setValue("gallery", value);
    }

    public List<File> getAll_gallery() {
        return getFileValues("gallery", AppContext.getCommonFilesDirPath());
    }

    public void remove_gallery(File value) {
        removeEqualValue("gallery", value);
    }

    public boolean contains_gallery(File value) {
        return containsValue("gallery", value);
    }

    public void add_gallery_path(String value) {
        setValue("gallery_path", value);
    }

    public List<String> getAll_gallery_path() {
        return getStringValues("gallery_path");
    }

    public void remove_gallery_path(String value) {
        removeEqualValue("gallery_path", value);
    }

    public boolean contains_gallery_path(String value) {
        return containsValue("gallery_path", value);
    }

}
