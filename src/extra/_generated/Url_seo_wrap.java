
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Url_seo_wrap
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "url_seo_wrap";

    private Url_seo_wrap(Item item) {
        super(item);
    }

    public static Url_seo_wrap get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'url_seo_wrap' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Url_seo_wrap(item);
    }

    public static Url_seo_wrap newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_main_host(String value) {
        setValue("main_host", value);
    }

    public String get_main_host() {
        return getStringValue("main_host");
    }

    public String getDefault_main_host(String defaultVal) {
        return getStringValue("main_host", defaultVal);
    }

    public boolean contains_main_host(String value) {
        return containsValue("main_host", value);
    }

}
