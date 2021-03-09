
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Catalog
    extends Item
{

    public final static String _NAME = "catalog";
    public final static String SHIP_DATE = "ship_date";
    public final static String INTEGRATION = "integration";
    public final static String PRICE_UPDATE = "price_update";
    public final static String PRICE_MICRO_UPDATE = "price_micro_update";
    public final static String HASH = "hash";
    public final static String ANALOGS = "analogs";
    public final static String DATE = "date";
    public final static String TEXT = "text";
    public final static String PICTURE = "picture";
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

    public void set_price_update(File value) {
        setValue("price_update", value);
    }

    public File get_price_update() {
        return getFileValue("price_update", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_price_update(File value) {
        return containsValue("price_update", value);
    }

    public void set_price_micro_update(File value) {
        setValue("price_micro_update", value);
    }

    public File get_price_micro_update() {
        return getFileValue("price_micro_update", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_price_micro_update(File value) {
        return containsValue("price_micro_update", value);
    }

    public void set_hash(Integer value) {
        setValue("hash", value);
    }

    public void setUI_hash(String value)
        throws Exception
    {
        setValueUI("hash", value);
    }

    public Integer get_hash() {
        return getIntValue("hash");
    }

    public Integer getDefault_hash(Integer defaultVal) {
        return getIntValue("hash", defaultVal);
    }

    public boolean contains_hash(Integer value) {
        return containsValue("hash", value);
    }

    public void set_analogs(File value) {
        setValue("analogs", value);
    }

    public File get_analogs() {
        return getFileValue("analogs", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_analogs(File value) {
        return containsValue("analogs", value);
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

    public void set_picture(File value) {
        setValue("picture", value);
    }

    public File get_picture() {
        return getFileValue("picture", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_picture(File value) {
        return containsValue("picture", value);
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
