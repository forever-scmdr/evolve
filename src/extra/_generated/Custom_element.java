
package extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Custom_element
    extends Item
{

    public final static String _NAME = "custom_element";
    public final static String NAME = "name";
    public final static String HEADER = "header";
    public final static String SUBHEADER = "subheader";
    public final static String TEXT = "text";
    public final static String IMAGE = "image";
    public final static String IMAGE_BGR = "image_bgr";
    public final static String YOUTUBE = "youtube";
    public final static String LINK = "link";

    private Custom_element(Item item) {
        super(item);
    }

    public static Custom_element get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'custom_element' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Custom_element(item);
    }

    public static Custom_element newChild(Item parent) {
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

    public void set_subheader(String value) {
        setValue("subheader", value);
    }

    public String get_subheader() {
        return getStringValue("subheader");
    }

    public String getDefault_subheader(String defaultVal) {
        return getStringValue("subheader", defaultVal);
    }

    public boolean contains_subheader(String value) {
        return containsValue("subheader", value);
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

    public void set_image(File value) {
        setValue("image", value);
    }

    public File get_image() {
        return getFileValue("image", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_image(File value) {
        return containsValue("image", value);
    }

    public void set_image_bgr(File value) {
        setValue("image_bgr", value);
    }

    public File get_image_bgr() {
        return getFileValue("image_bgr", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_image_bgr(File value) {
        return containsValue("image_bgr", value);
    }

    public void set_youtube(String value) {
        setValue("youtube", value);
    }

    public String get_youtube() {
        return getStringValue("youtube");
    }

    public String getDefault_youtube(String defaultVal) {
        return getStringValue("youtube", defaultVal);
    }

    public boolean contains_youtube(String value) {
        return containsValue("youtube", value);
    }

    public void set_link(String value) {
        setValue("link", value);
    }

    public String get_link() {
        return getStringValue("link");
    }

    public String getDefault_link(String defaultVal) {
        return getStringValue("link", defaultVal);
    }

    public boolean contains_link(String value) {
        return containsValue("link", value);
    }

}
