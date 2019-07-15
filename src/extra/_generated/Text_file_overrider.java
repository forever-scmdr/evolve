
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Text_file_overrider
    extends Item
{

    public final static String _NAME = "text_file_overrider";
    public final static String NAME = "name";
    public final static String SRC = "src";
    public final static String FILE_CONTENT = "file_content";

    private Text_file_overrider(Item item) {
        super(item);
    }

    public static Text_file_overrider get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'text_file_overrider' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Text_file_overrider(item);
    }

    public static Text_file_overrider newChild(Item parent) {
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

    public void set_src(String value) {
        setValue("src", value);
    }

    public String get_src() {
        return getStringValue("src");
    }

    public String getDefault_src(String defaultVal) {
        return getStringValue("src", defaultVal);
    }

    public boolean contains_src(String value) {
        return containsValue("src", value);
    }

    public void set_file_content(String value) {
        setValue("file_content", value);
    }

    public String get_file_content() {
        return getStringValue("file_content");
    }

    public String getDefault_file_content(String defaultVal) {
        return getStringValue("file_content", defaultVal);
    }

    public boolean contains_file_content(String value) {
        return containsValue("file_content", value);
    }

}
