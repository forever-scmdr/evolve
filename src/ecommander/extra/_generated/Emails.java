
package ecommander.extra._generated;

import java.util.List;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Emails
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "emails";

    private Emails(Item item) {
        super(item);
    }

    public static Emails get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'emails' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Emails(item);
    }

    public static Emails newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void add_email(String value) {
        setValue("email", value);
    }

    public List<String> getAll_email() {
        return getStringValues("email");
    }

    public void remove_email(String value) {
        removeEqualValue("email", value);
    }

    public boolean contains_email(String value) {
        return containsValue("email", value);
    }

}
