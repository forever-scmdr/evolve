
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Order_form
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "order_form";

    private Order_form(Item item) {
        super(item);
    }

    public static Order_form get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'order_form' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Order_form(item);
    }

    public static Order_form newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_person_type(String value) {
        setValue("person_type", value);
    }

    public String get_person_type() {
        return getStringValue("person_type");
    }

    public String getDefault_person_type(String defaultVal) {
        return getStringValue("person_type", defaultVal);
    }

    public boolean contains_person_type(String value) {
        return containsValue("person_type", value);
    }

    public void set_voucher_type(String value) {
        setValue("voucher_type", value);
    }

    public String get_voucher_type() {
        return getStringValue("voucher_type");
    }

    public String getDefault_voucher_type(String defaultVal) {
        return getStringValue("voucher_type", defaultVal);
    }

    public boolean contains_voucher_type(String value) {
        return containsValue("voucher_type", value);
    }

    public void set_first_name(String value) {
        setValue("first_name", value);
    }

    public String get_first_name() {
        return getStringValue("first_name");
    }

    public String getDefault_first_name(String defaultVal) {
        return getStringValue("first_name", defaultVal);
    }

    public boolean contains_first_name(String value) {
        return containsValue("first_name", value);
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

    public void set_last_name(String value) {
        setValue("last_name", value);
    }

    public String get_last_name() {
        return getStringValue("last_name");
    }

    public String getDefault_last_name(String defaultVal) {
        return getStringValue("last_name", defaultVal);
    }

    public boolean contains_last_name(String value) {
        return containsValue("last_name", value);
    }

    public void set_is_contractor(Byte value) {
        setValue("is_contractor", value);
    }

    public void setUI_is_contractor(String value)
        throws Exception
    {
        setValueUI("is_contractor", value);
    }

    public Byte get_is_contractor() {
        return getByteValue("is_contractor");
    }

    public Byte getDefault_is_contractor(Byte defaultVal) {
        return getByteValue("is_contractor", defaultVal);
    }

    public boolean contains_is_contractor(Byte value) {
        return containsValue("is_contractor", value);
    }

    public void set_citizen(String value) {
        setValue("citizen", value);
    }

    public String get_citizen() {
        return getStringValue("citizen");
    }

    public String getDefault_citizen(String defaultVal) {
        return getStringValue("citizen", defaultVal);
    }

    public boolean contains_citizen(String value) {
        return containsValue("citizen", value);
    }

    public void set_citizen_name(String value) {
        setValue("citizen_name", value);
    }

    public String get_citizen_name() {
        return getStringValue("citizen_name");
    }

    public String getDefault_citizen_name(String defaultVal) {
        return getStringValue("citizen_name", defaultVal);
    }

    public boolean contains_citizen_name(String value) {
        return containsValue("citizen_name", value);
    }

    public void set_birth_date(Long value) {
        setValue("birth_date", value);
    }

    public void setUI_birth_date(String value)
        throws Exception
    {
        setValueUI("birth_date", value);
    }

    public Long get_birth_date() {
        return getLongValue("birth_date");
    }

    public Long getDefault_birth_date(Long defaultVal) {
        return getLongValue("birth_date", defaultVal);
    }

    public boolean contains_birth_date(Long value) {
        return containsValue("birth_date", value);
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

    public void set_passport(String value) {
        setValue("passport", value);
    }

    public String get_passport() {
        return getStringValue("passport");
    }

    public String getDefault_passport(String defaultVal) {
        return getStringValue("passport", defaultVal);
    }

    public boolean contains_passport(String value) {
        return containsValue("passport", value);
    }

    public void set_passport_issued(String value) {
        setValue("passport_issued", value);
    }

    public String get_passport_issued() {
        return getStringValue("passport_issued");
    }

    public String getDefault_passport_issued(String defaultVal) {
        return getStringValue("passport_issued", defaultVal);
    }

    public boolean contains_passport_issued(String value) {
        return containsValue("passport_issued", value);
    }

    public void set_passport_issued_date(Long value) {
        setValue("passport_issued_date", value);
    }

    public void setUI_passport_issued_date(String value)
        throws Exception
    {
        setValueUI("passport_issued_date", value);
    }

    public Long get_passport_issued_date() {
        return getLongValue("passport_issued_date");
    }

    public Long getDefault_passport_issued_date(Long defaultVal) {
        return getLongValue("passport_issued_date", defaultVal);
    }

    public boolean contains_passport_issued_date(Long value) {
        return containsValue("passport_issued_date", value);
    }

    public void set_pay_only(Byte value) {
        setValue("pay_only", value);
    }

    public void setUI_pay_only(String value)
        throws Exception
    {
        setValueUI("pay_only", value);
    }

    public Byte get_pay_only() {
        return getByteValue("pay_only");
    }

    public Byte getDefault_pay_only(Byte defaultVal) {
        return getByteValue("pay_only", defaultVal);
    }

    public boolean contains_pay_only(Byte value) {
        return containsValue("pay_only", value);
    }

}
