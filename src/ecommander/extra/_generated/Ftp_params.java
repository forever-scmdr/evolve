
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Ftp_params
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "ftp_params";

    private Ftp_params(Item item) {
        super(item);
    }

    public static Ftp_params get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'ftp_params' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Ftp_params(item);
    }

    public static Ftp_params newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_server(String value) {
        setValue("server", value);
    }

    public String get_server() {
        return getStringValue("server");
    }

    public String getDefault_server(String defaultVal) {
        return getStringValue("server", defaultVal);
    }

    public boolean contains_server(String value) {
        return containsValue("server", value);
    }

    public void set_port(Integer value) {
        setValue("port", value);
    }

    public void setUI_port(String value)
        throws Exception
    {
        setValueUI("port", value);
    }

    public Integer get_port() {
        return getIntValue("port");
    }

    public Integer getDefault_port(Integer defaultVal) {
        return getIntValue("port", defaultVal);
    }

    public boolean contains_port(Integer value) {
        return containsValue("port", value);
    }

    public void set_login(String value) {
        setValue("login", value);
    }

    public String get_login() {
        return getStringValue("login");
    }

    public String getDefault_login(String defaultVal) {
        return getStringValue("login", defaultVal);
    }

    public boolean contains_login(String value) {
        return containsValue("login", value);
    }

    public void set_password(String value) {
        setValue("password", value);
    }

    public String get_password() {
        return getStringValue("password");
    }

    public String getDefault_password(String defaultVal) {
        return getStringValue("password", defaultVal);
    }

    public boolean contains_password(String value) {
        return containsValue("password", value);
    }

    public void set_local_path(String value) {
        setValue("local_path", value);
    }

    public String get_local_path() {
        return getStringValue("local_path");
    }

    public String getDefault_local_path(String defaultVal) {
        return getStringValue("local_path", defaultVal);
    }

    public boolean contains_local_path(String value) {
        return containsValue("local_path", value);
    }

}
