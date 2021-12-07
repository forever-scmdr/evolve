
package extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class User_jur
    extends Item
{

    public final static String _NAME = "user_jur";
    public final static String EMAIL = "email";
    public final static String PHONE = "phone";
    public final static String PASSWORD = "password";
    public final static String DISCOUNT = "discount";
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
    public final static String ORGANIZATION = "organization";
    public final static String CONTRACT_NUMBER = "contract_number";
    public final static String CONTACT_NAME = "contact_name";
    public final static String CONTACT_PHONE = "contact_phone";
    public final static String ADDRESS = "address";
    public final static String NO_ACCOUNT = "no_account";
    public final static String ACCOUNT = "account";
    public final static String BANK = "bank";
    public final static String BANK_ADDRESS = "bank_address";
    public final static String BANK_CODE = "bank_code";
    public final static String UNP = "unp";
    public final static String DIRECTOR = "director";
    public final static String BOSS = "boss";
    public final static String BASE = "base";
    public final static String BASE_NUMBER = "base_number";
    public final static String BASE_DATE = "base_date";
    public final static String FUND = "fund";
    public final static String SEND_CONTRACT_TO = "send_contract_to";

    private User_jur(Item item) {
        super(item);
    }

    public static User_jur get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'user_jur' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new User_jur(item);
    }

    public static User_jur newChild(Item parent) {
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

    public void set_organization(String value) {
        setValue("organization", value);
    }

    public String get_organization() {
        return getStringValue("organization");
    }

    public String getDefault_organization(String defaultVal) {
        return getStringValue("organization", defaultVal);
    }

    public boolean contains_organization(String value) {
        return containsValue("organization", value);
    }

    public void set_contract_number(String value) {
        setValue("contract_number", value);
    }

    public String get_contract_number() {
        return getStringValue("contract_number");
    }

    public String getDefault_contract_number(String defaultVal) {
        return getStringValue("contract_number", defaultVal);
    }

    public boolean contains_contract_number(String value) {
        return containsValue("contract_number", value);
    }

    public void set_contact_name(String value) {
        setValue("contact_name", value);
    }

    public String get_contact_name() {
        return getStringValue("contact_name");
    }

    public String getDefault_contact_name(String defaultVal) {
        return getStringValue("contact_name", defaultVal);
    }

    public boolean contains_contact_name(String value) {
        return containsValue("contact_name", value);
    }

    public void set_contact_phone(String value) {
        setValue("contact_phone", value);
    }

    public String get_contact_phone() {
        return getStringValue("contact_phone");
    }

    public String getDefault_contact_phone(String defaultVal) {
        return getStringValue("contact_phone", defaultVal);
    }

    public boolean contains_contact_phone(String value) {
        return containsValue("contact_phone", value);
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

    public void set_no_account(String value) {
        setValue("no_account", value);
    }

    public String get_no_account() {
        return getStringValue("no_account");
    }

    public String getDefault_no_account(String defaultVal) {
        return getStringValue("no_account", defaultVal);
    }

    public boolean contains_no_account(String value) {
        return containsValue("no_account", value);
    }

    public void set_account(String value) {
        setValue("account", value);
    }

    public String get_account() {
        return getStringValue("account");
    }

    public String getDefault_account(String defaultVal) {
        return getStringValue("account", defaultVal);
    }

    public boolean contains_account(String value) {
        return containsValue("account", value);
    }

    public void set_bank(String value) {
        setValue("bank", value);
    }

    public String get_bank() {
        return getStringValue("bank");
    }

    public String getDefault_bank(String defaultVal) {
        return getStringValue("bank", defaultVal);
    }

    public boolean contains_bank(String value) {
        return containsValue("bank", value);
    }

    public void set_bank_address(String value) {
        setValue("bank_address", value);
    }

    public String get_bank_address() {
        return getStringValue("bank_address");
    }

    public String getDefault_bank_address(String defaultVal) {
        return getStringValue("bank_address", defaultVal);
    }

    public boolean contains_bank_address(String value) {
        return containsValue("bank_address", value);
    }

    public void set_bank_code(String value) {
        setValue("bank_code", value);
    }

    public String get_bank_code() {
        return getStringValue("bank_code");
    }

    public String getDefault_bank_code(String defaultVal) {
        return getStringValue("bank_code", defaultVal);
    }

    public boolean contains_bank_code(String value) {
        return containsValue("bank_code", value);
    }

    public void set_unp(String value) {
        setValue("unp", value);
    }

    public String get_unp() {
        return getStringValue("unp");
    }

    public String getDefault_unp(String defaultVal) {
        return getStringValue("unp", defaultVal);
    }

    public boolean contains_unp(String value) {
        return containsValue("unp", value);
    }

    public void set_director(String value) {
        setValue("director", value);
    }

    public String get_director() {
        return getStringValue("director");
    }

    public String getDefault_director(String defaultVal) {
        return getStringValue("director", defaultVal);
    }

    public boolean contains_director(String value) {
        return containsValue("director", value);
    }

    public void set_boss(String value) {
        setValue("boss", value);
    }

    public String get_boss() {
        return getStringValue("boss");
    }

    public String getDefault_boss(String defaultVal) {
        return getStringValue("boss", defaultVal);
    }

    public boolean contains_boss(String value) {
        return containsValue("boss", value);
    }

    public void set_base(String value) {
        setValue("base", value);
    }

    public String get_base() {
        return getStringValue("base");
    }

    public String getDefault_base(String defaultVal) {
        return getStringValue("base", defaultVal);
    }

    public boolean contains_base(String value) {
        return containsValue("base", value);
    }

    public void set_base_number(String value) {
        setValue("base_number", value);
    }

    public String get_base_number() {
        return getStringValue("base_number");
    }

    public String getDefault_base_number(String defaultVal) {
        return getStringValue("base_number", defaultVal);
    }

    public boolean contains_base_number(String value) {
        return containsValue("base_number", value);
    }

    public void set_base_date(String value) {
        setValue("base_date", value);
    }

    public String get_base_date() {
        return getStringValue("base_date");
    }

    public String getDefault_base_date(String defaultVal) {
        return getStringValue("base_date", defaultVal);
    }

    public boolean contains_base_date(String value) {
        return containsValue("base_date", value);
    }

    public void set_fund(String value) {
        setValue("fund", value);
    }

    public String get_fund() {
        return getStringValue("fund");
    }

    public String getDefault_fund(String defaultVal) {
        return getStringValue("fund", defaultVal);
    }

    public boolean contains_fund(String value) {
        return containsValue("fund", value);
    }

    public void set_send_contract_to(String value) {
        setValue("send_contract_to", value);
    }

    public String get_send_contract_to() {
        return getStringValue("send_contract_to");
    }

    public String getDefault_send_contract_to(String defaultVal) {
        return getStringValue("send_contract_to", defaultVal);
    }

    public boolean contains_send_contract_to(String value) {
        return containsValue("send_contract_to", value);
    }

}
