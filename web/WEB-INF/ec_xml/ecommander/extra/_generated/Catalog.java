
package ecommander.extra._generated;

import java.io.File;
import java.math.BigDecimal;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Catalog
    extends Item
{

    public final static String _NAME = "catalog";
    public final static String BELCHIP_Q = "belchip_q";
    public final static String SHIP_DATE = "ship_date";
    public final static String INTEGRATION = "integration";
    public final static String DATE = "date";
    public final static String INTEGRATION_PENDING = "integration_pending";
    public final static String BIG_INTEGRATION = "big_integration";

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

    public void set_belchip_q(BigDecimal value) {
        setValue("belchip_q", value);
    }

    public void setUI_belchip_q(String value)
        throws Exception
    {
        setValueUI("belchip_q", value);
    }

    public BigDecimal get_belchip_q() {
        return getDecimalValue("belchip_q");
    }

    public BigDecimal getDefault_belchip_q(BigDecimal defaultVal) {
        return getDecimalValue("belchip_q", defaultVal);
    }

    public boolean contains_belchip_q(BigDecimal value) {
        return containsValue("belchip_q", value);
    }

    public void set_ship_date(Long value) {
        setValue("ship_date", value);
    }

    public void setUI_ship_date(String value)
        throws Exception
    {
        setValueUI("ship_date", value);
    }

    public Long get_ship_date() {
        return getLongValue("ship_date");
    }

    public Long getDefault_ship_date(Long defaultVal) {
        return getLongValue("ship_date", defaultVal);
    }

    public boolean contains_ship_date(Long value) {
        return containsValue("ship_date", value);
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

    public void set_big_integration(File value) {
        setValue("big_integration", value);
    }

    public File get_big_integration() {
        return getFileValue("big_integration", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_big_integration(File value) {
        return containsValue("big_integration", value);
    }

}
