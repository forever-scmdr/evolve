
package lunacrawler._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Params
    extends Item
{

    public final static String _NAME = "params";

    private Params(Item item) {
        super(item);
    }

    public static Params get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'params' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Params(item);
    }

    public static Params newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
