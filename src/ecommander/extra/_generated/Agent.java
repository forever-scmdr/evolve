
package ecommander.extra._generated;

import java.util.List;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Agent
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "agent";

    private Agent(Item item) {
        super(item);
    }

    public static Agent get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'agent' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Agent(item);
    }

    public static Agent newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_country(String value) {
        setValue("country", value);
    }

    public String get_country() {
        return getStringValue("country");
    }

    public String getDefault_country(String defaultVal) {
        return getStringValue("country", defaultVal);
    }

    public boolean contains_country(String value) {
        return containsValue("country", value);
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

    public void set_email_2(String value) {
        setValue("email_2", value);
    }

    public String get_email_2() {
        return getStringValue("email_2");
    }

    public String getDefault_email_2(String defaultVal) {
        return getStringValue("email_2", defaultVal);
    }

    public boolean contains_email_2(String value) {
        return containsValue("email_2", value);
    }

    public void set_email_3(String value) {
        setValue("email_3", value);
    }

    public String get_email_3() {
        return getStringValue("email_3");
    }

    public String getDefault_email_3(String defaultVal) {
        return getStringValue("email_3", defaultVal);
    }

    public boolean contains_email_3(String value) {
        return containsValue("email_3", value);
    }

    public void set_site(String value) {
        setValue("site", value);
    }

    public String get_site() {
        return getStringValue("site");
    }

    public String getDefault_site(String defaultVal) {
        return getStringValue("site", defaultVal);
    }

    public boolean contains_site(String value) {
        return containsValue("site", value);
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

    public void set_boss_position_dat(String value) {
        setValue("boss_position_dat", value);
    }

    public String get_boss_position_dat() {
        return getStringValue("boss_position_dat");
    }

    public String getDefault_boss_position_dat(String defaultVal) {
        return getStringValue("boss_position_dat", defaultVal);
    }

    public boolean contains_boss_position_dat(String value) {
        return containsValue("boss_position_dat", value);
    }

    public void set_boss_name(String value) {
        setValue("boss_name", value);
    }

    public String get_boss_name() {
        return getStringValue("boss_name");
    }

    public String getDefault_boss_name(String defaultVal) {
        return getStringValue("boss_name", defaultVal);
    }

    public boolean contains_boss_name(String value) {
        return containsValue("boss_name", value);
    }

    public void set_boss_name_dat(String value) {
        setValue("boss_name_dat", value);
    }

    public String get_boss_name_dat() {
        return getStringValue("boss_name_dat");
    }

    public String getDefault_boss_name_dat(String defaultVal) {
        return getStringValue("boss_name_dat", defaultVal);
    }

    public boolean contains_boss_name_dat(String value) {
        return containsValue("boss_name_dat", value);
    }

    public void set_boss_greetings(String value) {
        setValue("boss_greetings", value);
    }

    public String get_boss_greetings() {
        return getStringValue("boss_greetings");
    }

    public String getDefault_boss_greetings(String defaultVal) {
        return getStringValue("boss_greetings", defaultVal);
    }

    public boolean contains_boss_greetings(String value) {
        return containsValue("boss_greetings", value);
    }

    public void set_type(String value) {
        setValue("type", value);
    }

    public String get_type() {
        return getStringValue("type");
    }

    public String getDefault_type(String defaultVal) {
        return getStringValue("type", defaultVal);
    }

    public boolean contains_type(String value) {
        return containsValue("type", value);
    }

    public void set_branch(String value) {
        setValue("branch", value);
    }

    public String get_branch() {
        return getStringValue("branch");
    }

    public String getDefault_branch(String defaultVal) {
        return getStringValue("branch", defaultVal);
    }

    public boolean contains_branch(String value) {
        return containsValue("branch", value);
    }

    public void set_desc(String value) {
        setValue("desc", value);
    }

    public String get_desc() {
        return getStringValue("desc");
    }

    public String getDefault_desc(String defaultVal) {
        return getStringValue("desc", defaultVal);
    }

    public boolean contains_desc(String value) {
        return containsValue("desc", value);
    }

    public void add_tags(String value) {
        setValue("tags", value);
    }

    public List<String> getAll_tags() {
        return getStringValues("tags");
    }

    public void remove_tags(String value) {
        removeEqualValue("tags", value);
    }

    public boolean contains_tags(String value) {
        return containsValue("tags", value);
    }

    public void add_emails_sent(Long value) {
        setValue("emails_sent", value);
    }

    public void addUI_emails_sent(String value)
        throws Exception
    {
        setValueUI("emails_sent", value);
    }

    public List<Long> getAll_emails_sent() {
        return getLongValues("emails_sent");
    }

    public void remove_emails_sent(Long value) {
        removeEqualValue("emails_sent", value);
    }

    public boolean contains_emails_sent(Long value) {
        return containsValue("emails_sent", value);
    }

}
