
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Book_type
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "book_type";

    private Book_type(Item item) {
        super(item);
    }

    public static Book_type get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'book_type' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Book_type(item);
    }

    public static Book_type newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_text(String value) {
        setValue("text", value);
    }

    public String get_text() {
        return getStringValue("text");
    }

    public String getDefault_text(String defaultVal) {
        return getStringValue("text", defaultVal);
    }

    public boolean contains_text(String value) {
        return containsValue("text", value);
    }

    public void set_side_text(String value) {
        setValue("side_text", value);
    }

    public String get_side_text() {
        return getStringValue("side_text");
    }

    public String getDefault_side_text(String defaultVal) {
        return getStringValue("side_text", defaultVal);
    }

    public boolean contains_side_text(String value) {
        return containsValue("side_text", value);
    }

    public void set_contract(File value) {
        setValue("contract", value);
    }

    public File get_contract() {
        return getFileValue("contract", AppContext.getFilesDirPath());
    }

    public boolean contains_contract(File value) {
        return containsValue("contract", value);
    }

}
