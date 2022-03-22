
package extra._generated;

import java.util.List;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Product_labels
    extends Item
{

    public final static String _NAME = "product_labels";
    public final static String TAG = "tag";
    public final static String MARK = "mark";
    public final static String LABEL = "label";

    private Product_labels(Item item) {
        super(item);
    }

    public static Product_labels get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'product_labels' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Product_labels(item);
    }

    public static Product_labels newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void add_tag(String value) {
        setValue("tag", value);
    }

    public List<String> getAll_tag() {
        return getStringValues("tag");
    }

    public void remove_tag(String value) {
        removeEqualValue("tag", value);
    }

    public boolean contains_tag(String value) {
        return containsValue("tag", value);
    }

    public void add_mark(String value) {
        setValue("mark", value);
    }

    public List<String> getAll_mark() {
        return getStringValues("mark");
    }

    public void remove_mark(String value) {
        removeEqualValue("mark", value);
    }

    public boolean contains_mark(String value) {
        return containsValue("mark", value);
    }

    public void add_label(String value) {
        setValue("label", value);
    }

    public List<String> getAll_label() {
        return getStringValues("label");
    }

    public void remove_label(String value) {
        removeEqualValue("label", value);
    }

    public boolean contains_label(String value) {
        return containsValue("label", value);
    }

}
