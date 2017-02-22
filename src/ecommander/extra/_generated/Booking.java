
package ecommander.extra._generated;

import java.io.File;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;

public class Booking
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "booking";

    private Booking(Item item) {
        super(item);
    }

    public static Booking get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'booking' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Booking(item);
    }

    public static Booking newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_contract_template(File value) {
        setValue("contract_template", value);
    }

    public File get_contract_template() {
        return getFileValue("contract_template", AppContext.getFilesDirPath());
    }

    public boolean contains_contract_template(File value) {
        return containsValue("contract_template", value);
    }

    public void set_bill_bel(File value) {
        setValue("bill_bel", value);
    }

    public File get_bill_bel() {
        return getFileValue("bill_bel", AppContext.getFilesDirPath());
    }

    public boolean contains_bill_bel(File value) {
        return containsValue("bill_bel", value);
    }

    public void set_bill_rus(File value) {
        setValue("bill_rus", value);
    }

    public File get_bill_rus() {
        return getFileValue("bill_rus", AppContext.getFilesDirPath());
    }

    public boolean contains_bill_rus(File value) {
        return containsValue("bill_rus", value);
    }

    public void set_bill_eur(File value) {
        setValue("bill_eur", value);
    }

    public File get_bill_eur() {
        return getFileValue("bill_eur", AppContext.getFilesDirPath());
    }

    public boolean contains_bill_eur(File value) {
        return containsValue("bill_eur", value);
    }

    public void set_restrictions(String value) {
        setValue("restrictions", value);
    }

    public String get_restrictions() {
        return getStringValue("restrictions");
    }

    public String getDefault_restrictions(String defaultVal) {
        return getStringValue("restrictions", defaultVal);
    }

    public boolean contains_restrictions(String value) {
        return containsValue("restrictions", value);
    }

    public void set_booking(String value) {
        setValue("booking", value);
    }

    public String get_booking() {
        return getStringValue("booking");
    }

    public String getDefault_booking(String defaultVal) {
        return getStringValue("booking", defaultVal);
    }

    public boolean contains_booking(String value) {
        return containsValue("booking", value);
    }

}
