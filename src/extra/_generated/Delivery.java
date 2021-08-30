
package extra._generated;

import java.util.List;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Delivery
    extends Item
{

    public final static String _NAME = "delivery";
    public final static String NAME = "name";
    public final static String SUFFIX = "suffix";
    public final static String TEXT = "text";
    public final static String ASK_ADDRESS = "ask_address";
    public final static String SHOW_STORES = "show_stores";
    public final static String CURRENCY = "currency";
    public final static String COUNTRY = "country";

    private Delivery(Item item) {
        super(item);
    }

    public static Delivery get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'delivery' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Delivery(item);
    }

    public static Delivery newChild(Item parent) {
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

    public void set_ask_address(Byte value) {
        setValue("ask_address", value);
    }

    public void setUI_ask_address(String value)
        throws Exception
    {
        setValueUI("ask_address", value);
    }

    public Byte get_ask_address() {
        return getByteValue("ask_address");
    }

    public Byte getDefault_ask_address(Byte defaultVal) {
        return getByteValue("ask_address", defaultVal);
    }

    public boolean contains_ask_address(Byte value) {
        return containsValue("ask_address", value);
    }

    public void set_show_stores(Byte value) {
        setValue("show_stores", value);
    }

    public void setUI_show_stores(String value)
        throws Exception
    {
        setValueUI("show_stores", value);
    }

    public Byte get_show_stores() {
        return getByteValue("show_stores");
    }

    public Byte getDefault_show_stores(Byte defaultVal) {
        return getByteValue("show_stores", defaultVal);
    }

    public boolean contains_show_stores(Byte value) {
        return containsValue("show_stores", value);
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

    public void add_country(String value) {
        setValue("country", value);
    }

    public List<String> getAll_country() {
        return getStringValues("country");
    }

    public void remove_country(String value) {
        removeEqualValue("country", value);
    }

    public boolean contains_country(String value) {
        return containsValue("country", value);
    }

}
