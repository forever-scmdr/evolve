
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Comment
    extends Item
{

    public final static String _NAME = "comment";
    public final static String NAME = "name";
    public final static String DATE = "date";
    public final static String VISIT_DATE = "visit_date";
    public final static String BIRTH_YEAR = "birth_year";
    public final static String COUNTRY = "country";
    public final static String FLAG = "flag";
    public final static String TYPE = "type";
    public final static String EMAIL = "email";
    public final static String PHONE = "phone";
    public final static String TEXT = "text";

    private Comment(Item item) {
        super(item);
    }

    public static Comment get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'comment' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Comment(item);
    }

    public static Comment newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_name(String value) {
        setValue("name", value);
    }

    public String get_name() {
        return getStringValue("name");
    }

    public String getDefault_name(String defaultVal) {
        return getStringValue("name", defaultVal);
    }

    public boolean contains_name(String value) {
        return containsValue("name", value);
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

    public void set_visit_date(String value) {
        setValue("visit_date", value);
    }

    public String get_visit_date() {
        return getStringValue("visit_date");
    }

    public String getDefault_visit_date(String defaultVal) {
        return getStringValue("visit_date", defaultVal);
    }

    public boolean contains_visit_date(String value) {
        return containsValue("visit_date", value);
    }

    public void set_birth_year(String value) {
        setValue("birth_year", value);
    }

    public String get_birth_year() {
        return getStringValue("birth_year");
    }

    public String getDefault_birth_year(String defaultVal) {
        return getStringValue("birth_year", defaultVal);
    }

    public boolean contains_birth_year(String value) {
        return containsValue("birth_year", value);
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

    public void set_flag(String value) {
        setValue("flag", value);
    }

    public String get_flag() {
        return getStringValue("flag");
    }

    public String getDefault_flag(String defaultVal) {
        return getStringValue("flag", defaultVal);
    }

    public boolean contains_flag(String value) {
        return containsValue("flag", value);
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

    public void set_text(String value) {
        setValue("text", value);
    }

    public String get_text() {
        return getStringValue("text");
    }

    public String getDefault_text(String defaultVal) {
        return getStringValue("text", defaultVal);
    }

    public boolean contains_text(String value) {
        return containsValue("text", value);
    }

}
