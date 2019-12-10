
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Informer_wrap
    extends Item
{

    public final static String _NAME = "informer_wrap";
    public final static String NAME = "name";

    private Informer_wrap(Item item) {
        super(item);
    }

    public static Informer_wrap get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'informer_wrap' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Informer_wrap(item);
    }

    public static Informer_wrap newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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
