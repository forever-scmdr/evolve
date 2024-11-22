
package extra._generated;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Catalog
    extends Item
{

    public final static String _NAME = "catalog";
    public final static String DISCOUNT = "discount";
    public final static String IN_MAIN_MENU = "in_main_menu";
    public final static String DEFAULT_VIEW = "default_view";
    public final static String SHOW_FILTER = "show_filter";
    public final static String DEFAULT_STEP = "default_step";
    public final static String SHIP_DATE = "ship_date";
    public final static String INTEGRATION = "integration";
    public final static String DATE = "date";
    public final static String INTEGRATION_PENDING = "integration_pending";
    public final static String BIG_INTEGRATION = "big_integration";
    public final static String DISABLE = "disable";
    public final static String SHOW_SUBS = "show_subs";
    public final static String SUB_VIEW = "sub_view";
    public final static String SHOW_DEVICES = "show_devices";
    public final static String HIDE_SIDE_MENU = "hide_side_menu";
    public final static String HIDE_POPUP_MENU = "hide_popup_menu";

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

    public void set_discount(BigDecimal value) {
        setValue("discount", value);
    }

    public void setUI_discount(String value)
        throws Exception
    {
        setValueUI("discount", value);
    }

    public BigDecimal get_discount() {
        return getDecimalValue("discount");
    }

    public BigDecimal getDefault_discount(BigDecimal defaultVal) {
        return getDecimalValue("discount", defaultVal);
    }

    public boolean contains_discount(BigDecimal value) {
        return containsValue("discount", value);
    }

    public void set_in_main_menu(String value) {
        setValue("in_main_menu", value);
    }

    public String get_in_main_menu() {
        return getStringValue("in_main_menu");
    }

    public String getDefault_in_main_menu(String defaultVal) {
        return getStringValue("in_main_menu", defaultVal);
    }

    public boolean contains_in_main_menu(String value) {
        return containsValue("in_main_menu", value);
    }

    public void set_default_view(String value) {
        setValue("default_view", value);
    }

    public String get_default_view() {
        return getStringValue("default_view");
    }

    public String getDefault_default_view(String defaultVal) {
        return getStringValue("default_view", defaultVal);
    }

    public boolean contains_default_view(String value) {
        return containsValue("default_view", value);
    }

    public void set_show_filter(String value) {
        setValue("show_filter", value);
    }

    public String get_show_filter() {
        return getStringValue("show_filter");
    }

    public String getDefault_show_filter(String defaultVal) {
        return getStringValue("show_filter", defaultVal);
    }

    public boolean contains_show_filter(String value) {
        return containsValue("show_filter", value);
    }

    public void set_default_step(Double value) {
        setValue("default_step", value);
    }

    public void setUI_default_step(String value)
        throws Exception
    {
        setValueUI("default_step", value);
    }

    public Double get_default_step() {
        return getDoubleValue("default_step");
    }

    public Double getDefault_default_step(Double defaultVal) {
        return getDoubleValue("default_step", defaultVal);
    }

    public boolean contains_default_step(Double value) {
        return containsValue("default_step", value);
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

    public void add_disable(String value) {
        setValue("disable", value);
    }

    public List<String> getAll_disable() {
        return getStringValues("disable");
    }

    public void remove_disable(String value) {
        removeEqualValue("disable", value);
    }

    public boolean contains_disable(String value) {
        return containsValue("disable", value);
    }

    public void set_show_subs(Byte value) {
        setValue("show_subs", value);
    }

    public void setUI_show_subs(String value)
        throws Exception
    {
        setValueUI("show_subs", value);
    }

    public Byte get_show_subs() {
        return getByteValue("show_subs");
    }

    public Byte getDefault_show_subs(Byte defaultVal) {
        return getByteValue("show_subs", defaultVal);
    }

    public boolean contains_show_subs(Byte value) {
        return containsValue("show_subs", value);
    }

    public void set_sub_view(String value) {
        setValue("sub_view", value);
    }

    public String get_sub_view() {
        return getStringValue("sub_view");
    }

    public String getDefault_sub_view(String defaultVal) {
        return getStringValue("sub_view", defaultVal);
    }

    public boolean contains_sub_view(String value) {
        return containsValue("sub_view", value);
    }

    public void set_show_devices(Byte value) {
        setValue("show_devices", value);
    }

    public void setUI_show_devices(String value)
        throws Exception
    {
        setValueUI("show_devices", value);
    }

    public Byte get_show_devices() {
        return getByteValue("show_devices");
    }

    public Byte getDefault_show_devices(Byte defaultVal) {
        return getByteValue("show_devices", defaultVal);
    }

    public boolean contains_show_devices(Byte value) {
        return containsValue("show_devices", value);
    }

    public void set_hide_side_menu(Byte value) {
        setValue("hide_side_menu", value);
    }

    public void setUI_hide_side_menu(String value)
        throws Exception
    {
        setValueUI("hide_side_menu", value);
    }

    public Byte get_hide_side_menu() {
        return getByteValue("hide_side_menu");
    }

    public Byte getDefault_hide_side_menu(Byte defaultVal) {
        return getByteValue("hide_side_menu", defaultVal);
    }

    public boolean contains_hide_side_menu(Byte value) {
        return containsValue("hide_side_menu", value);
    }

    public void set_hide_popup_menu(Byte value) {
        setValue("hide_popup_menu", value);
    }

    public void setUI_hide_popup_menu(String value)
        throws Exception
    {
        setValueUI("hide_popup_menu", value);
    }

    public Byte get_hide_popup_menu() {
        return getByteValue("hide_popup_menu");
    }

    public Byte getDefault_hide_popup_menu(Byte defaultVal) {
        return getByteValue("hide_popup_menu", defaultVal);
    }

    public boolean contains_hide_popup_menu(Byte value) {
        return containsValue("hide_popup_menu", value);
    }

}
