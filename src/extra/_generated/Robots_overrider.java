
package extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Robots_overrider
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "robots_overrider";

    private Robots_overrider(Item item) {
        super(item);
    }

    public static Robots_overrider get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'robots_overrider' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Robots_overrider(item);
    }

    public static Robots_overrider newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_file_content(File value) {
        setValue("file_content", value);
    }

    public File get_file_content() {
        return getFileValue("file_content", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_file_content(File value) {
        return containsValue("file_content", value);
    }

}
