
package extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Catalog
    extends Item
{

    public final static String _NAME = "catalog";
    public final static String INTEGRATION_PENDING = "integration_pending";
    public final static String INTEGRATION = "integration";
    public final static String DATE = "date";
    public final static String DELETE_LIST = "delete_list";

    private Catalog(Item item) {
        super(item);
    }

    public static Catalog get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'catalog' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Catalog(item);
    }

    public static Catalog newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_integration_pending(Byte value) {
        setValue("integration_pending", value);
    }

    public void setUI_integration_pending(String value)
        throws Exception
    {
        setValueUI("integration_pending", value);
    }

    public Byte get_integration_pending() {
        return getByteValue("integration_pending");
    }

    public Byte getDefault_integration_pending(Byte defaultVal) {
        return getByteValue("integration_pending", defaultVal);
    }

    public boolean contains_integration_pending(Byte value) {
        return containsValue("integration_pending", value);
    }

    public void set_integration(File value) {
        setValue("integration", value);
    }

    public File get_integration() {
        return getFileValue("integration", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_integration(File value) {
        return containsValue("integration", value);
    }

    public void set_date(Long value) {
        setValue("date", value);
    }

    public void setUI_date(String value)
        throws Exception
    {
        setValueUI("date", value);
    }

    public Long get_date() {
        return getLongValue("date");
    }

    public Long getDefault_date(Long defaultVal) {
        return getLongValue("date", defaultVal);
    }

    public boolean contains_date(Long value) {
        return containsValue("date", value);
    }

    public void set_delete_list(String value) {
        setValue("delete_list", value);
    }

    public String get_delete_list() {
        return getStringValue("delete_list");
    }

    public String getDefault_delete_list(String defaultVal) {
        return getStringValue("delete_list", defaultVal);
    }

    public boolean contains_delete_list(String value) {
        return containsValue("delete_list", value);
    }

}
