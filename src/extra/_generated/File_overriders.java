
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class File_overriders
    extends Item
{

    public final static String _NAME = "file_overriders";

    private File_overriders(Item item) {
        super(item);
    }

    public static File_overriders get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'file_overriders' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new File_overriders(item);
    }

    public static File_overriders newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
