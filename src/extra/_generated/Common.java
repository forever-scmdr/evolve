
package extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Common
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "common";

    private Common(Item item) {
        super(item);
    }

    public static Common get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'common' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Common(item);
    }

    public static Common newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_top(String value) {
        setValue("top", value);
    }

    public String get_top() {
        return getStringValue("top");
    }

    public String getDefault_top(String defaultVal) {
        return getStringValue("top", defaultVal);
    }

    public boolean contains_top(String value) {
        return containsValue("top", value);
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

    public void set_bottom(String value) {
        setValue("bottom", value);
    }

    public String get_bottom() {
        return getStringValue("bottom");
    }

    public String getDefault_bottom(String defaultVal) {
        return getStringValue("bottom", defaultVal);
    }

    public boolean contains_bottom(String value) {
        return containsValue("bottom", value);
    }

    public void set_bottom_address(String value) {
        setValue("bottom_address", value);
    }

    public String get_bottom_address() {
        return getStringValue("bottom_address");
    }

    public String getDefault_bottom_address(String defaultVal) {
        return getStringValue("bottom_address", defaultVal);
    }

    public boolean contains_bottom_address(String value) {
        return containsValue("bottom_address", value);
    }

    public void set_bottom_copy(String value) {
        setValue("bottom_copy", value);
    }

    public String get_bottom_copy() {
        return getStringValue("bottom_copy");
    }

    public String getDefault_bottom_copy(String defaultVal) {
        return getStringValue("bottom_copy", defaultVal);
    }

    public boolean contains_bottom_copy(String value) {
        return containsValue("bottom_copy", value);
    }

    public void set_bottom_cards(String value) {
        setValue("bottom_cards", value);
    }

    public String get_bottom_cards() {
        return getStringValue("bottom_cards");
    }

    public String getDefault_bottom_cards(String defaultVal) {
        return getStringValue("bottom_cards", defaultVal);
    }

    public boolean contains_bottom_cards(String value) {
        return containsValue("bottom_cards", value);
    }

    public void set_bottom_social(String value) {
        setValue("bottom_social", value);
    }

    public String get_bottom_social() {
        return getStringValue("bottom_social");
    }

    public String getDefault_bottom_social(String defaultVal) {
        return getStringValue("bottom_social", defaultVal);
    }

    public boolean contains_bottom_social(String value) {
        return containsValue("bottom_social", value);
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
