
package lunacrawler._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Original_gallery
    extends Item
{

    public final static String _NAME = "original_gallery";
    public final static String WATERMARK_FILENAME = "watermark_filename";
    public final static String WATERMARK_FILE_MODIFIED = "watermark_file_modified";
    public final static String GALLERY = "gallery";

    private Original_gallery(Item item) {
        super(item);
    }

    public static Original_gallery get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'original_gallery' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Original_gallery(item);
    }

    public static Original_gallery newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_watermark_filename(String value) {
        setValue("watermark_filename", value);
    }

    public String get_watermark_filename() {
        return getStringValue("watermark_filename");
    }

    public String getDefault_watermark_filename(String defaultVal) {
        return getStringValue("watermark_filename", defaultVal);
    }

    public boolean contains_watermark_filename(String value) {
        return containsValue("watermark_filename", value);
    }

    public void set_watermark_file_modified(Long value) {
        setValue("watermark_file_modified", value);
    }

    public void setUI_watermark_file_modified(String value)
        throws Exception
    {
        setValueUI("watermark_file_modified", value);
    }

    public Long get_watermark_file_modified() {
        return getLongValue("watermark_file_modified");
    }

    public Long getDefault_watermark_file_modified(Long defaultVal) {
        return getLongValue("watermark_file_modified", defaultVal);
    }

    public boolean contains_watermark_file_modified(Long value) {
        return containsValue("watermark_file_modified", value);
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

}
