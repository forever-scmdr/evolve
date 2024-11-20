
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Map
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "map";

    private Map(Item item) {
        super(item);
    }

    public static Map get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'map' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Map(item);
    }

    public static Map newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_zoom(Double value) {
        setValue("zoom", value);
    }

    public void setUI_zoom(String value)
        throws Exception
    {
        setValueUI("zoom", value);
    }

    public Double get_zoom() {
        return getDoubleValue("zoom");
    }

    public Double getDefault_zoom(Double defaultVal) {
        return getDoubleValue("zoom", defaultVal);
    }

    public boolean contains_zoom(Double value) {
        return containsValue("zoom", value);
    }

}
