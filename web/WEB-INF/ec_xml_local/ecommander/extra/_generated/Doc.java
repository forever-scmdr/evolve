
package ecommander.extra._generated;

import java.util.List;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Doc
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "doc";

    private Doc(Item item) {
        super(item);
    }

    public static Doc get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'doc' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Doc(item);
    }

    public static Doc newChild(Item parent) {
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

    public void add_scope(String value) {
        setValue("scope", value);
    }

    public List<String> getAll_scope() {
        return getStringValues("scope");
    }

    public void remove_scope(String value) {
        removeEqualValue("scope", value);
    }

    public boolean contains_scope(String value) {
        return containsValue("scope", value);
    }

}
