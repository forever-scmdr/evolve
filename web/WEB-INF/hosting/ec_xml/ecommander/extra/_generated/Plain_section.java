
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Plain_section
    extends Item
{

    public final static String _NAME = "plain_section";
    public final static String NAME = "name";
    public final static String DATE = "date";
    public final static String THREAD = "thread";
    public final static String DEBUG = "debug";

    private Plain_section(Item item) {
        super(item);
    }

    public static Plain_section get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'plain_section' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Plain_section(item);
    }

    public static Plain_section newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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

    public void set_thread(String value) {
        setValue("thread", value);
    }

    public String get_thread() {
        return getStringValue("thread");
    }

    public String getDefault_thread(String defaultVal) {
        return getStringValue("thread", defaultVal);
    }

    public boolean contains_thread(String value) {
        return containsValue("thread", value);
    }

    public void set_debug(String value) {
        setValue("debug", value);
    }

    public String get_debug() {
        return getStringValue("debug");
    }

    public String getDefault_debug(String defaultVal) {
        return getStringValue("debug", defaultVal);
    }

    public boolean contains_debug(String value) {
        return containsValue("debug", value);
    }

}
