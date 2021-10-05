
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Tag
    extends Item
{

    public final static String _NAME = "tag";
    public final static String NAME = "name";
    public final static String COLOR = "color";
    public final static String TEXT_COLOR = "text_color";

    private Tag(Item item) {
        super(item);
    }

    public static Tag get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'tag' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Tag(item);
    }

    public static Tag newChild(Item parent) {
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

    public void set_color(String value) {
        setValue("color", value);
    }

    public String get_color() {
        return getStringValue("color");
    }

    public String getDefault_color(String defaultVal) {
        return getStringValue("color", defaultVal);
    }

    public boolean contains_color(String value) {
        return containsValue("color", value);
    }

    public void set_text_color(String value) {
        setValue("text_color", value);
    }

    public String get_text_color() {
        return getStringValue("text_color");
    }

    public String getDefault_text_color(String defaultVal) {
        return getStringValue("text_color", defaultVal);
    }

    public boolean contains_text_color(String value) {
        return containsValue("text_color", value);
    }

}
