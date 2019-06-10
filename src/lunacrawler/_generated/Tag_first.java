
package lunacrawler._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Tag_first
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "tag_first";

    private Tag_first(Item item) {
        super(item);
    }

    public static Tag_first get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'tag_first' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Tag_first(item);
    }

    public static Tag_first newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_tag(String value) {
        setValue("tag", value);
    }

    public String get_tag() {
        return getStringValue("tag");
    }

    public String getDefault_tag(String defaultVal) {
        return getStringValue("tag", defaultVal);
    }

    public boolean contains_tag(String value) {
        return containsValue("tag", value);
    }

}
