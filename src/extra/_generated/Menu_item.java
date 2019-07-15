
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Menu_item
    extends Item
{

    public final static String _NAME = "menu_item";
    public final static String IN_MAIN_MENU = "in_main_menu";

    private Menu_item(Item item) {
        super(item);
    }

    public static Menu_item get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'menu_item' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Menu_item(item);
    }

    public static Menu_item newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_in_main_menu(String value) {
        setValue("in_main_menu", value);
    }

    public String get_in_main_menu() {
        return getStringValue("in_main_menu");
    }

    public String getDefault_in_main_menu(String defaultVal) {
        return getStringValue("in_main_menu", defaultVal);
    }

    public boolean contains_in_main_menu(String value) {
        return containsValue("in_main_menu", value);
    }

}
