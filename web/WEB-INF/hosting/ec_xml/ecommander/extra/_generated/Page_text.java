
package ecommander.extra._generated;

import java.io.File;
import java.util.List;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Page_text
    extends Item
{

    public final static String _NAME = "page_text";
    public final static String NAME = "name";
    public final static String SPOILER = "spoiler";
    public final static String TEXT = "text";
    public final static String TEXT_PIC = "text_pic";

    private Page_text(Item item) {
        super(item);
    }

    public static Page_text get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'page_text' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Page_text(item);
    }

    public static Page_text newChild(Item parent) {
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

    public void set_spoiler(Byte value) {
        setValue("spoiler", value);
    }

    public void setUI_spoiler(String value)
        throws Exception
    {
        setValueUI("spoiler", value);
    }

    public Byte get_spoiler() {
        return getByteValue("spoiler");
    }

    public Byte getDefault_spoiler(Byte defaultVal) {
        return getByteValue("spoiler", defaultVal);
    }

    public boolean contains_spoiler(Byte value) {
        return containsValue("spoiler", value);
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

    public void add_text_pic(File value) {
        setValue("text_pic", value);
    }

    public List<File> getAll_text_pic() {
        return getFileValues("text_pic", AppContext.getCommonFilesDirPath());
    }

    public void remove_text_pic(File value) {
        removeEqualValue("text_pic", value);
    }

    public boolean contains_text_pic(File value) {
        return containsValue("text_pic", value);
    }

}
