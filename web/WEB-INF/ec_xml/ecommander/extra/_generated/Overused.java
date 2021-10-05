
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.util.List;

public class Overused
    extends Item
{

    public final static String _NAME = "overused";
    public final static String TAG = "tag";

    private Overused(Item item) {
        super(item);
    }

    public static Overused get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'overused' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Overused(item);
    }

    public static Overused newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void add_tag(String value) {
        setValue("tag", value);
    }

    public List<String> getAll_tag() {
        return getStringValues("tag");
    }

    public void remove_tag(String value) {
        removeEqualValue("tag", value);
    }

    public boolean contains_tag(String value) {
        return containsValue("tag", value);
    }

}
