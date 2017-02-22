
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Order
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "order";

    private Order(Item item) {
        super(item);
    }

    public static Order get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'order' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Order(item);
    }

    public static Order newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_pin(String value) {
        setValue("pin", value);
    }

    public String get_pin() {
        return getStringValue("pin");
    }

    public String getDefault_pin(String defaultVal) {
        return getStringValue("pin", defaultVal);
    }

    public boolean contains_pin(String value) {
        return containsValue("pin", value);
    }

    public void set_received_date(Long value) {
        setValue("received_date", value);
    }

    public void setUI_received_date(String value)
        throws Exception
    {
        setValueUI("received_date", value);
    }

    public Long get_received_date() {
        return getLongValue("received_date");
    }

    public Long getDefault_received_date(Long defaultVal) {
        return getLongValue("received_date", defaultVal);
    }

    public boolean contains_received_date(Long value) {
        return containsValue("received_date", value);
    }

    public void set_pay_until_date(Long value) {
        setValue("pay_until_date", value);
    }

    public void setUI_pay_until_date(String value)
        throws Exception
    {
        setValueUI("pay_until_date", value);
    }

    public Long get_pay_until_date() {
        return getLongValue("pay_until_date");
    }

    public Long getDefault_pay_until_date(Long defaultVal) {
        return getLongValue("pay_until_date", defaultVal);
    }

    public boolean contains_pay_until_date(Long value) {
        return containsValue("pay_until_date", value);
    }

    public void set_contract_date(Long value) {
        setValue("contract_date", value);
    }

    public void setUI_contract_date(String value)
        throws Exception
    {
        setValueUI("contract_date", value);
    }

    public Long get_contract_date() {
        return getLongValue("contract_date");
    }

    public Long getDefault_contract_date(Long defaultVal) {
        return getLongValue("contract_date", defaultVal);
    }

    public boolean contains_contract_date(Long value) {
        return containsValue("contract_date", value);
    }

    public void set_extra(String value) {
        setValue("extra", value);
    }

    public String get_extra() {
        return getStringValue("extra");
    }

    public String getDefault_extra(String defaultVal) {
        return getStringValue("extra", defaultVal);
    }

    public boolean contains_extra(String value) {
        return containsValue("extra", value);
    }

    public void set_sum(Double value) {
        setValue("sum", value);
    }

    public void setUI_sum(String value)
        throws Exception
    {
        setValueUI("sum", value);
    }

    public Double get_sum() {
        return getDoubleValue("sum");
    }

    public Double getDefault_sum(Double defaultVal) {
        return getDoubleValue("sum", defaultVal);
    }

    public boolean contains_sum(Double value) {
        return containsValue("sum", value);
    }

    public void set_cur(String value) {
        setValue("cur", value);
    }

    public String get_cur() {
        return getStringValue("cur");
    }

    public String getDefault_cur(String defaultVal) {
        return getStringValue("cur", defaultVal);
    }

    public boolean contains_cur(String value) {
        return containsValue("cur", value);
    }

    public void set_cur_code(String value) {
        setValue("cur_code", value);
    }

    public String get_cur_code() {
        return getStringValue("cur_code");
    }

    public String getDefault_cur_code(String defaultVal) {
        return getStringValue("cur_code", defaultVal);
    }

    public boolean contains_cur_code(String value) {
        return containsValue("cur_code", value);
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

    public void set_main_form(Long value) {
        setValue("main_form", value);
    }

    public void setUI_main_form(String value)
        throws Exception
    {
        setValueUI("main_form", value);
    }

    public Long get_main_form() {
        return getLongValue("main_form");
    }

    public Long getDefault_main_form(Long defaultVal) {
        return getLongValue("main_form", defaultVal);
    }

    public boolean contains_main_form(Long value) {
        return containsValue("main_form", value);
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

    public void set_contract_agreed(Byte value) {
        setValue("contract_agreed", value);
    }

    public void setUI_contract_agreed(String value)
        throws Exception
    {
        setValueUI("contract_agreed", value);
    }

    public Byte get_contract_agreed() {
        return getByteValue("contract_agreed");
    }

    public Byte getDefault_contract_agreed(Byte defaultVal) {
        return getByteValue("contract_agreed", defaultVal);
    }

    public boolean contains_contract_agreed(Byte value) {
        return containsValue("contract_agreed", value);
    }

    public void set_contract(File value) {
        setValue("contract", value);
    }

    public File get_contract() {
        return getFileValue("contract", AppContext.getFilesDirPath());
    }

    public boolean contains_contract(File value) {
        return containsValue("contract", value);
    }

    public void set_bill(File value) {
        setValue("bill", value);
    }

    public File get_bill() {
        return getFileValue("bill", AppContext.getFilesDirPath());
    }

    public boolean contains_bill(File value) {
        return containsValue("bill", value);
    }

}
