
package lunacrawler._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class News_wrap
    extends Item
{

    public final static String _NAME = "news_wrap";

    private News_wrap(Item item) {
        super(item);
    }

    public static News_wrap get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'news_wrap' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new News_wrap(item);
    }

    public static News_wrap newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

}
