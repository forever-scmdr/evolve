
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Sale
    extends Item
{

    public final static String _NAME = "sale";
    public final static String REPORT = "report";
    public final static String DEVICE = "device";
    public final static String DEVICE_TYPE = "device_type";
    public final static String DEVICE_GROUP = "device_group";
    public final static String QTY = "qty";
    public final static String YEAR = "year";
    public final static String QUARTAL = "quartal";
    public final static String SALE_DATE = "sale_date";
    public final static String REGISTER_DATE = "register_date";
    public final static String AGENT_CODE = "agent_code";
    public final static String AGENT_NAME = "agent_name";
    public final static String AGENT_PLAIN_NAME = "agent_plain_name";
    public final static String DEALER_CODE = "dealer_code";
    public final static String TAG = "tag";
    public final static String ASSIGNED = "assigned";
    public final static String HAS_TAGS = "has_tags";

    private Sale(Item item) {
        super(item);
    }

    public static Sale get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'sale' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Sale(item);
    }

    public static Sale newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_report(String value) {
        setValue("report", value);
    }

    public String get_report() {
        return getStringValue("report");
    }

    public String getDefault_report(String defaultVal) {
        return getStringValue("report", defaultVal);
    }

    public boolean contains_report(String value) {
        return containsValue("report", value);
    }

    public void set_device(String value) {
        setValue("device", value);
    }

    public String get_device() {
        return getStringValue("device");
    }

    public String getDefault_device(String defaultVal) {
        return getStringValue("device", defaultVal);
    }

    public boolean contains_device(String value) {
        return containsValue("device", value);
    }

    public void set_device_type(String value) {
        setValue("device_type", value);
    }

    public String get_device_type() {
        return getStringValue("device_type");
    }

    public String getDefault_device_type(String defaultVal) {
        return getStringValue("device_type", defaultVal);
    }

    public boolean contains_device_type(String value) {
        return containsValue("device_type", value);
    }

    public void set_device_group(String value) {
        setValue("device_group", value);
    }

    public String get_device_group() {
        return getStringValue("device_group");
    }

    public String getDefault_device_group(String defaultVal) {
        return getStringValue("device_group", defaultVal);
    }

    public boolean contains_device_group(String value) {
        return containsValue("device_group", value);
    }

    public void set_qty(Integer value) {
        setValue("qty", value);
    }

    public void setUI_qty(String value)
        throws Exception
    {
        setValueUI("qty", value);
    }

    public Integer get_qty() {
        return getIntValue("qty");
    }

    public Integer getDefault_qty(Integer defaultVal) {
        return getIntValue("qty", defaultVal);
    }

    public boolean contains_qty(Integer value) {
        return containsValue("qty", value);
    }

    public void set_year(Integer value) {
        setValue("year", value);
    }

    public void setUI_year(String value)
        throws Exception
    {
        setValueUI("year", value);
    }

    public Integer get_year() {
        return getIntValue("year");
    }

    public Integer getDefault_year(Integer defaultVal) {
        return getIntValue("year", defaultVal);
    }

    public boolean contains_year(Integer value) {
        return containsValue("year", value);
    }

    public void set_quartal(Integer value) {
        setValue("quartal", value);
    }

    public void setUI_quartal(String value)
        throws Exception
    {
        setValueUI("quartal", value);
    }

    public Integer get_quartal() {
        return getIntValue("quartal");
    }

    public Integer getDefault_quartal(Integer defaultVal) {
        return getIntValue("quartal", defaultVal);
    }

    public boolean contains_quartal(Integer value) {
        return containsValue("quartal", value);
    }

    public void set_sale_date(Long value) {
        setValue("sale_date", value);
    }

    public void setUI_sale_date(String value)
        throws Exception
    {
        setValueUI("sale_date", value);
    }

    public Long get_sale_date() {
        return getLongValue("sale_date");
    }

    public Long getDefault_sale_date(Long defaultVal) {
        return getLongValue("sale_date", defaultVal);
    }

    public boolean contains_sale_date(Long value) {
        return containsValue("sale_date", value);
    }

    public void set_register_date(Long value) {
        setValue("register_date", value);
    }

    public void setUI_register_date(String value)
        throws Exception
    {
        setValueUI("register_date", value);
    }

    public Long get_register_date() {
        return getLongValue("register_date");
    }

    public Long getDefault_register_date(Long defaultVal) {
        return getLongValue("register_date", defaultVal);
    }

    public boolean contains_register_date(Long value) {
        return containsValue("register_date", value);
    }

    public void set_agent_code(String value) {
        setValue("agent_code", value);
    }

    public String get_agent_code() {
        return getStringValue("agent_code");
    }

    public String getDefault_agent_code(String defaultVal) {
        return getStringValue("agent_code", defaultVal);
    }

    public boolean contains_agent_code(String value) {
        return containsValue("agent_code", value);
    }

    public void set_agent_name(String value) {
        setValue("agent_name", value);
    }

    public String get_agent_name() {
        return getStringValue("agent_name");
    }

    public String getDefault_agent_name(String defaultVal) {
        return getStringValue("agent_name", defaultVal);
    }

    public boolean contains_agent_name(String value) {
        return containsValue("agent_name", value);
    }

    public void set_agent_plain_name(String value) {
        setValue("agent_plain_name", value);
    }

    public String get_agent_plain_name() {
        return getStringValue("agent_plain_name");
    }

    public String getDefault_agent_plain_name(String defaultVal) {
        return getStringValue("agent_plain_name", defaultVal);
    }

    public boolean contains_agent_plain_name(String value) {
        return containsValue("agent_plain_name", value);
    }

    public void set_dealer_code(String value) {
        setValue("dealer_code", value);
    }

    public String get_dealer_code() {
        return getStringValue("dealer_code");
    }

    public String getDefault_dealer_code(String defaultVal) {
        return getStringValue("dealer_code", defaultVal);
    }

    public boolean contains_dealer_code(String value) {
        return containsValue("dealer_code", value);
    }

    public void set_tag(String value) {
        setValue("tag", value);
    }

    public String get_tag() {
        return getStringValue("tag");
    }

    public String getDefault_tag(String defaultVal) {
        return getStringValue("tag", defaultVal);
    }

    public boolean contains_tag(String value) {
        return containsValue("tag", value);
    }

    public void set_assigned(Byte value) {
        setValue("assigned", value);
    }

    public void setUI_assigned(String value)
        throws Exception
    {
        setValueUI("assigned", value);
    }

    public Byte get_assigned() {
        return getByteValue("assigned");
    }

    public Byte getDefault_assigned(Byte defaultVal) {
        return getByteValue("assigned", defaultVal);
    }

    public boolean contains_assigned(Byte value) {
        return containsValue("assigned", value);
    }

    public void set_has_tags(Byte value) {
        setValue("has_tags", value);
    }

    public void setUI_has_tags(String value)
        throws Exception
    {
        setValueUI("has_tags", value);
    }

    public Byte get_has_tags() {
        return getByteValue("has_tags");
    }

    public Byte getDefault_has_tags(Byte defaultVal) {
        return getByteValue("has_tags", defaultVal);
    }

    public boolean contains_has_tags(Byte value) {
        return containsValue("has_tags", value);
    }

}
