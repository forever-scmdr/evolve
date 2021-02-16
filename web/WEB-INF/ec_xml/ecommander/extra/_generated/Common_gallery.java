
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Common_gallery
    extends Item
{

    public final static String _NAME = "common_gallery";
    public final static String NAME = "name";
    public final static String SPOILER = "spoiler";
    public final static String HEIGHT = "height";
    public final static String WIDTH = "width";
    public final static String BORDER = "border";
    public final static String GUTTER = "gutter";

    private Common_gallery(Item item) {
        super(item);
    }

    public static Common_gallery get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'common_gallery' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Common_gallery(item);
    }

    public static Common_gallery newChild(Item parent) {
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

}
