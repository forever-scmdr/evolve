
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Feedback
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "feedback";

    private Feedback(Item item) {
        super(item);
    }

    public static Feedback get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'feedback' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Feedback(item);
    }

    public static Feedback newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_fio(String value) {
        setValue("fio", value);
    }

    public String get_fio() {
        return getStringValue("fio");
    }

    public String getDefault_fio(String defaultVal) {
        return getStringValue("fio", defaultVal);
    }

    public boolean contains_fio(String value) {
        return containsValue("fio", value);
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

    public void set_live_date(String value) {
        setValue("live_date", value);
    }

    public String get_live_date() {
        return getStringValue("live_date");
    }

    public String getDefault_live_date(String defaultVal) {
        return getStringValue("live_date", defaultVal);
    }

    public boolean contains_live_date(String value) {
        return containsValue("live_date", value);
    }

    public void set_room_feedback(String value) {
        setValue("room_feedback", value);
    }

    public String get_room_feedback() {
        return getStringValue("room_feedback");
    }

    public String getDefault_room_feedback(String defaultVal) {
        return getStringValue("room_feedback", defaultVal);
    }

    public boolean contains_room_feedback(String value) {
        return containsValue("room_feedback", value);
    }

    public void set_service_feedback(String value) {
        setValue("service_feedback", value);
    }

    public String get_service_feedback() {
        return getStringValue("service_feedback");
    }

    public String getDefault_service_feedback(String defaultVal) {
        return getStringValue("service_feedback", defaultVal);
    }

    public boolean contains_service_feedback(String value) {
        return containsValue("service_feedback", value);
    }

    public void set_birth(String value) {
        setValue("birth", value);
    }

    public String get_birth() {
        return getStringValue("birth");
    }

    public String getDefault_birth(String defaultVal) {
        return getStringValue("birth", defaultVal);
    }

    public boolean contains_birth(String value) {
        return containsValue("birth", value);
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

    public void set_room_type(String value) {
        setValue("room_type", value);
    }

    public String get_room_type() {
        return getStringValue("room_type");
    }

    public String getDefault_room_type(String defaultVal) {
        return getStringValue("room_type", defaultVal);
    }

    public boolean contains_room_type(String value) {
        return containsValue("room_type", value);
    }

    public void set_show(Byte value) {
        setValue("show", value);
    }

    public void setUI_show(String value)
        throws Exception
    {
        setValueUI("show", value);
    }

    public Byte get_show() {
        return getByteValue("show");
    }

    public Byte getDefault_show(Byte defaultVal) {
        return getByteValue("show", defaultVal);
    }

    public boolean contains_show(Byte value) {
        return containsValue("show", value);
    }

    public void set_answer(String value) {
        setValue("answer", value);
    }

    public String get_answer() {
        return getStringValue("answer");
    }

    public String getDefault_answer(String defaultVal) {
        return getStringValue("answer", defaultVal);
    }

    public boolean contains_answer(String value) {
        return containsValue("answer", value);
    }

}
