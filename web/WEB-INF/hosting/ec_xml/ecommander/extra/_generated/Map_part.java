
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Map_part
    extends Item
{

    public final static String _NAME = "map_part";
    public final static String NAME = "name";
    public final static String SPOILER = "spoiler";
    public final static String SHOW_PLACES = "show_places";
    public final static String CENTER = "center";
    public final static String ZOOM = "zoom";

    private Map_part(Item item) {
        super(item);
    }

    public static Map_part get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'map_part' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Map_part(item);
    }

    public static Map_part newChild(Item parent) {
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

    public void set_show_places(Byte value) {
        setValue("show_places", value);
    }

    public void setUI_show_places(String value)
        throws Exception
    {
        setValueUI("show_places", value);
    }

    public Byte get_show_places() {
        return getByteValue("show_places");
    }

    public Byte getDefault_show_places(Byte defaultVal) {
        return getByteValue("show_places", defaultVal);
    }

    public boolean contains_show_places(Byte value) {
        return containsValue("show_places", value);
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
