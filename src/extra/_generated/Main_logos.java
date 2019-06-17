
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Main_logos
    extends Item
{

    public final static String _NAME = "main_logos";

    private Main_logos(Item item) {
        super(item);
    }

    public static Main_logos get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'main_logos' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Main_logos(item);
    }

    public static Main_logos newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
