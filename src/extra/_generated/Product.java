
package extra._generated;

import java.util.List;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Product
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "product";

    private Product(Item item) {
        super(item);
    }

    public static Product get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'product' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Product(item);
    }

    public static Product newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_code(String value) {
        setValue("code", value);
    }

    public String get_code() {
        return getStringValue("code");
    }

    public String getDefault_code(String defaultVal) {
        return getStringValue("code", defaultVal);
    }

    public boolean contains_code(String value) {
        return containsValue("code", value);
    }

    public void set_price(Double value) {
        setValue("price", value);
    }

    public void setUI_price(String value)
        throws Exception
    {
        setValueUI("price", value);
    }

    public Double get_price() {
        return getDoubleValue("price");
    }

    public Double getDefault_price(Double defaultVal) {
        return getDoubleValue("price", defaultVal);
    }

    public boolean contains_price(Double value) {
        return containsValue("price", value);
    }

    public void set_short(String value) {
        setValue("short", value);
    }

    public String get_short() {
        return getStringValue("short");
    }

    public String getDefault_short(String defaultVal) {
        return getStringValue("short", defaultVal);
    }

    public boolean contains_short(String value) {
        return containsValue("short", value);
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

    public void set_tech(String value) {
        setValue("tech", value);
    }

    public String get_tech() {
        return getStringValue("tech");
    }

    public String getDefault_tech(String defaultVal) {
        return getStringValue("tech", defaultVal);
    }

    public boolean contains_tech(String value) {
        return containsValue("tech", value);
    }

    public void set_apply(String value) {
        setValue("apply", value);
    }

    public String get_apply() {
        return getStringValue("apply");
    }

    public String getDefault_apply(String defaultVal) {
        return getStringValue("apply", defaultVal);
    }

    public boolean contains_apply(String value) {
        return containsValue("apply", value);
    }

    public void add_go_with(String value) {
        setValue("go_with", value);
    }

    public List<String> getAll_go_with() {
        return getStringValues("go_with");
    }

    public void remove_go_with(String value) {
        removeEqualValue("go_with", value);
    }

    public boolean contains_go_with(String value) {
        return containsValue("go_with", value);
    }

}
