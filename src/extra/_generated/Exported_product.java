
package extra._generated;

import java.math.BigDecimal;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Exported_product
    extends Item
{

    public final static String _NAME = "exported_product";
    public final static String URL = "url";
    public final static String CURRENCY_ID = "currency_id";
    public final static String PRICE_ORIGINAL = "price_original";

    private Exported_product(Item item) {
        super(item);
    }

    public static Exported_product get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'exported_product' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Exported_product(item);
    }

    public static Exported_product newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_url(String value) {
        setValue("url", value);
    }

    public String get_url() {
        return getStringValue("url");
    }

    public String getDefault_url(String defaultVal) {
        return getStringValue("url", defaultVal);
    }

    public boolean contains_url(String value) {
        return containsValue("url", value);
    }

    public void set_currency_id(String value) {
        setValue("currency_id", value);
    }

    public String get_currency_id() {
        return getStringValue("currency_id");
    }

    public String getDefault_currency_id(String defaultVal) {
        return getStringValue("currency_id", defaultVal);
    }

    public boolean contains_currency_id(String value) {
        return containsValue("currency_id", value);
    }

    public void set_price_original(BigDecimal value) {
        setValue("price_original", value);
    }

    public void setUI_price_original(String value)
        throws Exception
    {
        setValueUI("price_original", value);
    }

    public BigDecimal get_price_original() {
        return getDecimalValue("price_original");
    }

    public BigDecimal getDefault_price_original(BigDecimal defaultVal) {
        return getDecimalValue("price_original", defaultVal);
    }

    public boolean contains_price_original(BigDecimal value) {
        return containsValue("price_original", value);
    }

}
