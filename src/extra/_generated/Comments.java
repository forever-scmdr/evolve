
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Comments
    extends Item
{

    public final static String _NAME = "comments";

    private Comments(Item item) {
        super(item);
    }

    public static Comments get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'comments' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Comments(item);
    }

    public static Comments newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
