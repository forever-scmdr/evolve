
package extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Main_slider_frame
    extends Item
{

    public final static String _NAME = "main_slider_frame";
    public final static String NAME = "name";
    public final static String TEXT = "text";
    public final static String LINK_NAME = "link_name";
    public final static String LINK = "link";
    public final static String PIC = "pic";

    private Main_slider_frame(Item item) {
        super(item);
    }

    public static Main_slider_frame get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'main_slider_frame' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Main_slider_frame(item);
    }

    public static Main_slider_frame newChild(Item parent) {
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

    public void set_link_name(String value) {
        setValue("link_name", value);
    }

    public String get_link_name() {
        return getStringValue("link_name");
    }

    public String getDefault_link_name(String defaultVal) {
        return getStringValue("link_name", defaultVal);
    }

    public boolean contains_link_name(String value) {
        return containsValue("link_name", value);
    }

    public void set_link(String value) {
        setValue("link", value);
    }

    public String get_link() {
        return getStringValue("link");
    }

    public String getDefault_link(String defaultVal) {
        return getStringValue("link", defaultVal);
    }

    public boolean contains_link(String value) {
        return containsValue("link", value);
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
