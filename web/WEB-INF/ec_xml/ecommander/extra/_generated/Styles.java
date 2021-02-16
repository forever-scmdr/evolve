
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Styles
    extends Item
{

    public final static String _NAME = "styles";
    public final static String CSS = "css";

    private Styles(Item item) {
        super(item);
    }

    public static Styles get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'styles' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Styles(item);
    }

    public static Styles newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_css(File value) {
        setValue("css", value);
    }

    public File get_css() {
        return getFileValue("css", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_css(File value) {
        return containsValue("css", value);
    }

}
