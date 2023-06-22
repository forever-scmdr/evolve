
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Device_info
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "device_info";

    private Device_info(Item item) {
        super(item);
    }

    public static Device_info get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'device_info' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Device_info(item);
    }

    public static Device_info newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_type(String value) {
        setValue("type", value);
    }

    public String get_type() {
        return getStringValue("type");
    }

    public String getDefault_type(String defaultVal) {
        return getStringValue("type", defaultVal);
    }

    public boolean contains_type(String value) {
        return containsValue("type", value);
    }

    public void set_num(String value) {
        setValue("num", value);
    }

    public String get_num() {
        return getStringValue("num");
    }

    public String getDefault_num(String defaultVal) {
        return getStringValue("num", defaultVal);
    }

    public boolean contains_num(String value) {
        return containsValue("num", value);
    }

    public void set_ab_num(String value) {
        setValue("ab_num", value);
    }

    public String get_ab_num() {
        return getStringValue("ab_num");
    }

    public String getDefault_ab_num(String defaultVal) {
        return getStringValue("ab_num", defaultVal);
    }

    public boolean contains_ab_num(String value) {
        return containsValue("ab_num", value);
    }

    public void set_address(String value) {
        setValue("address", value);
    }

    public String get_address() {
        return getStringValue("address");
    }

    public String getDefault_address(String defaultVal) {
        return getStringValue("address", defaultVal);
    }

    public boolean contains_address(String value) {
        return containsValue("address", value);
    }

    public void set_organization(String value) {
        setValue("organization", value);
    }

    public String get_organization() {
        return getStringValue("organization");
    }

    public String getDefault_organization(String defaultVal) {
        return getStringValue("organization", defaultVal);
    }

    public boolean contains_organization(String value) {
        return containsValue("organization", value);
    }

    public void set_contact(String value) {
        setValue("contact", value);
    }

    public String get_contact() {
        return getStringValue("contact");
    }

    public String getDefault_contact(String defaultVal) {
        return getStringValue("contact", defaultVal);
    }

    public boolean contains_contact(String value) {
        return containsValue("contact", value);
    }

    public void set_position(String value) {
        setValue("position", value);
    }

    public String get_position() {
        return getStringValue("position");
    }

    public String getDefault_position(String defaultVal) {
        return getStringValue("position", defaultVal);
    }

    public boolean contains_position(String value) {
        return containsValue("position", value);
    }

    public void set_country(String value) {
        setValue("country", value);
    }

    public String get_country() {
        return getStringValue("country");
    }

    public String getDefault_country(String defaultVal) {
        return getStringValue("country", defaultVal);
    }

    public boolean contains_country(String value) {
        return containsValue("country", value);
    }

    public void set_city(String value) {
        setValue("city", value);
    }

    public String get_city() {
        return getStringValue("city");
    }

    public String getDefault_city(String defaultVal) {
        return getStringValue("city", defaultVal);
    }

    public boolean contains_city(String value) {
        return containsValue("city", value);
    }

    public void set_street(String value) {
        setValue("street", value);
    }

    public String get_street() {
        return getStringValue("street");
    }

    public String getDefault_street(String defaultVal) {
        return getStringValue("street", defaultVal);
    }

    public boolean contains_street(String value) {
        return containsValue("street", value);
    }

    public void set_number(String value) {
        setValue("number", value);
    }

    public String get_number() {
        return getStringValue("number");
    }

    public String getDefault_number(String defaultVal) {
        return getStringValue("number", defaultVal);
    }

    public boolean contains_number(String value) {
        return containsValue("number", value);
    }

    public void set_coordinates(String value) {
        setValue("coordinates", value);
    }

    public String get_coordinates() {
        return getStringValue("coordinates");
    }

    public String getDefault_coordinates(String defaultVal) {
        return getStringValue("coordinates", defaultVal);
    }

    public boolean contains_coordinates(String value) {
        return containsValue("coordinates", value);
    }

    public void set_report_start(String value) {
        setValue("report_start", value);
    }

    public String get_report_start() {
        return getStringValue("report_start");
    }

    public String getDefault_report_start(String defaultVal) {
        return getStringValue("report_start", defaultVal);
    }

    public boolean contains_report_start(String value) {
        return containsValue("report_start", value);
    }

    public void set_build_walls(String value) {
        setValue("build_walls", value);
    }

    public String get_build_walls() {
        return getStringValue("build_walls");
    }

    public String getDefault_build_walls(String defaultVal) {
        return getStringValue("build_walls", defaultVal);
    }

    public boolean contains_build_walls(String value) {
        return containsValue("build_walls", value);
    }

    public void set_build_type(String value) {
        setValue("build_type", value);
    }

    public String get_build_type() {
        return getStringValue("build_type");
    }

    public String getDefault_build_type(String defaultVal) {
        return getStringValue("build_type", defaultVal);
    }

    public boolean contains_build_type(String value) {
        return containsValue("build_type", value);
    }

    public void set_build_stages(String value) {
        setValue("build_stages", value);
    }

    public String get_build_stages() {
        return getStringValue("build_stages");
    }

    public String getDefault_build_stages(String defaultVal) {
        return getStringValue("build_stages", defaultVal);
    }

    public boolean contains_build_stages(String value) {
        return containsValue("build_stages", value);
    }

    public void set_build_entrances(String value) {
        setValue("build_entrances", value);
    }

    public String get_build_entrances() {
        return getStringValue("build_entrances");
    }

    public String getDefault_build_entrances(String defaultVal) {
        return getStringValue("build_entrances", defaultVal);
    }

    public boolean contains_build_entrances(String value) {
        return containsValue("build_entrances", value);
    }

    public void set_ceiling_height(String value) {
        setValue("ceiling_height", value);
    }

    public String get_ceiling_height() {
        return getStringValue("ceiling_height");
    }

    public String getDefault_ceiling_height(String defaultVal) {
        return getStringValue("ceiling_height", defaultVal);
    }

    public boolean contains_ceiling_height(String value) {
        return containsValue("ceiling_height", value);
    }

    public void set_total_space(String value) {
        setValue("total_space", value);
    }

    public String get_total_space() {
        return getStringValue("total_space");
    }

    public String getDefault_total_space(String defaultVal) {
        return getStringValue("total_space", defaultVal);
    }

    public boolean contains_total_space(String value) {
        return containsValue("total_space", value);
    }

    public void set_total_heat_space(String value) {
        setValue("total_heat_space", value);
    }

    public String get_total_heat_space() {
        return getStringValue("total_heat_space");
    }

    public String getDefault_total_heat_space(String defaultVal) {
        return getStringValue("total_heat_space", defaultVal);
    }

    public boolean contains_total_heat_space(String value) {
        return containsValue("total_heat_space", value);
    }

    public void set_unheat_surface(String value) {
        setValue("unheat_surface", value);
    }

    public String get_unheat_surface() {
        return getStringValue("unheat_surface");
    }

    public String getDefault_unheat_surface(String defaultVal) {
        return getStringValue("unheat_surface", defaultVal);
    }

    public boolean contains_unheat_surface(String value) {
        return containsValue("unheat_surface", value);
    }

    public void set_total_volume(String value) {
        setValue("total_volume", value);
    }

    public String get_total_volume() {
        return getStringValue("total_volume");
    }

    public String getDefault_total_volume(String defaultVal) {
        return getStringValue("total_volume", defaultVal);
    }

    public boolean contains_total_volume(String value) {
        return containsValue("total_volume", value);
    }

    public void set_heat_volume(String value) {
        setValue("heat_volume", value);
    }

    public String get_heat_volume() {
        return getStringValue("heat_volume");
    }

    public String getDefault_heat_volume(String defaultVal) {
        return getStringValue("heat_volume", defaultVal);
    }

    public boolean contains_heat_volume(String value) {
        return containsValue("heat_volume", value);
    }

    public void set_unheat_volume(String value) {
        setValue("unheat_volume", value);
    }

    public String get_unheat_volume() {
        return getStringValue("unheat_volume");
    }

    public String getDefault_unheat_volume(String defaultVal) {
        return getStringValue("unheat_volume", defaultVal);
    }

    public boolean contains_unheat_volume(String value) {
        return containsValue("unheat_volume", value);
    }

    public void set_avg_temp(String value) {
        setValue("avg_temp", value);
    }

    public String get_avg_temp() {
        return getStringValue("avg_temp");
    }

    public String getDefault_avg_temp(String defaultVal) {
        return getStringValue("avg_temp", defaultVal);
    }

    public boolean contains_avg_temp(String value) {
        return containsValue("avg_temp", value);
    }

    public void set_heat_isol_type(String value) {
        setValue("heat_isol_type", value);
    }

    public String get_heat_isol_type() {
        return getStringValue("heat_isol_type");
    }

    public String getDefault_heat_isol_type(String defaultVal) {
        return getStringValue("heat_isol_type", defaultVal);
    }

    public boolean contains_heat_isol_type(String value) {
        return containsValue("heat_isol_type", value);
    }

    public void set_radiator_type(String value) {
        setValue("radiator_type", value);
    }

    public String get_radiator_type() {
        return getStringValue("radiator_type");
    }

    public String getDefault_radiator_type(String defaultVal) {
        return getStringValue("radiator_type", defaultVal);
    }

    public boolean contains_radiator_type(String value) {
        return containsValue("radiator_type", value);
    }

    public void set_window_type(String value) {
        setValue("window_type", value);
    }

    public String get_window_type() {
        return getStringValue("window_type");
    }

    public String getDefault_window_type(String defaultVal) {
        return getStringValue("window_type", defaultVal);
    }

    public boolean contains_window_type(String value) {
        return containsValue("window_type", value);
    }

    public void set_door_closer(String value) {
        setValue("door_closer", value);
    }

    public String get_door_closer() {
        return getStringValue("door_closer");
    }

    public String getDefault_door_closer(String defaultVal) {
        return getStringValue("door_closer", defaultVal);
    }

    public boolean contains_door_closer(String value) {
        return containsValue("door_closer", value);
    }

    public void set_build_year(String value) {
        setValue("build_year", value);
    }

    public String get_build_year() {
        return getStringValue("build_year");
    }

    public String getDefault_build_year(String defaultVal) {
        return getStringValue("build_year", defaultVal);
    }

    public boolean contains_build_year(String value) {
        return containsValue("build_year", value);
    }

    public void set_repair_year(String value) {
        setValue("repair_year", value);
    }

    public String get_repair_year() {
        return getStringValue("repair_year");
    }

    public String getDefault_repair_year(String defaultVal) {
        return getStringValue("repair_year", defaultVal);
    }

    public boolean contains_repair_year(String value) {
        return containsValue("repair_year", value);
    }

}
