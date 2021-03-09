
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Mail_texts
    extends Item
{

    public final static String _NAME = "mail_texts";
    public final static String TOP = "top";
    public final static String BOTTOM = "bottom";

    private Mail_texts(Item item) {
        super(item);
    }

    public static Mail_texts get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'mail_texts' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Mail_texts(item);
    }

    public static Mail_texts newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_top(String value) {
        setValue("top", value);
    }

    public String get_top() {
        return getStringValue("top");
    }

    public String getDefault_top(String defaultVal) {
        return getStringValue("top", defaultVal);
    }

    public boolean contains_top(String value) {
        return containsValue("top", value);
    }

    public void set_bottom(String value) {
        setValue("bottom", value);
    }

    public String get_bottom() {
        return getStringValue("bottom");
    }

    public String getDefault_bottom(String defaultVal) {
        return getStringValue("bottom", defaultVal);
    }

    public boolean contains_bottom(String value) {
        return containsValue("bottom", value);
    }

}
