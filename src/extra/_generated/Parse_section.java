
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Parse_section
    extends Item
{

    public final static String _NAME = "parse_section";
    public final static String ITEM_URLS = "item_urls";
    public final static String ITEM_URLS_BACKUP = "item_urls_backup";

    private Parse_section(Item item) {
        super(item);
    }

    public static Parse_section get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'parse_section' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Parse_section(item);
    }

    public static Parse_section newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_item_urls(String value) {
        setValue("item_urls", value);
    }

    public String get_item_urls() {
        return getStringValue("item_urls");
    }

    public String getDefault_item_urls(String defaultVal) {
        return getStringValue("item_urls", defaultVal);
    }

    public boolean contains_item_urls(String value) {
        return containsValue("item_urls", value);
    }

    public void set_item_urls_backup(String value) {
        setValue("item_urls_backup", value);
    }

    public String get_item_urls_backup() {
        return getStringValue("item_urls_backup");
    }

    public String getDefault_item_urls_backup(String defaultVal) {
        return getStringValue("item_urls_backup", defaultVal);
    }

    public boolean contains_item_urls_backup(String value) {
        return containsValue("item_urls_backup", value);
    }

}
