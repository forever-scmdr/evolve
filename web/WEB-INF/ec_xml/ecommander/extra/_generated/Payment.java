
package ecommander.extra._generated;

import java.util.List;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Payment
    extends Item
{

    public final static String _NAME = "payment";
    public final static String NAME = "name";
    public final static String SUFFIX = "suffix";
    public final static String TEXT = "text";
    public final static String CURRENCY = "currency";

    private Payment(Item item) {
        super(item);
    }

    public static Payment get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'payment' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Payment(item);
    }

    public static Payment newChild(Item parent) {
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

    public void set_suffix(String value) {
        setValue("suffix", value);
    }

    public String get_suffix() {
        return getStringValue("suffix");
    }

    public String getDefault_suffix(String defaultVal) {
        return getStringValue("suffix", defaultVal);
    }

    public boolean contains_suffix(String value) {
        return containsValue("suffix", value);
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

    public void add_currency(String value) {
        setValue("currency", value);
    }

    public List<String> getAll_currency() {
        return getStringValues("currency");
    }

    public void remove_currency(String value) {
        removeEqualValue("currency", value);
    }

    public boolean contains_currency(String value) {
        return containsValue("currency", value);
    }

}
