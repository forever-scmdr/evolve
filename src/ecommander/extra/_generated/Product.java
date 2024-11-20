
package ecommander.extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

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

    public void set_search_string(String value) {
        setValue("search_string", value);
    }

    public String get_search_string() {
        return getStringValue("search_string");
    }

    public String getDefault_search_string(String defaultVal) {
        return getStringValue("search_string", defaultVal);
    }

    public boolean contains_search_string(String value) {
        return containsValue("search_string", value);
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

    public void set_alt(String value) {
        setValue("alt", value);
    }

    public String get_alt() {
        return getStringValue("alt");
    }

    public String getDefault_alt(String defaultVal) {
        return getStringValue("alt", defaultVal);
    }

    public boolean contains_alt(String value) {
        return containsValue("alt", value);
    }

    public void set_img_big(File value) {
        setValue("img_big", value);
    }

    public File get_img_big() {
        return getFileValue("img_big", AppContext.getFilesDirPath());
    }

    public boolean contains_img_big(File value) {
        return containsValue("img_big", value);
    }

    public void set_img_small(File value) {
        setValue("img_small", value);
    }

    public File get_img_small() {
        return getFileValue("img_small", AppContext.getFilesDirPath());
    }

    public boolean contains_img_small(File value) {
        return containsValue("img_small", value);
    }

    public void set_size(String value) {
        setValue("size", value);
    }

    public String get_size() {
        return getStringValue("size");
    }

    public String getDefault_size(String defaultVal) {
        return getStringValue("size", defaultVal);
    }

    public boolean contains_size(String value) {
        return containsValue("size", value);
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

    public void add_tag(String value) {
        setValue("tag", value);
    }

    public List<String> getAll_tag() {
        return getStringValues("tag");
    }

    public void remove_tag(String value) {
        removeEqualValue("tag", value);
    }

    public boolean contains_tag(String value) {
        return containsValue("tag", value);
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

    public void set_mods(String value) {
        setValue("mods", value);
    }

    public String get_mods() {
        return getStringValue("mods");
    }

    public String getDefault_mods(String defaultVal) {
        return getStringValue("mods", defaultVal);
    }

    public boolean contains_mods(String value) {
        return containsValue("mods", value);
    }

    public void add_doc_assoc(Long value) {
        setValue("doc_assoc", value);
    }

    public void addUI_doc_assoc(String value)
        throws Exception
    {
        setValueUI("doc_assoc", value);
    }

    public List<Long> getAll_doc_assoc() {
        return getLongValues("doc_assoc");
    }

    public void remove_doc_assoc(Long value) {
        removeEqualValue("doc_assoc", value);
    }

    public boolean contains_doc_assoc(Long value) {
        return containsValue("doc_assoc", value);
    }

}
