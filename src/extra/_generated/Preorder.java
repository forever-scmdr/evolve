
package extra._generated;

import java.util.List;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Preorder
    extends Item
{

    public final static String _NAME = "preorder";
    public final static String EMAIL = "email";
    public final static String CODE = "code";

    private Preorder(Item item) {
        super(item);
    }

    public static Preorder get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'preorder' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Preorder(item);
    }

    public static Preorder newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_email(String value) {
        setValue("email", value);
    }

    public String get_email() {
        return getStringValue("email");
    }

    public String getDefault_email(String defaultVal) {
        return getStringValue("email", defaultVal);
    }

    public boolean contains_email(String value) {
        return containsValue("email", value);
    }

    public void add_code(String value) {
        setValue("code", value);
    }

    public List<String> getAll_code() {
        return getStringValues("code");
    }

    public void remove_code(String value) {
        removeEqualValue("code", value);
    }

    public boolean contains_code(String value) {
        return containsValue("code", value);
    }

}
