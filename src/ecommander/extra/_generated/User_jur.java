
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.math.BigDecimal;

public class User_jur
    extends Item
{

    public final static String _NAME = "user_jur";
    public final static String EMAIL = "email";
    public final static String PHONE = "phone";
    public final static String PASSWORD = "password";
    public final static String REGISTERED = "registered";
    public final static String PAYMENT = "payment";
    public final static String ORGANIZATION = "organization";
    public final static String CONTRACT_NUMBER = "contract_number";
    public final static String CONTACT_NAME = "contact_name";
    public final static String CONTACT_PHONE = "contact_phone";
    public final static String ADDRESS = "address";
    public final static String CITY = "city";
    public final static String ROUTE = "route";
    public final static String DEBT = "debt";
    public final static String DISCOUNT = "discount";
    public final static String SHIP_TYPE = "ship_type";
    public final static String PAY_TYPE = "pay_type";
    public final static String NO_ACCOUNT = "no_account";
    public final static String ACCOUNT = "account";
    public final static String BANK = "bank";
    public final static String BANK_ADDRESS = "bank_address";
    public final static String BANK_CODE = "bank_code";
    public final static String UNP = "unp";
    public final static String DIRECTOR = "director";
    public final static String BASE = "base";
    public final static String BASE_NUMBER = "base_number";
    public final static String BASE_DATE = "base_date";
    public final static String COMMENT = "comment";

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

    public void set_route(String value) {
        setValue("route", value);
    }

    public String get_route() {
        return getStringValue("route");
    }

    public String getDefault_route(String defaultVal) {
        return getStringValue("route", defaultVal);
    }

    public boolean contains_route(String value) {
        return containsValue("route", value);
    }

    public void set_debt(BigDecimal value) {
        setValue("debt", value);
    }

    public void setUI_debt(String value)
        throws Exception
    {
        setValueUI("debt", value);
    }

    public BigDecimal get_debt() {
        return getDecimalValue("debt");
    }

    public BigDecimal getDefault_debt(BigDecimal defaultVal) {
        return getDecimalValue("debt", defaultVal);
    }

    public boolean contains_debt(BigDecimal value) {
        return containsValue("debt", value);
    }

    public void set_discount(Double value) {
        setValue("discount", value);
    }

    public void setUI_discount(String value)
        throws Exception
    {
        setValueUI("discount", value);
    }

    public Double get_discount() {
        return getDoubleValue("discount");
    }

    public Double getDefault_discount(Double defaultVal) {
        return getDoubleValue("discount", defaultVal);
    }

    public boolean contains_discount(Double value) {
        return containsValue("discount", value);
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

}
