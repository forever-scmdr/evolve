
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Robots_text_overrider
    extends Item
{

    public final static String _NAME = "robots_text_overrider";
    public final static String FILE_CONTENT = "file_content";

    private Robots_text_overrider(Item item) {
        super(item);
    }

    public static Robots_text_overrider get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'robots_text_overrider' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Robots_text_overrider(item);
    }

    public static Robots_text_overrider newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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
