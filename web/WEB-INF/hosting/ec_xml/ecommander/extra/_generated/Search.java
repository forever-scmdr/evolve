
package ecommander.extra._generated;

import java.util.List;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Search
    extends Item
{

    public final static String _NAME = "search";
    public final static String DEFAULT_VIEW = "default_view";
    public final static String DISABLE = "disable";
    public final static String HIDE_SIDE_MENU = "hide_side_menu";

    private Search(Item item) {
        super(item);
    }

    public static Search get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'search' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Search(item);
    }

    public static Search newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_default_view(String value) {
        setValue("default_view", value);
    }

    public String get_default_view() {
        return getStringValue("default_view");
    }

    public String getDefault_default_view(String defaultVal) {
        return getStringValue("default_view", defaultVal);
    }

    public boolean contains_default_view(String value) {
        return containsValue("default_view", value);
    }

    public void add_disable(String value) {
        setValue("disable", value);
    }

    public List<String> getAll_disable() {
        return getStringValues("disable");
    }

    public void remove_disable(String value) {
        removeEqualValue("disable", value);
    }

    public boolean contains_disable(String value) {
        return containsValue("disable", value);
    }

    public void set_hide_side_menu(Byte value) {
        setValue("hide_side_menu", value);
    }

    public void setUI_hide_side_menu(String value)
        throws Exception
    {
        setValueUI("hide_side_menu", value);
    }

    public Byte get_hide_side_menu() {
        return getByteValue("hide_side_menu");
    }

    public Byte getDefault_hide_side_menu(Byte defaultVal) {
        return getByteValue("hide_side_menu", defaultVal);
    }

    public boolean contains_hide_side_menu(Byte value) {
        return containsValue("hide_side_menu", value);
    }

}
