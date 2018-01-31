
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class File_list
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "file_list";

    private File_list(Item item) {
        super(item);
    }

    public static File_list get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'file_list' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new File_list(item);
    }

    public static File_list newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

}
