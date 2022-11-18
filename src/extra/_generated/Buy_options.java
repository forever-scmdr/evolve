
package extra._generated;

import java.util.List;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Buy_options
    extends Item
{

    public final static String _NAME = "buy_options";
    public final static String OPTION = "option";
    public final static String CURRENCY = "currency";

    private Buy_options(Item item) {
        super(item);
    }

    public static Buy_options get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'buy_options' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Buy_options(item);
    }

    public static Buy_options newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void add_option(String value) {
        setValue("option", value);
    }

    public List<String> getAll_option() {
        return getStringValues("option");
    }

    public void remove_option(String value) {
        removeEqualValue("option", value);
    }

    public boolean contains_option(String value) {
        return containsValue("option", value);
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
