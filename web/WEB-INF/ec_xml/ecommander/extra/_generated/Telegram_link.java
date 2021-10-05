
package ecommander.extra._generated;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Telegram_link
    extends Item
{

    public final static String _NAME = "telegram_link";
    public final static String TEXT = "text";
    public final static String STYLE = "style";
    public final static String START = "start";
    public final static String FINISH = "finish";

    private Telegram_link(Item item) {
        super(item);
    }

    public static Telegram_link get(Item item) {
        if (item == null) {
            return null;
        }
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'telegram_link' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Telegram_link(item);
    }

    public static Telegram_link newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_NAME), parent));
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

    public void set_start(Long value) {
        setValue("start", value);
    }

    public void setUI_start(String value)
        throws Exception
    {
        setValueUI("start", value);
    }

    public Long get_start() {
        return getLongValue("start");
    }

    public Long getDefault_start(Long defaultVal) {
        return getLongValue("start", defaultVal);
    }

    public boolean contains_start(Long value) {
        return containsValue("start", value);
    }

    public void set_finish(Long value) {
        setValue("finish", value);
    }

    public void setUI_finish(String value)
        throws Exception
    {
        setValueUI("finish", value);
    }

    public Long get_finish() {
        return getLongValue("finish");
    }

    public Long getDefault_finish(Long defaultVal) {
        return getLongValue("finish", defaultVal);
    }

    public boolean contains_finish(Long value) {
        return containsValue("finish", value);
    }

}
