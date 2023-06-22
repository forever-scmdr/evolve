
package ecommander.extra._generated;

import java.util.List;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Device
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "device";

    private Device(Item item) {
        super(item);
    }

    public static Device get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'device' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Device(item);
    }

    public static Device newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_ab_num(String value) {
        setValue("ab_num", value);
    }

    public String get_ab_num() {
        return getStringValue("ab_num");
    }

    public String getDefault_ab_num(String defaultVal) {
        return getStringValue("ab_num", defaultVal);
    }

    public boolean contains_ab_num(String value) {
        return containsValue("ab_num", value);
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

    public void set_last_updated(Long value) {
        setValue("last_updated", value);
    }

    public void setUI_last_updated(String value)
        throws Exception
    {
        setValueUI("last_updated", value);
    }

    public Long get_last_updated() {
        return getLongValue("last_updated");
    }

    public Long getDefault_last_updated(Long defaultVal) {
        return getLongValue("last_updated", defaultVal);
    }

    public boolean contains_last_updated(Long value) {
        return containsValue("last_updated", value);
    }

    public void add_last_updated_dir(String value) {
        setValue("last_updated_dir", value);
    }

    public List<String> getAll_last_updated_dir() {
        return getStringValues("last_updated_dir");
    }

    public void remove_last_updated_dir(String value) {
        removeEqualValue("last_updated_dir", value);
    }

    public boolean contains_last_updated_dir(String value) {
        return containsValue("last_updated_dir", value);
    }

}
