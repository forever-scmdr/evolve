
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Group_catalog
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "group_catalog";

    private Group_catalog(Item item) {
        super(item);
    }

    public static Group_catalog get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'group_catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Group_catalog(item);
    }

    public static Group_catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_price(File value) {
        setValue("price", value);
    }

    public File get_price() {
        return getFileValue("price", AppContext.getFilesDirPath());
    }

    public boolean contains_price(File value) {
        return containsValue("price", value);
    }

}
