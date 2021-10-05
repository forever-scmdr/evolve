
package ecommander.extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.io.File;

public class Catalog_banner
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "catalog_banner";

    private Catalog_banner(Item item) {
        super(item);
    }

    public static Catalog_banner get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'catalog_banner' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Catalog_banner(item);
    }

    public static Catalog_banner newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_link(String value) {
        setValue("link", value);
    }

    public String get_link() {
        return getStringValue("link");
    }

    public String getDefault_link(String defaultVal) {
        return getStringValue("link", defaultVal);
    }

    public boolean contains_link(String value) {
        return containsValue("link", value);
    }

    public void set_pic(File value) {
        setValue("pic", value);
    }

    public File get_pic() {
        return getFileValue("pic", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_pic(File value) {
        return containsValue("pic", value);
    }

}
