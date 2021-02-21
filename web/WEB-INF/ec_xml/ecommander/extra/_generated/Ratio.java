
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Ratio
    extends Item
{

    public final static String _NAME = "ratio";
    public final static String NAME = "name";
    public final static String CODE = "code";
    public final static String CURRENCY_RATIO = "currency_ratio";
    public final static String Q = "q";
    public final static String SCALE = "scale";
    public final static String ROUND = "round";
    public final static String NBRB_ID = "nbrb_id";

    private Ratio(Item item) {
        super(item);
    }

    public static Ratio get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'ratio' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Ratio(item);
    }

    public static Ratio newChild(Item parent) {
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

    public void set_code(String value) {
        setValue("code", value);
    }

    public String get_code() {
        return getStringValue("code");
    }

    public String getDefault_code(String defaultVal) {
        return getStringValue("code", defaultVal);
    }

    public boolean contains_code(String value) {
        return containsValue("code", value);
    }

    public void set_currency_ratio(Double value) {
        setValue("currency_ratio", value);
    }

    public void setUI_currency_ratio(String value)
        throws Exception
    {
        setValueUI("currency_ratio", value);
    }

    public Double get_currency_ratio() {
        return getDoubleValue("currency_ratio");
    }

    public Double getDefault_currency_ratio(Double defaultVal) {
        return getDoubleValue("currency_ratio", defaultVal);
    }

    public boolean contains_currency_ratio(Double value) {
        return containsValue("currency_ratio", value);
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

    public void set_round(Byte value) {
        setValue("round", value);
    }

    public void setUI_round(String value)
        throws Exception
    {
        setValueUI("round", value);
    }

    public Byte get_round() {
        return getByteValue("round");
    }

    public Byte getDefault_round(Byte defaultVal) {
        return getByteValue("round", defaultVal);
    }

    public boolean contains_round(Byte value) {
        return containsValue("round", value);
    }

    public void set_nbrb_id(String value) {
        setValue("nbrb_id", value);
    }

    public String get_nbrb_id() {
        return getStringValue("nbrb_id");
    }

    public String getDefault_nbrb_id(String defaultVal) {
        return getStringValue("nbrb_id", defaultVal);
    }

    public boolean contains_nbrb_id(String value) {
        return containsValue("nbrb_id", value);
    }

}
