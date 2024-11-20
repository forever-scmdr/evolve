
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class All_agents
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "all_agents";

    private All_agents(Item item) {
        super(item);
    }

    public static All_agents get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'all_agents' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new All_agents(item);
    }

    public static All_agents newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_file(File value) {
        setValue("file", value);
    }

    public File get_file() {
        return getFileValue("file", AppContext.getFilesDirPath());
    }

    public boolean contains_file(File value) {
        return containsValue("file", value);
    }

}
