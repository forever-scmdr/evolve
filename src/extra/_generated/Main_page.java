
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Main_page
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "main_page";

    private Main_page(Item item) {
        super(item);
    }

    public static Main_page get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'main_page' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Main_page(item);
    }

    public static Main_page newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_link_text(String value) {
        setValue("link_text", value);
    }

    public String get_link_text() {
        return getStringValue("link_text");
    }

    public String getDefault_link_text(String defaultVal) {
        return getStringValue("link_text", defaultVal);
    }

    public boolean contains_link_text(String value) {
        return containsValue("link_text", value);
    }

    public void set_link_link(String value) {
        setValue("link_link", value);
    }

    public String get_link_link() {
        return getStringValue("link_link");
    }

    public String getDefault_link_link(String defaultVal) {
        return getStringValue("link_link", defaultVal);
    }

    public boolean contains_link_link(String value) {
        return containsValue("link_link", value);
    }

}
