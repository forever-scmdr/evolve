
package extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Course
    extends Item
{

    public final static String _NAME = "course";
    public final static String QUOTIENT_1 = "quotient_1";
    public final static String LEVEL_1 = "level_1";
    public final static String QUOTIENT_2 = "quotient_2";
    public final static String LEVEL_2 = "level_2";
    public final static String QUOTIENT_3 = "quotient_3";
    public final static String QUOTIENT_BUKINISTIC = "quotient_bukinistic";

    private Course(Item item) {
        super(item);
    }

    public static Course get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'course' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Course(item);
    }

    public static Course newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_quotient_1(BigDecimal value) {
        setValue("quotient_1", value);
    }

    public void setUI_quotient_1(String value)
        throws Exception
    {
        setValueUI("quotient_1", value);
    }

    public BigDecimal get_quotient_1() {
        return getDecimalValue("quotient_1");
    }

    public BigDecimal getDefault_quotient_1(BigDecimal defaultVal) {
        return getDecimalValue("quotient_1", defaultVal);
    }

    public boolean contains_quotient_1(BigDecimal value) {
        return containsValue("quotient_1", value);
    }

    public void set_level_1(BigDecimal value) {
        setValue("level_1", value);
    }

    public void setUI_level_1(String value)
        throws Exception
    {
        setValueUI("level_1", value);
    }

    public BigDecimal get_level_1() {
        return getDecimalValue("level_1");
    }

    public BigDecimal getDefault_level_1(BigDecimal defaultVal) {
        return getDecimalValue("level_1", defaultVal);
    }

    public boolean contains_level_1(BigDecimal value) {
        return containsValue("level_1", value);
    }

    public void set_quotient_2(BigDecimal value) {
        setValue("quotient_2", value);
    }

    public void setUI_quotient_2(String value)
        throws Exception
    {
        setValueUI("quotient_2", value);
    }

    public BigDecimal get_quotient_2() {
        return getDecimalValue("quotient_2");
    }

    public BigDecimal getDefault_quotient_2(BigDecimal defaultVal) {
        return getDecimalValue("quotient_2", defaultVal);
    }

    public boolean contains_quotient_2(BigDecimal value) {
        return containsValue("quotient_2", value);
    }

    public void set_level_2(BigDecimal value) {
        setValue("level_2", value);
    }

    public void setUI_level_2(String value)
        throws Exception
    {
        setValueUI("level_2", value);
    }

    public BigDecimal get_level_2() {
        return getDecimalValue("level_2");
    }

    public BigDecimal getDefault_level_2(BigDecimal defaultVal) {
        return getDecimalValue("level_2", defaultVal);
    }

    public boolean contains_level_2(BigDecimal value) {
        return containsValue("level_2", value);
    }

    public void set_quotient_3(BigDecimal value) {
        setValue("quotient_3", value);
    }

    public void setUI_quotient_3(String value)
        throws Exception
    {
        setValueUI("quotient_3", value);
    }

    public BigDecimal get_quotient_3() {
        return getDecimalValue("quotient_3");
    }

    public BigDecimal getDefault_quotient_3(BigDecimal defaultVal) {
        return getDecimalValue("quotient_3", defaultVal);
    }

    public boolean contains_quotient_3(BigDecimal value) {
        return containsValue("quotient_3", value);
    }

    public void set_quotient_bukinistic(BigDecimal value) {
        setValue("quotient_bukinistic", value);
    }

    public void setUI_quotient_bukinistic(String value)
        throws Exception
    {
        setValueUI("quotient_bukinistic", value);
    }

    public BigDecimal get_quotient_bukinistic() {
        return getDecimalValue("quotient_bukinistic");
    }

    public BigDecimal getDefault_quotient_bukinistic(BigDecimal defaultVal) {
        return getDecimalValue("quotient_bukinistic", defaultVal);
    }

    public boolean contains_quotient_bukinistic(BigDecimal value) {
        return containsValue("quotient_bukinistic", value);
    }

}
