
package ecommander.extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.io.File;

public class Custom_block
    extends Item
{

    public final static String _NAME = "custom_block";
    public final static String NAME = "name";
    public final static String SPOILER = "spoiler";
    public final static String HEADER = "header";
    public final static String SUBHEADER = "subheader";
    public final static String TEXT = "text";
    public final static String IMAGE = "image";
    public final static String IMAGE_BGR = "image_bgr";
    public final static String YOUTUBE = "youtube";
    public final static String LINK = "link";
    public final static String CLASS = "class";
    public final static String TYPE = "type";

    private Custom_block(Item item) {
        super(item);
    }

    public static Custom_block get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'custom_block' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Custom_block(item);
    }

    public static Custom_block newChild(Item parent) {
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

    public void set_spoiler(Byte value) {
        setValue("spoiler", value);
    }

    public void setUI_spoiler(String value)
        throws Exception
    {
        setValueUI("spoiler", value);
    }

    public Byte get_spoiler() {
        return getByteValue("spoiler");
    }

    public Byte getDefault_spoiler(Byte defaultVal) {
        return getByteValue("spoiler", defaultVal);
    }

    public boolean contains_spoiler(Byte value) {
        return containsValue("spoiler", value);
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

    public void set_class(String value) {
        setValue("class", value);
    }

    public String get_class() {
        return getStringValue("class");
    }

    public String getDefault_class(String defaultVal) {
        return getStringValue("class", defaultVal);
    }

    public boolean contains_class(String value) {
        return containsValue("class", value);
    }

    public void set_type(String value) {
        setValue("type", value);
    }

    public String get_type() {
        return getStringValue("type");
    }

    public String getDefault_type(String defaultVal) {
        return getStringValue("type", defaultVal);
    }

    public boolean contains_type(String value) {
        return containsValue("type", value);
    }

}
