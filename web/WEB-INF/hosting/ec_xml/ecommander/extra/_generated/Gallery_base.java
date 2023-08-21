
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Gallery_base
    extends Item
{

    public final static String _NAME = "gallery_base";
    public final static String HEIGHT = "height";
    public final static String WIDTH = "width";
    public final static String BORDER = "border";
    public final static String GUTTER = "gutter";

    private Gallery_base(Item item) {
        super(item);
    }

    public static Gallery_base get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'gallery_base' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Gallery_base(item);
    }

    public static Gallery_base newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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
