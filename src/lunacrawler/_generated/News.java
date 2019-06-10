
package lunacrawler._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class News
    extends Item
{

    public final static String _NAME = "news";
    public final static String NAME = "name";
    public final static String ON_MAIN = "on_main";

    private News(Item item) {
        super(item);
    }

    public static News get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'news' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new News(item);
    }

    public static News newChild(Item parent) {
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

    public void set_on_main(String value) {
        setValue("on_main", value);
    }

    public String get_on_main() {
        return getStringValue("on_main");
    }

    public String getDefault_on_main(String defaultVal) {
        return getStringValue("on_main", defaultVal);
    }

    public boolean contains_on_main(String value) {
        return containsValue("on_main", value);
    }

}
