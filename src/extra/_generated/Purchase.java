
package extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Purchase
    extends Item
{

    public final static String _NAME = "purchase";
    public final static String NUM = "num";
    public final static String DATE = "date";
    public final static String QTY = "qty";
    public final static String QTY_AVAIL = "qty_avail";
    public final static String QTY_TOTAL = "qty_total";
    public final static String SUM = "sum";
    public final static String INT_NUMBER = "int_number";
    public final static String STATUS = "status";
    public final static String PAYED = "payed";
    public final static String CLIENT_ID = "client_id";
    public final static String LOGIN = "login";
    public final static String EMAIL = "email";
    public final static String PHONE = "phone";
    public final static String CLIENT_NAME = "client_name";
    public final static String SECOND_NAME = "second_name";
    public final static String PHYS = "phys";
    public final static String COOKIE = "cookie";
    public final static String PAYMENT = "payment";
    public final static String DELIVERY = "delivery";
    public final static String INDEX = "index";
    public final static String CITY = "city";
    public final static String REGION = "region";
    public final static String ADDRESS = "address";
    public final static String IF_ABSENT = "if_absent";

    private Purchase(Item item) {
        super(item);
    }

    public static Purchase get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'purchase' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Purchase(item);
    }

    public static Purchase newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_num(String value) {
        setValue("num", value);
    }

    public String get_num() {
        return getStringValue("num");
    }

    public String getDefault_num(String defaultVal) {
        return getStringValue("num", defaultVal);
    }

    public boolean contains_num(String value) {
        return containsValue("num", value);
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

    public void set_qty(Double value) {
        setValue("qty", value);
    }

    public void setUI_qty(String value)
        throws Exception
    {
        setValueUI("qty", value);
    }

    public Double get_qty() {
        return getDoubleValue("qty");
    }

    public Double getDefault_qty(Double defaultVal) {
        return getDoubleValue("qty", defaultVal);
    }

    public boolean contains_qty(Double value) {
        return containsValue("qty", value);
    }

    public void set_qty_avail(Double value) {
        setValue("qty_avail", value);
    }

    public void setUI_qty_avail(String value)
        throws Exception
    {
        setValueUI("qty_avail", value);
    }

    public Double get_qty_avail() {
        return getDoubleValue("qty_avail");
    }

    public Double getDefault_qty_avail(Double defaultVal) {
        return getDoubleValue("qty_avail", defaultVal);
    }

    public boolean contains_qty_avail(Double value) {
        return containsValue("qty_avail", value);
    }

    public void set_qty_total(Double value) {
        setValue("qty_total", value);
    }

    public void setUI_qty_total(String value)
        throws Exception
    {
        setValueUI("qty_total", value);
    }

    public Double get_qty_total() {
        return getDoubleValue("qty_total");
    }

    public Double getDefault_qty_total(Double defaultVal) {
        return getDoubleValue("qty_total", defaultVal);
    }

    public boolean contains_qty_total(Double value) {
        return containsValue("qty_total", value);
    }

    public void set_sum(BigDecimal value) {
        setValue("sum", value);
    }

    public void setUI_sum(String value)
        throws Exception
    {
        setValueUI("sum", value);
    }

    public BigDecimal get_sum() {
        return getDecimalValue("sum");
    }

    public BigDecimal getDefault_sum(BigDecimal defaultVal) {
        return getDecimalValue("sum", defaultVal);
    }

    public boolean contains_sum(BigDecimal value) {
        return containsValue("sum", value);
    }

    public void set_int_number(Integer value) {
        setValue("int_number", value);
    }

    public void setUI_int_number(String value)
        throws Exception
    {
        setValueUI("int_number", value);
    }

    public Integer get_int_number() {
        return getIntValue("int_number");
    }

    public Integer getDefault_int_number(Integer defaultVal) {
        return getIntValue("int_number", defaultVal);
    }

    public boolean contains_int_number(Integer value) {
        return containsValue("int_number", value);
    }

    public void set_status(Byte value) {
        setValue("status", value);
    }

    public void setUI_status(String value)
        throws Exception
    {
        setValueUI("status", value);
    }

    public Byte get_status() {
        return getByteValue("status");
    }

    public Byte getDefault_status(Byte defaultVal) {
        return getByteValue("status", defaultVal);
    }

    public boolean contains_status(Byte value) {
        return containsValue("status", value);
    }

    public void set_payed(Byte value) {
        setValue("payed", value);
    }

    public void setUI_payed(String value)
        throws Exception
    {
        setValueUI("payed", value);
    }

    public Byte get_payed() {
        return getByteValue("payed");
    }

    public Byte getDefault_payed(Byte defaultVal) {
        return getByteValue("payed", defaultVal);
    }

    public boolean contains_payed(Byte value) {
        return containsValue("payed", value);
    }

    public void set_client_id(Long value) {
        setValue("client_id", value);
    }

    public void setUI_client_id(String value)
        throws Exception
    {
        setValueUI("client_id", value);
    }

    public Long get_client_id() {
        return getLongValue("client_id");
    }

    public Long getDefault_client_id(Long defaultVal) {
        return getLongValue("client_id", defaultVal);
    }

    public boolean contains_client_id(Long value) {
        return containsValue("client_id", value);
    }

    public void set_login(String value) {
        setValue("login", value);
    }

    public String get_login() {
        return getStringValue("login");
    }

    public String getDefault_login(String defaultVal) {
        return getStringValue("login", defaultVal);
    }

    public boolean contains_login(String value) {
        return containsValue("login", value);
    }

    public void set_email(String value) {
        setValue("email", value);
    }

    public String get_email() {
        return getStringValue("email");
    }

    public String getDefault_email(String defaultVal) {
        return getStringValue("email", defaultVal);
    }

    public boolean contains_email(String value) {
        return containsValue("email", value);
    }

    public void set_phone(String value) {
        setValue("phone", value);
    }

    public String get_phone() {
        return getStringValue("phone");
    }

    public String getDefault_phone(String defaultVal) {
        return getStringValue("phone", defaultVal);
    }

    public boolean contains_phone(String value) {
        return containsValue("phone", value);
    }

    public void set_client_name(String value) {
        setValue("client_name", value);
    }

    public String get_client_name() {
        return getStringValue("client_name");
    }

    public String getDefault_client_name(String defaultVal) {
        return getStringValue("client_name", defaultVal);
    }

    public boolean contains_client_name(String value) {
        return containsValue("client_name", value);
    }

    public void set_second_name(String value) {
        setValue("second_name", value);
    }

    public String get_second_name() {
        return getStringValue("second_name");
    }

    public String getDefault_second_name(String defaultVal) {
        return getStringValue("second_name", defaultVal);
    }

    public boolean contains_second_name(String value) {
        return containsValue("second_name", value);
    }

    public void set_phys(Byte value) {
        setValue("phys", value);
    }

    public void setUI_phys(String value)
        throws Exception
    {
        setValueUI("phys", value);
    }

    public Byte get_phys() {
        return getByteValue("phys");
    }

    public Byte getDefault_phys(Byte defaultVal) {
        return getByteValue("phys", defaultVal);
    }

    public boolean contains_phys(Byte value) {
        return containsValue("phys", value);
    }

    public void set_cookie(String value) {
        setValue("cookie", value);
    }

    public String get_cookie() {
        return getStringValue("cookie");
    }

    public String getDefault_cookie(String defaultVal) {
        return getStringValue("cookie", defaultVal);
    }

    public boolean contains_cookie(String value) {
        return containsValue("cookie", value);
    }

    public void set_payment(String value) {
        setValue("payment", value);
    }

    public String get_payment() {
        return getStringValue("payment");
    }

    public String getDefault_payment(String defaultVal) {
        return getStringValue("payment", defaultVal);
    }

    public boolean contains_payment(String value) {
        return containsValue("payment", value);
    }

    public void set_delivery(String value) {
        setValue("delivery", value);
    }

    public String get_delivery() {
        return getStringValue("delivery");
    }

    public String getDefault_delivery(String defaultVal) {
        return getStringValue("delivery", defaultVal);
    }

    public boolean contains_delivery(String value) {
        return containsValue("delivery", value);
    }

    public void set_index(Integer value) {
        setValue("index", value);
    }

    public void setUI_index(String value)
        throws Exception
    {
        setValueUI("index", value);
    }

    public Integer get_index() {
        return getIntValue("index");
    }

    public Integer getDefault_index(Integer defaultVal) {
        return getIntValue("index", defaultVal);
    }

    public boolean contains_index(Integer value) {
        return containsValue("index", value);
    }

    public void set_city(String value) {
        setValue("city", value);
    }

    public String get_city() {
        return getStringValue("city");
    }

    public String getDefault_city(String defaultVal) {
        return getStringValue("city", defaultVal);
    }

    public boolean contains_city(String value) {
        return containsValue("city", value);
    }

    public void set_region(String value) {
        setValue("region", value);
    }

    public String get_region() {
        return getStringValue("region");
    }

    public String getDefault_region(String defaultVal) {
        return getStringValue("region", defaultVal);
    }

    public boolean contains_region(String value) {
        return containsValue("region", value);
    }

    public void set_address(String value) {
        setValue("address", value);
    }

    public String get_address() {
        return getStringValue("address");
    }

    public String getDefault_address(String defaultVal) {
        return getStringValue("address", defaultVal);
    }

    public boolean contains_address(String value) {
        return containsValue("address", value);
    }

    public void set_if_absent(String value) {
        setValue("if_absent", value);
    }

    public String get_if_absent() {
        return getStringValue("if_absent");
    }

    public String getDefault_if_absent(String defaultVal) {
        return getStringValue("if_absent", defaultVal);
    }

    public boolean contains_if_absent(String value) {
        return containsValue("if_absent", value);
    }

}
