
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class News
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "news";

    private News(Item item) {
        super(item);
    }

    public static News get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'news' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new News(item);
    }

    public static News newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_name(String value) {
        setValue("name", value);
    }

    public String get_name() {
        return getStringValue("name");
    }

    public String getDefault_name(String defaultVal) {
        return getStringValue("name", defaultVal);
    }

    public boolean contains_name(String value) {
        return containsValue("name", value);
    }

}
