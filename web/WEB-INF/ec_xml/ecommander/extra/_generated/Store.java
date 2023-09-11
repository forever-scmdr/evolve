
package ecommander.extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.io.File;

public class Store
    extends Item
{

    public final static String _NAME = "store";
    public final static String NAME = "name";
    public final static String NUMBER = "number";
    public final static String FILE_HASH = "file_hash";
    public final static String OLD_FILE_HASH = "old_file_hash";
    public final static String BIG_INTEGRATION = "big_integration";

    private Store(Item item) {
        super(item);
    }

    public static Store get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'store' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Store(item);
    }

    public static Store newChild(Item parent) {
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

    public void set_number(Integer value) {
        setValue("number", value);
    }

    public void setUI_number(String value)
        throws Exception
    {
        setValueUI("number", value);
    }

    public Integer get_number() {
        return getIntValue("number");
    }

    public Integer getDefault_number(Integer defaultVal) {
        return getIntValue("number", defaultVal);
    }

    public boolean contains_number(Integer value) {
        return containsValue("number", value);
    }

    public void set_file_hash(Integer value) {
        setValue("file_hash", value);
    }

    public void setUI_file_hash(String value)
        throws Exception
    {
        setValueUI("file_hash", value);
    }

    public Integer get_file_hash() {
        return getIntValue("file_hash");
    }

    public Integer getDefault_file_hash(Integer defaultVal) {
        return getIntValue("file_hash", defaultVal);
    }

    public boolean contains_file_hash(Integer value) {
        return containsValue("file_hash", value);
    }

    public void set_old_file_hash(Integer value) {
        setValue("old_file_hash", value);
    }

    public void setUI_old_file_hash(String value)
        throws Exception
    {
        setValueUI("old_file_hash", value);
    }

    public Integer get_old_file_hash() {
        return getIntValue("old_file_hash");
    }

    public Integer getDefault_old_file_hash(Integer defaultVal) {
        return getIntValue("old_file_hash", defaultVal);
    }

    public boolean contains_old_file_hash(Integer value) {
        return containsValue("old_file_hash", value);
    }

    public void set_big_integration(File value) {
        setValue("big_integration", value);
    }

    public File get_big_integration() {
        return getFileValue("big_integration", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_big_integration(File value) {
        return containsValue("big_integration", value);
    }

}
