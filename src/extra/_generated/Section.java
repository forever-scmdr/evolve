
package extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Section
    extends Item
{

    public final static String _NAME = "section";
    public final static String NAME = "name";
    public final static String ICON = "icon";
    public final static String SHORT = "short";
    public final static String SHOW_SUBS = "show_subs";
    public final static String SUB_VIEW = "sub_view";
    public final static String SHOW_DEVICES = "show_devices";
    public final static String CATEGORY_ID = "category_id";
    public final static String CODE = "code";
    public final static String PARENT_ID = "parent_id";
    public final static String LIMIT_1 = "limit_1";
    public final static String LIMIT_2 = "limit_2";
    public final static String DISCOUNT_1 = "discount_1";
    public final static String DISCOUNT_2 = "discount_2";
    public final static String NORM = "norm";
    public final static String MAIN_PIC = "main_pic";
    public final static String PIC_PATH = "pic_path";
    public final static String EXTRA = "extra";
    public final static String PARAMS_SHORT = "params_short";
    public final static String PARAMS_LIST = "params_list";
    public final static String PARAMS_FILTER = "params_filter";

    private Section(Item item) {
        super(item);
    }

    public static Section get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'section' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Section(item);
    }

    public static Section newChild(Item parent) {
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

    public void set_icon(File value) {
        setValue("icon", value);
    }

    public File get_icon() {
        return getFileValue("icon", AppContext.getCommonFilesDirPath());
    }

    public boolean contains_icon(File value) {
        return containsValue("icon", value);
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

    public void set_show_subs(Byte value) {
        setValue("show_subs", value);
    }

    public void setUI_show_subs(String value)
        throws Exception
    {
        setValueUI("show_subs", value);
    }

    public Byte get_show_subs() {
        return getByteValue("show_subs");
    }

    public Byte getDefault_show_subs(Byte defaultVal) {
        return getByteValue("show_subs", defaultVal);
    }

    public boolean contains_show_subs(Byte value) {
        return containsValue("show_subs", value);
    }

    public void set_sub_view(String value) {
        setValue("sub_view", value);
    }

    public String get_sub_view() {
        return getStringValue("sub_view");
    }

    public String getDefault_sub_view(String defaultVal) {
        return getStringValue("sub_view", defaultVal);
    }

    public boolean contains_sub_view(String value) {
        return containsValue("sub_view", value);
    }

    public void set_show_devices(Byte value) {
        setValue("show_devices", value);
    }

    public void setUI_show_devices(String value)
        throws Exception
    {
        setValueUI("show_devices", value);
    }

    public Byte get_show_devices() {
        return getByteValue("show_devices");
    }

    public Byte getDefault_show_devices(Byte defaultVal) {
        return getByteValue("show_devices", defaultVal);
    }

    public boolean contains_show_devices(Byte value) {
        return containsValue("show_devices", value);
    }

    public void set_category_id(String value) {
        setValue("category_id", value);
    }

    public String get_category_id() {
        return getStringValue("category_id");
    }

    public String getDefault_category_id(String defaultVal) {
        return getStringValue("category_id", defaultVal);
    }

    public boolean contains_category_id(String value) {
        return containsValue("category_id", value);
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

    public void set_parent_id(String value) {
        setValue("parent_id", value);
    }

    public String get_parent_id() {
        return getStringValue("parent_id");
    }

    public String getDefault_parent_id(String defaultVal) {
        return getStringValue("parent_id", defaultVal);
    }

    public boolean contains_parent_id(String value) {
        return containsValue("parent_id", value);
    }

    public void set_limit_1(Double value) {
        setValue("limit_1", value);
    }

    public void setUI_limit_1(String value)
        throws Exception
    {
        setValueUI("limit_1", value);
    }

    public Double get_limit_1() {
        return getDoubleValue("limit_1");
    }

    public Double getDefault_limit_1(Double defaultVal) {
        return getDoubleValue("limit_1", defaultVal);
    }

    public boolean contains_limit_1(Double value) {
        return containsValue("limit_1", value);
    }

    public void set_limit_2(Double value) {
        setValue("limit_2", value);
    }

    public void setUI_limit_2(String value)
        throws Exception
    {
        setValueUI("limit_2", value);
    }

    public Double get_limit_2() {
        return getDoubleValue("limit_2");
    }

    public Double getDefault_limit_2(Double defaultVal) {
        return getDoubleValue("limit_2", defaultVal);
    }

    public boolean contains_limit_2(Double value) {
        return containsValue("limit_2", value);
    }

    public void set_discount_1(Integer value) {
        setValue("discount_1", value);
    }

    public void setUI_discount_1(String value)
        throws Exception
    {
        setValueUI("discount_1", value);
    }

    public Integer get_discount_1() {
        return getIntValue("discount_1");
    }

    public Integer getDefault_discount_1(Integer defaultVal) {
        return getIntValue("discount_1", defaultVal);
    }

    public boolean contains_discount_1(Integer value) {
        return containsValue("discount_1", value);
    }

    public void set_discount_2(Integer value) {
        setValue("discount_2", value);
    }

    public void setUI_discount_2(String value)
        throws Exception
    {
        setValueUI("discount_2", value);
    }

    public Integer get_discount_2() {
        return getIntValue("discount_2");
    }

    public Integer getDefault_discount_2(Integer defaultVal) {
        return getIntValue("discount_2", defaultVal);
    }

    public boolean contains_discount_2(Integer value) {
        return containsValue("discount_2", value);
    }

    public void set_norm(Double value) {
        setValue("norm", value);
    }

    public void setUI_norm(String value)
        throws Exception
    {
        setValueUI("norm", value);
    }

    public Double get_norm() {
        return getDoubleValue("norm");
    }

    public Double getDefault_norm(Double defaultVal) {
        return getDoubleValue("norm", defaultVal);
    }

    public boolean contains_norm(Double value) {
        return containsValue("norm", value);
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

    public void set_pic_path(String value) {
        setValue("pic_path", value);
    }

    public String get_pic_path() {
        return getStringValue("pic_path");
    }

    public String getDefault_pic_path(String defaultVal) {
        return getStringValue("pic_path", defaultVal);
    }

    public boolean contains_pic_path(String value) {
        return containsValue("pic_path", value);
    }

    public void set_extra(String value) {
        setValue("extra", value);
    }

    public String get_extra() {
        return getStringValue("extra");
    }

    public String getDefault_extra(String defaultVal) {
        return getStringValue("extra", defaultVal);
    }

    public boolean contains_extra(String value) {
        return containsValue("extra", value);
    }

    public void set_params_short(String value) {
        setValue("params_short", value);
    }

    public String get_params_short() {
        return getStringValue("params_short");
    }

    public String getDefault_params_short(String defaultVal) {
        return getStringValue("params_short", defaultVal);
    }

    public boolean contains_params_short(String value) {
        return containsValue("params_short", value);
    }

    public void set_params_list(String value) {
        setValue("params_list", value);
    }

    public String get_params_list() {
        return getStringValue("params_list");
    }

    public String getDefault_params_list(String defaultVal) {
        return getStringValue("params_list", defaultVal);
    }

    public boolean contains_params_list(String value) {
        return containsValue("params_list", value);
    }

}
