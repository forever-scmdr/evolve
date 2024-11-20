
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Catalog
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "catalog";

    private Catalog(Item item) {
        super(item);
    }

    public static Catalog get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Catalog(item);
    }

    public static Catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_price_file(File value) {
        setValue("price_file", value);
    }

    public File get_price_file() {
        return getFileValue("price_file", AppContext.getFilesDirPath());
    }

    public boolean contains_price_file(File value) {
        return containsValue("price_file", value);
    }

    public void set_price_list(File value) {
        setValue("price_list", value);
    }

    public File get_price_list() {
        return getFileValue("price_list", AppContext.getFilesDirPath());
    }

    public boolean contains_price_list(File value) {
        return containsValue("price_list", value);
    }

    public void set_show_price_link(Byte value) {
        setValue("show_price_link", value);
    }

    public void setUI_show_price_link(String value)
        throws Exception
    {
        setValueUI("show_price_link", value);
    }

    public Byte get_show_price_link() {
        return getByteValue("show_price_link");
    }

    public Byte getDefault_show_price_link(Byte defaultVal) {
        return getByteValue("show_price_link", defaultVal);
    }

    public boolean contains_show_price_link(Byte value) {
        return containsValue("show_price_link", value);
    }

}
