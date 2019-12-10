
package ecommander.extra._generated;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

import java.io.File;
import java.util.List;

public class Parse_item
    extends Item
{

    public final static String _NAME = "parse_item";
    public final static String URL = "url";
    public final static String DOWNLOADED = "downloaded";
    public final static String PARSED = "parsed";
    public final static String GOT_FILES = "got_files";
    public final static String DUPLICATED = "duplicated";
    public final static String HTML = "html";
    public final static String XML = "xml";
    public final static String TEST_URL = "test_url";
    public final static String FILE = "file";
    public final static String HTML_PIC = "html_pic";

    private Parse_item(Item item) {
        super(item);
    }

    public static Parse_item get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'parse_item' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Parse_item(item);
    }

    public static Parse_item newChild(Item parent) {
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

    public void set_downloaded(Byte value) {
        setValue("downloaded", value);
    }

    public void setUI_downloaded(String value)
        throws Exception
    {
        setValueUI("downloaded", value);
    }

    public Byte get_downloaded() {
        return getByteValue("downloaded");
    }

    public Byte getDefault_downloaded(Byte defaultVal) {
        return getByteValue("downloaded", defaultVal);
    }

    public boolean contains_downloaded(Byte value) {
        return containsValue("downloaded", value);
    }

    public void set_parsed(Byte value) {
        setValue("parsed", value);
    }

    public void setUI_parsed(String value)
        throws Exception
    {
        setValueUI("parsed", value);
    }

    public Byte get_parsed() {
        return getByteValue("parsed");
    }

    public Byte getDefault_parsed(Byte defaultVal) {
        return getByteValue("parsed", defaultVal);
    }

    public boolean contains_parsed(Byte value) {
        return containsValue("parsed", value);
    }

    public void set_got_files(Byte value) {
        setValue("got_files", value);
    }

    public void setUI_got_files(String value)
        throws Exception
    {
        setValueUI("got_files", value);
    }

    public Byte get_got_files() {
        return getByteValue("got_files");
    }

    public Byte getDefault_got_files(Byte defaultVal) {
        return getByteValue("got_files", defaultVal);
    }

    public boolean contains_got_files(Byte value) {
        return containsValue("got_files", value);
    }

    public void set_duplicated(Byte value) {
        setValue("duplicated", value);
    }

    public void setUI_duplicated(String value)
        throws Exception
    {
        setValueUI("duplicated", value);
    }

    public Byte get_duplicated() {
        return getByteValue("duplicated");
    }

    public Byte getDefault_duplicated(Byte defaultVal) {
        return getByteValue("duplicated", defaultVal);
    }

    public boolean contains_duplicated(Byte value) {
        return containsValue("duplicated", value);
    }

    public void set_html(String value) {
        setValue("html", value);
    }

    public String get_html() {
        return getStringValue("html");
    }

    public String getDefault_html(String defaultVal) {
        return getStringValue("html", defaultVal);
    }

    public boolean contains_html(String value) {
        return containsValue("html", value);
    }

    public void set_xml(String value) {
        setValue("xml", value);
    }

    public String get_xml() {
        return getStringValue("xml");
    }

    public String getDefault_xml(String defaultVal) {
        return getStringValue("xml", defaultVal);
    }

    public boolean contains_xml(String value) {
        return containsValue("xml", value);
    }

    public void set_test_url(String value) {
        setValue("test_url", value);
    }

    public String get_test_url() {
        return getStringValue("test_url");
    }

    public String getDefault_test_url(String defaultVal) {
        return getStringValue("test_url", defaultVal);
    }

    public boolean contains_test_url(String value) {
        return containsValue("test_url", value);
    }

    public void add_file(File value) {
        setValue("file", value);
    }

    public List<File> getAll_file() {
        return getFileValues("file", AppContext.getCommonFilesDirPath());
    }

    public void remove_file(File value) {
        removeEqualValue("file", value);
    }

    public boolean contains_file(File value) {
        return containsValue("file", value);
    }

    public void add_html_pic(File value) {
        setValue("html_pic", value);
    }

    public List<File> getAll_html_pic() {
        return getFileValues("html_pic", AppContext.getCommonFilesDirPath());
    }

    public void remove_html_pic(File value) {
        removeEqualValue("html_pic", value);
    }

    public boolean contains_html_pic(File value) {
        return containsValue("html_pic", value);
    }

}
