
package lunacrawler._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Url_seo_wrap
    extends Item
{

    public final static String _NAME = "url_seo_wrap";
    public final static String MAIN_HOST = "main_host";

    private Url_seo_wrap(Item item) {
        super(item);
    }

    public static Url_seo_wrap get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'url_seo_wrap' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Url_seo_wrap(item);
    }

    public static Url_seo_wrap newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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
