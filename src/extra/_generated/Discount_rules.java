
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Discount_rules
    extends Item
{

    public final static String _NAME = "discount_rules";
    public final static String TEXT = "text";
    public final static String DISCOUNT_1 = "discount_1";
    public final static String DISCOUNT_2 = "discount_2";

    private Discount_rules(Item item) {
        super(item);
    }

    public static Discount_rules get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'discount_rules' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Discount_rules(item);
    }

    public static Discount_rules newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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

    public void set_discount_1(Integer value) {
        setValue("discount_1", value);
    }

    public void setUI_discount_1(String value)
        throws Exception
    {
        setValueUI("discount_1", value);
    }

    public Integer get_discount_1() {
        return getIntValue("discount_1");
    }

    public Integer getDefault_discount_1(Integer defaultVal) {
        return getIntValue("discount_1", defaultVal);
    }

    public boolean contains_discount_1(Integer value) {
        return containsValue("discount_1", value);
    }

    public void set_discount_2(Integer value) {
        setValue("discount_2", value);
    }

    public void setUI_discount_2(String value)
        throws Exception
    {
        setValueUI("discount_2", value);
    }

    public Integer get_discount_2() {
        return getIntValue("discount_2");
    }

    public Integer getDefault_discount_2(Integer defaultVal) {
        return getIntValue("discount_2", defaultVal);
    }

    public boolean contains_discount_2(Integer value) {
        return containsValue("discount_2", value);
    }

}
