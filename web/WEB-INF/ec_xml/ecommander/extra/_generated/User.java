
package ecommander.extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class User
    extends Item
{

    public final static String _NAME = "user";
    public final static String EMAIL = "email";
    public final static String PHONE = "phone";
    public final static String PASSWORD = "password";
    public final static String DISCOUNT = "discount";
    public final static String DISCOUNT_NUMBER = "discount_number";
    public final static String REGISTERED = "registered";
    public final static String PAYMENT = "payment";
    public final static String PAY_TYPE = "pay_type";
    public final static String IF_ABSENT = "if_absent";
    public final static String SHIP_TYPE = "ship_type";
    public final static String NEED_POST_ADDRESS = "need_post_address";
    public final static String GET_ORDER_FROM = "get_order_from";
    public final static String POST_ADDRESS = "post_address";
    public final static String POST_INDEX = "post_index";
    public final static String POST_CITY = "post_city";
    public final static String POST_REGION = "post_region";
    public final static String POST_COUNTRY = "post_country";
    public final static String COMMENT = "comment";
    public final static String BOUGHTS_SERIALIZED = "boughts_serialized";
    public final static String CUSTOM_BOUGHTS_SERIALIZED = "custom_boughts_serialized";
    public final static String FAV_COOKIE = "fav_cookie";
    public final static String OLD_LOGIN = "old_login";

    private User(Item item) {
        super(item);
    }

    public static User get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'user' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new User(item);
    }

    public static User newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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

    public void set_password(String value) {
        setValue("password", value);
    }

    public String get_password() {
        return getStringValue("password");
    }

    public String getDefault_password(String defaultVal) {
        return getStringValue("password", defaultVal);
    }

    public boolean contains_password(String value) {
        return containsValue("password", value);
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

    public void set_discount_number(String value) {
        setValue("discount_number", value);
    }

    public String get_discount_number() {
        return getStringValue("discount_number");
    }

    public String getDefault_discount_number(String defaultVal) {
        return getStringValue("discount_number", defaultVal);
    }

    public boolean contains_discount_number(String value) {
        return containsValue("discount_number", value);
    }

    public void set_registered(Byte value) {
        setValue("registered", value);
    }

    public void setUI_registered(String value)
        throws Exception
    {
        setValueUI("registered", value);
    }

    public Byte get_registered() {
        return getByteValue("registered");
    }

    public Byte getDefault_registered(Byte defaultVal) {
        return getByteValue("registered", defaultVal);
    }

    public boolean contains_registered(Byte value) {
        return containsValue("registered", value);
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

    public void set_pay_type(String value) {
        setValue("pay_type", value);
    }

    public String get_pay_type() {
        return getStringValue("pay_type");
    }

    public String getDefault_pay_type(String defaultVal) {
        return getStringValue("pay_type", defaultVal);
    }

    public boolean contains_pay_type(String value) {
        return containsValue("pay_type", value);
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

    public void set_ship_type(String value) {
        setValue("ship_type", value);
    }

    public String get_ship_type() {
        return getStringValue("ship_type");
    }

    public String getDefault_ship_type(String defaultVal) {
        return getStringValue("ship_type", defaultVal);
    }

    public boolean contains_ship_type(String value) {
        return containsValue("ship_type", value);
    }

    public void set_need_post_address(String value) {
        setValue("need_post_address", value);
    }

    public String get_need_post_address() {
        return getStringValue("need_post_address");
    }

    public String getDefault_need_post_address(String defaultVal) {
        return getStringValue("need_post_address", defaultVal);
    }

    public boolean contains_need_post_address(String value) {
        return containsValue("need_post_address", value);
    }

    public void set_get_order_from(String value) {
        setValue("get_order_from", value);
    }

    public String get_get_order_from() {
        return getStringValue("get_order_from");
    }

    public String getDefault_get_order_from(String defaultVal) {
        return getStringValue("get_order_from", defaultVal);
    }

    public boolean contains_get_order_from(String value) {
        return containsValue("get_order_from", value);
    }

    public void set_post_address(String value) {
        setValue("post_address", value);
    }

    public String get_post_address() {
        return getStringValue("post_address");
    }

    public String getDefault_post_address(String defaultVal) {
        return getStringValue("post_address", defaultVal);
    }

    public boolean contains_post_address(String value) {
        return containsValue("post_address", value);
    }

    public void set_post_index(String value) {
        setValue("post_index", value);
    }

    public String get_post_index() {
        return getStringValue("post_index");
    }

    public String getDefault_post_index(String defaultVal) {
        return getStringValue("post_index", defaultVal);
    }

    public boolean contains_post_index(String value) {
        return containsValue("post_index", value);
    }

    public void set_post_city(String value) {
        setValue("post_city", value);
    }

    public String get_post_city() {
        return getStringValue("post_city");
    }

    public String getDefault_post_city(String defaultVal) {
        return getStringValue("post_city", defaultVal);
    }

    public boolean contains_post_city(String value) {
        return containsValue("post_city", value);
    }

    public void set_post_region(String value) {
        setValue("post_region", value);
    }

    public String get_post_region() {
        return getStringValue("post_region");
    }

    public String getDefault_post_region(String defaultVal) {
        return getStringValue("post_region", defaultVal);
    }

    public boolean contains_post_region(String value) {
        return containsValue("post_region", value);
    }

    public void set_post_country(String value) {
        setValue("post_country", value);
    }

    public String get_post_country() {
        return getStringValue("post_country");
    }

    public String getDefault_post_country(String defaultVal) {
        return getStringValue("post_country", defaultVal);
    }

    public boolean contains_post_country(String value) {
        return containsValue("post_country", value);
    }

    public void set_comment(String value) {
        setValue("comment", value);
    }

    public String get_comment() {
        return getStringValue("comment");
    }

    public String getDefault_comment(String defaultVal) {
        return getStringValue("comment", defaultVal);
    }

    public boolean contains_comment(String value) {
        return containsValue("comment", value);
    }

    public void set_boughts_serialized(String value) {
        setValue("boughts_serialized", value);
    }

    public String get_boughts_serialized() {
        return getStringValue("boughts_serialized");
    }

    public String getDefault_boughts_serialized(String defaultVal) {
        return getStringValue("boughts_serialized", defaultVal);
    }

    public boolean contains_boughts_serialized(String value) {
        return containsValue("boughts_serialized", value);
    }

    public void set_custom_boughts_serialized(String value) {
        setValue("custom_boughts_serialized", value);
    }

    public String get_custom_boughts_serialized() {
        return getStringValue("custom_boughts_serialized");
    }

    public String getDefault_custom_boughts_serialized(String defaultVal) {
        return getStringValue("custom_boughts_serialized", defaultVal);
    }

    public boolean contains_custom_boughts_serialized(String value) {
        return containsValue("custom_boughts_serialized", value);
    }

    public void set_fav_cookie(String value) {
        setValue("fav_cookie", value);
    }

    public String get_fav_cookie() {
        return getStringValue("fav_cookie");
    }

    public String getDefault_fav_cookie(String defaultVal) {
        return getStringValue("fav_cookie", defaultVal);
    }

    public boolean contains_fav_cookie(String value) {
        return containsValue("fav_cookie", value);
    }

    public void set_old_login(String value) {
        setValue("old_login", value);
    }

    public String get_old_login() {
        return getStringValue("old_login");
    }

    public String getDefault_old_login(String defaultVal) {
        return getStringValue("old_login", defaultVal);
    }

    public boolean contains_old_login(String value) {
        return containsValue("old_login", value);
    }

}
