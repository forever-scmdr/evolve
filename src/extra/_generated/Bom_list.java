
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Bom_list
    extends Item
{

    public final static String _NAME = "bom_list";
    public final static String NAME = "name";
    public final static String DESCRIPTION = "description";
    public final static String DATE = "date";
    public final static String SORT_POSITION = "sort_position";
    public final static String LINE = "line";

    private Bom_list(Item item) {
        super(item);
    }

    public static Bom_list get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'bom_list' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Bom_list(item);
    }

    public static Bom_list newChild(Item parent) {
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

    public void set_description(String value) {
        setValue("description", value);
    }

    public String get_description() {
        return getStringValue("description");
    }

    public String getDefault_description(String defaultVal) {
        return getStringValue("description", defaultVal);
    }

    public boolean contains_description(String value) {
        return containsValue("description", value);
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

    public void set_sort_position(Integer value) {
        setValue("sort_position", value);
    }

    public void setUI_sort_position(String value)
        throws Exception
    {
        setValueUI("sort_position", value);
    }

    public Integer get_sort_position() {
        return getIntValue("sort_position");
    }

    public Integer getDefault_sort_position(Integer defaultVal) {
        return getIntValue("sort_position", defaultVal);
    }

    public boolean contains_sort_position(Integer value) {
        return containsValue("sort_position", value);
    }

}
