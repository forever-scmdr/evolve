
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Room
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "room";

    private Room(Item item) {
        super(item);
    }

    public static Room get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'room' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Room(item);
    }

    public static Room newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
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

    public void set_base_beds(Integer value) {
        setValue("base_beds", value);
    }

    public void setUI_base_beds(String value)
        throws Exception
    {
        setValueUI("base_beds", value);
    }

    public Integer get_base_beds() {
        return getIntValue("base_beds");
    }

    public Integer getDefault_base_beds(Integer defaultVal) {
        return getIntValue("base_beds", defaultVal);
    }

    public boolean contains_base_beds(Integer value) {
        return containsValue("base_beds", value);
    }

    public void set_extra_beds(Integer value) {
        setValue("extra_beds", value);
    }

    public void setUI_extra_beds(String value)
        throws Exception
    {
        setValueUI("extra_beds", value);
    }

    public Integer get_extra_beds() {
        return getIntValue("extra_beds");
    }

    public Integer getDefault_extra_beds(Integer defaultVal) {
        return getIntValue("extra_beds", defaultVal);
    }

    public boolean contains_extra_beds(Integer value) {
        return containsValue("extra_beds", value);
    }

    public void set_top_text(String value) {
        setValue("top_text", value);
    }

    public String get_top_text() {
        return getStringValue("top_text");
    }

    public String getDefault_top_text(String defaultVal) {
        return getStringValue("top_text", defaultVal);
    }

    public boolean contains_top_text(String value) {
        return containsValue("top_text", value);
    }

    public void set_side_table(String value) {
        setValue("side_table", value);
    }

    public String get_side_table() {
        return getStringValue("side_table");
    }

    public String getDefault_side_table(String defaultVal) {
        return getStringValue("side_table", defaultVal);
    }

    public boolean contains_side_table(String value) {
        return containsValue("side_table", value);
    }

    public void set_price_san_bel_first(Double value) {
        setValue("price_san_bel_first", value);
    }

    public void setUI_price_san_bel_first(String value)
        throws Exception
    {
        setValueUI("price_san_bel_first", value);
    }

    public Double get_price_san_bel_first() {
        return getDoubleValue("price_san_bel_first");
    }

    public Double getDefault_price_san_bel_first(Double defaultVal) {
        return getDoubleValue("price_san_bel_first", defaultVal);
    }

    public boolean contains_price_san_bel_first(Double value) {
        return containsValue("price_san_bel_first", value);
    }

    public void set_price_san_bel_second(Double value) {
        setValue("price_san_bel_second", value);
    }

    public void setUI_price_san_bel_second(String value)
        throws Exception
    {
        setValueUI("price_san_bel_second", value);
    }

    public Double get_price_san_bel_second() {
        return getDoubleValue("price_san_bel_second");
    }

    public Double getDefault_price_san_bel_second(Double defaultVal) {
        return getDoubleValue("price_san_bel_second", defaultVal);
    }

    public boolean contains_price_san_bel_second(Double value) {
        return containsValue("price_san_bel_second", value);
    }

    public void set_price_san_bel_third(Double value) {
        setValue("price_san_bel_third", value);
    }

    public void setUI_price_san_bel_third(String value)
        throws Exception
    {
        setValueUI("price_san_bel_third", value);
    }

    public Double get_price_san_bel_third() {
        return getDoubleValue("price_san_bel_third");
    }

    public Double getDefault_price_san_bel_third(Double defaultVal) {
        return getDoubleValue("price_san_bel_third", defaultVal);
    }

    public boolean contains_price_san_bel_third(Double value) {
        return containsValue("price_san_bel_third", value);
    }

    public void set_price_ozd_bel_first(Double value) {
        setValue("price_ozd_bel_first", value);
    }

    public void setUI_price_ozd_bel_first(String value)
        throws Exception
    {
        setValueUI("price_ozd_bel_first", value);
    }

    public Double get_price_ozd_bel_first() {
        return getDoubleValue("price_ozd_bel_first");
    }

    public Double getDefault_price_ozd_bel_first(Double defaultVal) {
        return getDoubleValue("price_ozd_bel_first", defaultVal);
    }

    public boolean contains_price_ozd_bel_first(Double value) {
        return containsValue("price_ozd_bel_first", value);
    }

    public void set_price_ozd_bel_second(Double value) {
        setValue("price_ozd_bel_second", value);
    }

    public void setUI_price_ozd_bel_second(String value)
        throws Exception
    {
        setValueUI("price_ozd_bel_second", value);
    }

    public Double get_price_ozd_bel_second() {
        return getDoubleValue("price_ozd_bel_second");
    }

    public Double getDefault_price_ozd_bel_second(Double defaultVal) {
        return getDoubleValue("price_ozd_bel_second", defaultVal);
    }

    public boolean contains_price_ozd_bel_second(Double value) {
        return containsValue("price_ozd_bel_second", value);
    }

    public void set_price_ozd_bel_third(Double value) {
        setValue("price_ozd_bel_third", value);
    }

    public void setUI_price_ozd_bel_third(String value)
        throws Exception
    {
        setValueUI("price_ozd_bel_third", value);
    }

    public Double get_price_ozd_bel_third() {
        return getDoubleValue("price_ozd_bel_third");
    }

    public Double getDefault_price_ozd_bel_third(Double defaultVal) {
        return getDoubleValue("price_ozd_bel_third", defaultVal);
    }

    public boolean contains_price_ozd_bel_third(Double value) {
        return containsValue("price_ozd_bel_third", value);
    }

    public void set_price_ozd_rus_first(Double value) {
        setValue("price_ozd_rus_first", value);
    }

    public void setUI_price_ozd_rus_first(String value)
        throws Exception
    {
        setValueUI("price_ozd_rus_first", value);
    }

    public Double get_price_ozd_rus_first() {
        return getDoubleValue("price_ozd_rus_first");
    }

    public Double getDefault_price_ozd_rus_first(Double defaultVal) {
        return getDoubleValue("price_ozd_rus_first", defaultVal);
    }

    public boolean contains_price_ozd_rus_first(Double value) {
        return containsValue("price_ozd_rus_first", value);
    }

    public void set_price_ozd_rus_second(Double value) {
        setValue("price_ozd_rus_second", value);
    }

    public void setUI_price_ozd_rus_second(String value)
        throws Exception
    {
        setValueUI("price_ozd_rus_second", value);
    }

    public Double get_price_ozd_rus_second() {
        return getDoubleValue("price_ozd_rus_second");
    }

    public Double getDefault_price_ozd_rus_second(Double defaultVal) {
        return getDoubleValue("price_ozd_rus_second", defaultVal);
    }

    public boolean contains_price_ozd_rus_second(Double value) {
        return containsValue("price_ozd_rus_second", value);
    }

    public void set_price_ozd_rus_third(Double value) {
        setValue("price_ozd_rus_third", value);
    }

    public void setUI_price_ozd_rus_third(String value)
        throws Exception
    {
        setValueUI("price_ozd_rus_third", value);
    }

    public Double get_price_ozd_rus_third() {
        return getDoubleValue("price_ozd_rus_third");
    }

    public Double getDefault_price_ozd_rus_third(Double defaultVal) {
        return getDoubleValue("price_ozd_rus_third", defaultVal);
    }

    public boolean contains_price_ozd_rus_third(Double value) {
        return containsValue("price_ozd_rus_third", value);
    }

    public void set_price_san_rus_first(Double value) {
        setValue("price_san_rus_first", value);
    }

    public void setUI_price_san_rus_first(String value)
        throws Exception
    {
        setValueUI("price_san_rus_first", value);
    }

    public Double get_price_san_rus_first() {
        return getDoubleValue("price_san_rus_first");
    }

    public Double getDefault_price_san_rus_first(Double defaultVal) {
        return getDoubleValue("price_san_rus_first", defaultVal);
    }

    public boolean contains_price_san_rus_first(Double value) {
        return containsValue("price_san_rus_first", value);
    }

    public void set_price_san_rus_second(Double value) {
        setValue("price_san_rus_second", value);
    }

    public void setUI_price_san_rus_second(String value)
        throws Exception
    {
        setValueUI("price_san_rus_second", value);
    }

    public Double get_price_san_rus_second() {
        return getDoubleValue("price_san_rus_second");
    }

    public Double getDefault_price_san_rus_second(Double defaultVal) {
        return getDoubleValue("price_san_rus_second", defaultVal);
    }

    public boolean contains_price_san_rus_second(Double value) {
        return containsValue("price_san_rus_second", value);
    }

    public void set_price_san_rus_third(Double value) {
        setValue("price_san_rus_third", value);
    }

    public void setUI_price_san_rus_third(String value)
        throws Exception
    {
        setValueUI("price_san_rus_third", value);
    }

    public Double get_price_san_rus_third() {
        return getDoubleValue("price_san_rus_third");
    }

    public Double getDefault_price_san_rus_third(Double defaultVal) {
        return getDoubleValue("price_san_rus_third", defaultVal);
    }

    public boolean contains_price_san_rus_third(Double value) {
        return containsValue("price_san_rus_third", value);
    }

    public void set_price_ozd_eur_first(Double value) {
        setValue("price_ozd_eur_first", value);
    }

    public void setUI_price_ozd_eur_first(String value)
        throws Exception
    {
        setValueUI("price_ozd_eur_first", value);
    }

    public Double get_price_ozd_eur_first() {
        return getDoubleValue("price_ozd_eur_first");
    }

    public Double getDefault_price_ozd_eur_first(Double defaultVal) {
        return getDoubleValue("price_ozd_eur_first", defaultVal);
    }

    public boolean contains_price_ozd_eur_first(Double value) {
        return containsValue("price_ozd_eur_first", value);
    }

    public void set_price_ozd_eur_second(Double value) {
        setValue("price_ozd_eur_second", value);
    }

    public void setUI_price_ozd_eur_second(String value)
        throws Exception
    {
        setValueUI("price_ozd_eur_second", value);
    }

    public Double get_price_ozd_eur_second() {
        return getDoubleValue("price_ozd_eur_second");
    }

    public Double getDefault_price_ozd_eur_second(Double defaultVal) {
        return getDoubleValue("price_ozd_eur_second", defaultVal);
    }

    public boolean contains_price_ozd_eur_second(Double value) {
        return containsValue("price_ozd_eur_second", value);
    }

    public void set_price_ozd_eur_third(Double value) {
        setValue("price_ozd_eur_third", value);
    }

    public void setUI_price_ozd_eur_third(String value)
        throws Exception
    {
        setValueUI("price_ozd_eur_third", value);
    }

    public Double get_price_ozd_eur_third() {
        return getDoubleValue("price_ozd_eur_third");
    }

    public Double getDefault_price_ozd_eur_third(Double defaultVal) {
        return getDoubleValue("price_ozd_eur_third", defaultVal);
    }

    public boolean contains_price_ozd_eur_third(Double value) {
        return containsValue("price_ozd_eur_third", value);
    }

    public void set_price_san_eur_first(Double value) {
        setValue("price_san_eur_first", value);
    }

    public void setUI_price_san_eur_first(String value)
        throws Exception
    {
        setValueUI("price_san_eur_first", value);
    }

    public Double get_price_san_eur_first() {
        return getDoubleValue("price_san_eur_first");
    }

    public Double getDefault_price_san_eur_first(Double defaultVal) {
        return getDoubleValue("price_san_eur_first", defaultVal);
    }

    public boolean contains_price_san_eur_first(Double value) {
        return containsValue("price_san_eur_first", value);
    }

    public void set_price_san_eur_second(Double value) {
        setValue("price_san_eur_second", value);
    }

    public void setUI_price_san_eur_second(String value)
        throws Exception
    {
        setValueUI("price_san_eur_second", value);
    }

    public Double get_price_san_eur_second() {
        return getDoubleValue("price_san_eur_second");
    }

    public Double getDefault_price_san_eur_second(Double defaultVal) {
        return getDoubleValue("price_san_eur_second", defaultVal);
    }

    public boolean contains_price_san_eur_second(Double value) {
        return containsValue("price_san_eur_second", value);
    }

    public void set_price_san_eur_third(Double value) {
        setValue("price_san_eur_third", value);
    }

    public void setUI_price_san_eur_third(String value)
        throws Exception
    {
        setValueUI("price_san_eur_third", value);
    }

    public Double get_price_san_eur_third() {
        return getDoubleValue("price_san_eur_third");
    }

    public Double getDefault_price_san_eur_third(Double defaultVal) {
        return getDoubleValue("price_san_eur_third", defaultVal);
    }

    public boolean contains_price_san_eur_third(Double value) {
        return containsValue("price_san_eur_third", value);
    }

}
