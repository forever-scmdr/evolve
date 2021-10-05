
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Currency
    extends Item
{

    public final static String _NAME = "currency";
    public final static String TITLE = "title";
    public final static String NAME = "name";
    public final static String RATIO = "ratio";
    public final static String SCALE = "scale";
    public final static String Q = "q";

    private Currency(Item item) {
        super(item);
    }

    public static Currency get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'currency' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Currency(item);
    }

    public static Currency newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_title(String value) {
        setValue("title", value);
    }

    public String get_title() {
        return getStringValue("title");
    }

    public String getDefault_title(String defaultVal) {
        return getStringValue("title", defaultVal);
    }

    public boolean contains_title(String value) {
        return containsValue("title", value);
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

    public void set_ratio(Double value) {
        setValue("ratio", value);
    }

    public void setUI_ratio(String value)
        throws Exception
    {
        setValueUI("ratio", value);
    }

    public Double get_ratio() {
        return getDoubleValue("ratio");
    }

    public Double getDefault_ratio(Double defaultVal) {
        return getDoubleValue("ratio", defaultVal);
    }

    public boolean contains_ratio(Double value) {
        return containsValue("ratio", value);
    }

    public void set_scale(Integer value) {
        setValue("scale", value);
    }

    public void setUI_scale(String value)
        throws Exception
    {
        setValueUI("scale", value);
    }

    public Integer get_scale() {
        return getIntValue("scale");
    }

    public Integer getDefault_scale(Integer defaultVal) {
        return getIntValue("scale", defaultVal);
    }

    public boolean contains_scale(Integer value) {
        return containsValue("scale", value);
    }

    public void set_q(Double value) {
        setValue("q", value);
    }

    public void setUI_q(String value)
        throws Exception
    {
        setValueUI("q", value);
    }

    public Double get_q() {
        return getDoubleValue("q");
    }

    public Double getDefault_q(Double defaultVal) {
        return getDoubleValue("q", defaultVal);
    }

    public boolean contains_q(Double value) {
        return containsValue("q", value);
    }

}
