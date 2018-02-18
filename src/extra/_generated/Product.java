
package extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Product
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "product";

    private Product(Item item) {
        super(item);
    }

    public static Product get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'product' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Product(item);
    }

    public static Product newChild(Item parent) {
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

    public void set_code(String value) {
        setValue("code", value);
    }

    public String get_code() {
        return getStringValue("code");
    }

    public String getDefault_code(String defaultVal) {
        return getStringValue("code", defaultVal);
    }

    public boolean contains_code(String value) {
        return containsValue("code", value);
    }

    public void set_main_pic(File value) {
        setValue("main_pic", value);
    }

    public File get_main_pic() {
        return getFileValue("main_pic", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_main_pic(File value) {
        return containsValue("main_pic", value);
    }

    public void set_price(Double value) {
        setValue("price", value);
    }

    public void setUI_price(String value)
        throws Exception
    {
        setValueUI("price", value);
    }

    public Double get_price() {
        return getDoubleValue("price");
    }

    public Double getDefault_price(Double defaultVal) {
        return getDoubleValue("price", defaultVal);
    }

    public boolean contains_price(Double value) {
        return containsValue("price", value);
    }

    public void set_short(String value) {
        setValue("short", value);
    }

    public String get_short() {
        return getStringValue("short");
    }

    public String getDefault_short(String defaultVal) {
        return getStringValue("short", defaultVal);
    }

    public boolean contains_short(String value) {
        return containsValue("short", value);
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

    public void set_tech(String value) {
        setValue("tech", value);
    }

    public String get_tech() {
        return getStringValue("tech");
    }

    public String getDefault_tech(String defaultVal) {
        return getStringValue("tech", defaultVal);
    }

    public boolean contains_tech(String value) {
        return containsValue("tech", value);
    }

    public void set_apply(String value) {
        setValue("apply", value);
    }

    public String get_apply() {
        return getStringValue("apply");
    }

    public String getDefault_apply(String defaultVal) {
        return getStringValue("apply", defaultVal);
    }

    public boolean contains_apply(String value) {
        return containsValue("apply", value);
    }

    public void add_accessiories(String value) {
        setValue("accessiories", value);
    }

    public List<String> getAll_accessiories() {
        return getStringValues("accessiories");
    }

    public void remove_accessiories(String value) {
        removeEqualValue("accessiories", value);
    }

    public boolean contains_accessiories(String value) {
        return containsValue("accessiories", value);
    }

    public void add_sets(String value) {
        setValue("sets", value);
    }

    public List<String> getAll_sets() {
        return getStringValues("sets");
    }

    public void remove_sets(String value) {
        removeEqualValue("sets", value);
    }

    public boolean contains_sets(String value) {
        return containsValue("sets", value);
    }

    public void add_probes(String value) {
        setValue("probes", value);
    }

    public List<String> getAll_probes() {
        return getStringValues("probes");
    }

    public void remove_probes(String value) {
        removeEqualValue("probes", value);
    }

    public boolean contains_probes(String value) {
        return containsValue("probes", value);
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

    public void add_text_pics(File value) {
        setValue("text_pics", value);
    }

    public List<File> getAll_text_pics() {
        return getFileValues("text_pics", AppContext.getCommonFilesDirPath());
    }

    public void remove_text_pics(File value) {
        removeEqualValue("text_pics", value);
    }

    public boolean contains_text_pics(File value) {
        return containsValue("text_pics", value);
    }

    public void add_video(String value) {
        setValue("video", value);
    }

    public List<String> getAll_video() {
        return getStringValues("video");
    }

    public void remove_video(String value) {
        removeEqualValue("video", value);
    }

    public boolean contains_video(String value) {
        return containsValue("video", value);
    }

}
