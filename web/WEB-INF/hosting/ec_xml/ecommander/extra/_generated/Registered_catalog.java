
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Registered_catalog
    extends Item
{

    public final static String _NAME = "registered_catalog";
    public final static String USERS_FILE = "users_file";

    private Registered_catalog(Item item) {
        super(item);
    }

    public static Registered_catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'registered_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Registered_catalog(item);
    }

    public static Registered_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_users_file(File value) {
        setValue("users_file", value);
    }

    public File get_users_file() {
        return getFileValue("users_file", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_users_file(File value) {
        return containsValue("users_file", value);
    }

}
