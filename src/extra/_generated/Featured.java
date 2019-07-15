
package extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.io.File;

public class Featured
    extends Item
{

    public final static String _NAME = "featured";
    public final static String NAME = "name";
    public final static String LINK = "link";
    public final static String CAT_LINK = "cat_link";
    public final static String DATE = "date";
    public final static String TAG = "tag";
    public final static String STYLE = "style";
    public final static String MAIN_PIC = "main_pic";

    private Featured(Item item) {
        super(item);
    }

    public static Featured get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'featured' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Featured(item);
    }

    public static Featured newChild(Item parent) {
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

    public void set_link(String value) {
        setValue("link", value);
    }

    public String get_link() {
        return getStringValue("link");
    }

    public String getDefault_link(String defaultVal) {
        return getStringValue("link", defaultVal);
    }

    public boolean contains_link(String value) {
        return containsValue("link", value);
    }

    public void set_cat_link(String value) {
        setValue("cat_link", value);
    }

    public String get_cat_link() {
        return getStringValue("cat_link");
    }

    public String getDefault_cat_link(String defaultVal) {
        return getStringValue("cat_link", defaultVal);
    }

    public boolean contains_cat_link(String value) {
        return containsValue("cat_link", value);
    }

    public void set_date(Long value) {
        setValue("date", value);
    }

    public void setUI_date(String value)
        throws Exception
    {
        setValueUI("date", value);
    }

    public Long get_date() {
        return getLongValue("date");
    }

    public Long getDefault_date(Long defaultVal) {
        return getLongValue("date", defaultVal);
    }

    public boolean contains_date(Long value) {
        return containsValue("date", value);
    }

    public void set_tag(String value) {
        setValue("tag", value);
    }

    public String get_tag() {
        return getStringValue("tag");
    }

    public String getDefault_tag(String defaultVal) {
        return getStringValue("tag", defaultVal);
    }

    public boolean contains_tag(String value) {
        return containsValue("tag", value);
    }

    public void set_style(String value) {
        setValue("style", value);
    }

    public String get_style() {
        return getStringValue("style");
    }

    public String getDefault_style(String defaultVal) {
        return getStringValue("style", defaultVal);
    }

    public boolean contains_style(String value) {
        return containsValue("style", value);
    }

    public void set_main_pic(File value) {
        setValue("main_pic", value);
    }

    public File get_main_pic() {
        return getFileValue("main_pic", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_main_pic(File value) {
        return containsValue("main_pic", value);
    }

}
