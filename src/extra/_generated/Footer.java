
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Footer
    extends Item
{

    public final static String _NAME = "footer";

    private Footer(Item item) {
        super(item);
    }

    public static Footer get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'footer' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Footer(item);
    }

    public static Footer newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
