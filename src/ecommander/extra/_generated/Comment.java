
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Comment
    extends Item
{

    public final static String _NAME = "comment";
    public final static String NAME = "name";
    public final static String EMAIL = "email";
    public final static String DATE = "date";
    public final static String TEXT = "text";
    public final static String MODERATED = "moderated";

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

    public void set_moderated(Byte value) {
        setValue("moderated", value);
    }

    public void setUI_moderated(String value)
        throws Exception
    {
        setValueUI("moderated", value);
    }

    public Byte get_moderated() {
        return getByteValue("moderated");
    }

    public Byte getDefault_moderated(Byte defaultVal) {
        return getByteValue("moderated", defaultVal);
    }

    public boolean contains_moderated(Byte value) {
        return containsValue("moderated", value);
    }

}
