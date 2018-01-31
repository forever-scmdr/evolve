
package ecommander.extra._generated;

import java.util.List;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Free_room
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "free_room";

    private Free_room(Item item) {
        super(item);
    }

    public static Free_room get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'free_room' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Free_room(item);
    }

    public static Free_room newChild(Item parent) {
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

    public void set_from(Long value) {
        setValue("from", value);
    }

    public void setUI_from(String value)
        throws Exception
    {
        setValueUI("from", value);
    }

    public Long get_from() {
        return getLongValue("from");
    }

    public Long getDefault_from(Long defaultVal) {
        return getLongValue("from", defaultVal);
    }

    public boolean contains_from(Long value) {
        return containsValue("from", value);
    }

    public void set_to(Long value) {
        setValue("to", value);
    }

    public void setUI_to(String value)
        throws Exception
    {
        setValueUI("to", value);
    }

    public Long get_to() {
        return getLongValue("to");
    }

    public Long getDefault_to(Long defaultVal) {
        return getLongValue("to", defaultVal);
    }

    public boolean contains_to(Long value) {
        return containsValue("to", value);
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

    public void set_type_name(String value) {
        setValue("type_name", value);
    }

    public String get_type_name() {
        return getStringValue("type_name");
    }

    public String getDefault_type_name(String defaultVal) {
        return getStringValue("type_name", defaultVal);
    }

    public boolean contains_type_name(String value) {
        return containsValue("type_name", value);
    }

    public void set_type(Long value) {
        setValue("type", value);
    }

    public void setUI_type(String value)
        throws Exception
    {
        setValueUI("type", value);
    }

    public Long get_type() {
        return getLongValue("type");
    }

    public Long getDefault_type(Long defaultVal) {
        return getLongValue("type", defaultVal);
    }

    public boolean contains_type(Long value) {
        return containsValue("type", value);
    }

    public void add_order_form_base(Long value) {
        setValue("order_form_base", value);
    }

    public void addUI_order_form_base(String value)
        throws Exception
    {
        setValueUI("order_form_base", value);
    }

    public List<Long> getAll_order_form_base() {
        return getLongValues("order_form_base");
    }

    public void remove_order_form_base(Long value) {
        removeEqualValue("order_form_base", value);
    }

    public boolean contains_order_form_base(Long value) {
        return containsValue("order_form_base", value);
    }

    public void add_order_form_extra(Long value) {
        setValue("order_form_extra", value);
    }

    public void addUI_order_form_extra(String value)
        throws Exception
    {
        setValueUI("order_form_extra", value);
    }

    public List<Long> getAll_order_form_extra() {
        return getLongValues("order_form_extra");
    }

    public void remove_order_form_extra(Long value) {
        removeEqualValue("order_form_extra", value);
    }

    public boolean contains_order_form_extra(Long value) {
        return containsValue("order_form_extra", value);
    }

}
