
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
    public final static String CONTRACT_NUMBER = "contract_number";
    public final static String INN = "inn";
    public final static String KPP = "kpp";
    public final static String ADDRESS = "address";
    public final static String CORP_EMAIL = "corp_email";
    public final static String BOSS = "boss";
    public final static String BOSS_POSITION = "boss_position";
    public final static String INFORM = "inform";
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

    public void set_corp_email(String value) {
        setValue("corp_email", value);
    }

    public String get_corp_email() {
        return getStringValue("corp_email");
    }

    public String getDefault_corp_email(String defaultVal) {
        return getStringValue("corp_email", defaultVal);
    }

    public boolean contains_corp_email(String value) {
        return containsValue("corp_email", value);
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

    public void set_boss_position(String value) {
        setValue("boss_position", value);
    }

    public String get_boss_position() {
        return getStringValue("boss_position");
    }

    public String getDefault_boss_position(String defaultVal) {
        return getStringValue("boss_position", defaultVal);
    }

    public boolean contains_boss_position(String value) {
        return containsValue("boss_position", value);
    }

    public void set_inform(String value) {
        setValue("inform", value);
    }

    public String get_inform() {
        return getStringValue("inform");
    }

    public String getDefault_inform(String defaultVal) {
        return getStringValue("inform", defaultVal);
    }

    public boolean contains_inform(String value) {
        return containsValue("inform", value);
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
