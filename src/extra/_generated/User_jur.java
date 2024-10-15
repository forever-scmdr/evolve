
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

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
    public final static String ORGN = "orgn";
    public final static String KPP = "kpp";
    public final static String INN = "inn";
    public final static String ADDRESS = "address";
    public final static String FACT_ADDRESS = "fact_address";
    public final static String POST_ADDRESS = "post_address";
    public final static String WEB_SITE = "web_site";
    public final static String CONTACT_NAME = "contact_name";
    public final static String CONTACT_PHONE = "contact_phone";
    public final static String CONTACT_EMAIL = "contact_email";
    public final static String SHIP_TYPE = "ship_type";
    public final static String PAY_TYPE = "pay_type";
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

    public void set_orgn(String value) {
        setValue("orgn", value);
    }

    public String get_orgn() {
        return getStringValue("orgn");
    }

    public String getDefault_orgn(String defaultVal) {
        return getStringValue("orgn", defaultVal);
    }

    public boolean contains_orgn(String value) {
        return containsValue("orgn", value);
    }

    public void set_kpp(String value) {
        setValue("kpp", value);
    }

    public String get_kpp() {
        return getStringValue("kpp");
    }

    public String getDefault_kpp(String defaultVal) {
        return getStringValue("kpp", defaultVal);
    }

    public boolean contains_kpp(String value) {
        return containsValue("kpp", value);
    }

    public void set_inn(String value) {
        setValue("inn", value);
    }

    public String get_inn() {
        return getStringValue("inn");
    }

    public String getDefault_inn(String defaultVal) {
        return getStringValue("inn", defaultVal);
    }

    public boolean contains_inn(String value) {
        return containsValue("inn", value);
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

    public void set_fact_address(String value) {
        setValue("fact_address", value);
    }

    public String get_fact_address() {
        return getStringValue("fact_address");
    }

    public String getDefault_fact_address(String defaultVal) {
        return getStringValue("fact_address", defaultVal);
    }

    public boolean contains_fact_address(String value) {
        return containsValue("fact_address", value);
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

    public void set_web_site(String value) {
        setValue("web_site", value);
    }

    public String get_web_site() {
        return getStringValue("web_site");
    }

    public String getDefault_web_site(String defaultVal) {
        return getStringValue("web_site", defaultVal);
    }

    public boolean contains_web_site(String value) {
        return containsValue("web_site", value);
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

    public void set_contact_email(String value) {
        setValue("contact_email", value);
    }

    public String get_contact_email() {
        return getStringValue("contact_email");
    }

    public String getDefault_contact_email(String defaultVal) {
        return getStringValue("contact_email", defaultVal);
    }

    public boolean contains_contact_email(String value) {
        return containsValue("contact_email", value);
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
