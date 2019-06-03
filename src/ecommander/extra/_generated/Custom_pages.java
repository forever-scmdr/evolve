
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Custom_pages
    extends Item
{

    public final static String _NAME = "custom_pages";
    public final static String XXL_TEXT = "xxl_text";

    private Custom_pages(Item item) {
        super(item);
    }

    public static Custom_pages get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'custom_pages' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Custom_pages(item);
    }

    public static Custom_pages newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_xxl_text(String value) {
        setValue("xxl_text", value);
    }

    public String get_xxl_text() {
        return getStringValue("xxl_text");
    }

    public String getDefault_xxl_text(String defaultVal) {
        return getStringValue("xxl_text", defaultVal);
    }

    public boolean contains_xxl_text(String value) {
        return containsValue("xxl_text", value);
    }

}
