
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Region
    extends Item
{

    public final static String _NAME = "region";
    public final static String NAME = "name";
    public final static String CENTER = "center";
    public final static String ZOOM = "zoom";

    private Region(Item item) {
        super(item);
    }

    public static Region get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'region' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Region(item);
    }

    public static Region newChild(Item parent) {
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

    public void set_center(String value) {
        setValue("center", value);
    }

    public String get_center() {
        return getStringValue("center");
    }

    public String getDefault_center(String defaultVal) {
        return getStringValue("center", defaultVal);
    }

    public boolean contains_center(String value) {
        return containsValue("center", value);
    }

    public void set_zoom(Byte value) {
        setValue("zoom", value);
    }

    public void setUI_zoom(String value)
        throws Exception
    {
        setValueUI("zoom", value);
    }

    public Byte get_zoom() {
        return getByteValue("zoom");
    }

    public Byte getDefault_zoom(Byte defaultVal) {
        return getByteValue("zoom", defaultVal);
    }

    public boolean contains_zoom(Byte value) {
        return containsValue("zoom", value);
    }

}
