
package extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.io.File;

public class Robots_overrider
    extends Item
{

    public final static String _NAME = "robots_overrider";
    public final static String FILE_CONTENT = "file_content";

    private Robots_overrider(Item item) {
        super(item);
    }

    public static Robots_overrider get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'robots_overrider' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Robots_overrider(item);
    }

    public static Robots_overrider newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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
