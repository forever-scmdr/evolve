
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Catalog_meta
    extends Item
{

    public final static String _NAME = "catalog_meta";
    public final static String EXCEL_ORDER_TEMPLATE = "excel_order_template";

    private Catalog_meta(Item item) {
        super(item);
    }

    public static Catalog_meta get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'catalog_meta' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Catalog_meta(item);
    }

    public static Catalog_meta newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_excel_order_template(File value) {
        setValue("excel_order_template", value);
    }

    public File get_excel_order_template() {
        return getFileValue("excel_order_template", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_excel_order_template(File value) {
        return containsValue("excel_order_template", value);
    }

}
