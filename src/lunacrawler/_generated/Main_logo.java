
package lunacrawler._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Main_logo
    extends Item
{

    public final static String _NAME = "main_logo";
    public final static String NAME = "name";
    public final static String PIC = "pic";

    private Main_logo(Item item) {
        super(item);
    }

    public static Main_logo get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'main_logo' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Main_logo(item);
    }

    public static Main_logo newChild(Item parent) {
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

    public void set_pic(File value) {
        setValue("pic", value);
    }

    public File get_pic() {
        return getFileValue("pic", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_pic(File value) {
        return containsValue("pic", value);
    }

}
