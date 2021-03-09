
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Common
    extends Item
{

    public final static String _NAME = "common";
    public final static String LEFT = "left";
    public final static String LINK_TEXT = "link_text";
    public final static String LINK_LINK = "link_link";
    public final static String GOOGLE_VERIFICATION = "google_verification";
    public final static String YANDEX_VERIFICATION = "yandex_verification";

    private Common(Item item) {
        super(item);
    }

    public static Common get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'common' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Common(item);
    }

    public static Common newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
    }

    public void set_left(String value) {
        setValue("left", value);
    }

    public String get_left() {
        return getStringValue("left");
    }

    public String getDefault_left(String defaultVal) {
        return getStringValue("left", defaultVal);
    }

    public boolean contains_left(String value) {
        return containsValue("left", value);
    }

    public void set_link_text(String value) {
        setValue("link_text", value);
    }

    public String get_link_text() {
        return getStringValue("link_text");
    }

    public String getDefault_link_text(String defaultVal) {
        return getStringValue("link_text", defaultVal);
    }

    public boolean contains_link_text(String value) {
        return containsValue("link_text", value);
    }

    public void set_link_link(String value) {
        setValue("link_link", value);
    }

    public String get_link_link() {
        return getStringValue("link_link");
    }

    public String getDefault_link_link(String defaultVal) {
        return getStringValue("link_link", defaultVal);
    }

    public boolean contains_link_link(String value) {
        return containsValue("link_link", value);
    }

    public void set_google_verification(String value) {
        setValue("google_verification", value);
    }

    public String get_google_verification() {
        return getStringValue("google_verification");
    }

    public String getDefault_google_verification(String defaultVal) {
        return getStringValue("google_verification", defaultVal);
    }

    public boolean contains_google_verification(String value) {
        return containsValue("google_verification", value);
    }

    public void set_yandex_verification(String value) {
        setValue("yandex_verification", value);
    }

    public String get_yandex_verification() {
        return getStringValue("yandex_verification");
    }

    public String getDefault_yandex_verification(String defaultVal) {
        return getStringValue("yandex_verification", defaultVal);
    }

    public boolean contains_yandex_verification(String value) {
        return containsValue("yandex_verification", value);
    }

}
