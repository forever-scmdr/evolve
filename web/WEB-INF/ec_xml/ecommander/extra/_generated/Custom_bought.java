
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Custom_bought
    extends Item
{

    public final static String _NAME = "custom_bought";
    public final static String POSITION = "position";
    public final static String MARK = "mark";
    public final static String TYPE = "type";
    public final static String CASE = "case";
    public final static String QTY = "qty";
    public final static String LINK = "link";
    public final static String EXTRA = "extra";
    public final static String NONEMPTY = "nonempty";

    private Custom_bought(Item item) {
        super(item);
    }

    public static Custom_bought get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'custom_bought' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Custom_bought(item);
    }

    public static Custom_bought newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_position(Integer value) {
        setValue("position", value);
    }

    public void setUI_position(String value)
        throws Exception
    {
        setValueUI("position", value);
    }

    public Integer get_position() {
        return getIntValue("position");
    }

    public Integer getDefault_position(Integer defaultVal) {
        return getIntValue("position", defaultVal);
    }

    public boolean contains_position(Integer value) {
        return containsValue("position", value);
    }

    public void set_mark(String value) {
        setValue("mark", value);
    }

    public String get_mark() {
        return getStringValue("mark");
    }

    public String getDefault_mark(String defaultVal) {
        return getStringValue("mark", defaultVal);
    }

    public boolean contains_mark(String value) {
        return containsValue("mark", value);
    }

    public void set_type(String value) {
        setValue("type", value);
    }

    public String get_type() {
        return getStringValue("type");
    }

    public String getDefault_type(String defaultVal) {
        return getStringValue("type", defaultVal);
    }

    public boolean contains_type(String value) {
        return containsValue("type", value);
    }

    public void set_case(String value) {
        setValue("case", value);
    }

    public String get_case() {
        return getStringValue("case");
    }

    public String getDefault_case(String defaultVal) {
        return getStringValue("case", defaultVal);
    }

    public boolean contains_case(String value) {
        return containsValue("case", value);
    }

    public void set_qty(String value) {
        setValue("qty", value);
    }

    public String get_qty() {
        return getStringValue("qty");
    }

    public String getDefault_qty(String defaultVal) {
        return getStringValue("qty", defaultVal);
    }

    public boolean contains_qty(String value) {
        return containsValue("qty", value);
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

    public void set_extra(String value) {
        setValue("extra", value);
    }

    public String get_extra() {
        return getStringValue("extra");
    }

    public String getDefault_extra(String defaultVal) {
        return getStringValue("extra", defaultVal);
    }

    public boolean contains_extra(String value) {
        return containsValue("extra", value);
    }

    public void set_nonempty(String value) {
        setValue("nonempty", value);
    }

    public String get_nonempty() {
        return getStringValue("nonempty");
    }

    public String getDefault_nonempty(String defaultVal) {
        return getStringValue("nonempty", defaultVal);
    }

    public boolean contains_nonempty(String value) {
        return containsValue("nonempty", value);
    }

}
