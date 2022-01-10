
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Observer
    extends Item
{

    public final static String _NAME = "observer";
    public final static String OBSERVER = "observer";
    public final static String OBSERVABLE = "observable";

    private Observer(Item item) {
        super(item);
    }

    public static Observer get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'observer' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Observer(item);
    }

    public static Observer newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_observer(String value) {
        setValue("observer", value);
    }

    public String get_observer() {
        return getStringValue("observer");
    }

    public String getDefault_observer(String defaultVal) {
        return getStringValue("observer", defaultVal);
    }

    public boolean contains_observer(String value) {
        return containsValue("observer", value);
    }

    public void set_observable(String value) {
        setValue("observable", value);
    }

    public String get_observable() {
        return getStringValue("observable");
    }

    public String getDefault_observable(String defaultVal) {
        return getStringValue("observable", defaultVal);
    }

    public boolean contains_observable(String value) {
        return containsValue("observable", value);
    }

}
