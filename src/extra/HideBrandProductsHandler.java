package extra;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class HideBrandProductsHandler implements ItemEventCommandFactory, CatalogConst {

    private static final String HIDE = "скрыть";
    private static final String SHOW = "раскрыть";
    private static final String DELETE = "удалить";

    private static class Command extends DBPersistenceCommandUnit {

        private Item brand;

        public Command(Item brand) {
            this.brand = brand;
        }

        @Override
        public void execute() throws Exception {
            String status = brand.getStringValue("change_status");
            if (!StringUtils.equalsAnyIgnoreCase(status, HIDE, SHOW, DELETE)) {
                return;
            }
            String brandMask = brand.getStringValue("mask");
            ItemQuery query;
            byte newStatus;
            if (StringUtils.equalsIgnoreCase(status, DELETE)) {
                query = new ItemQuery(PRODUCT_ITEM, (byte) 0, (byte) 1);
                newStatus = 2;
            } else if (StringUtils.equalsIgnoreCase(status, HIDE)) {
                query = new ItemQuery(PRODUCT_ITEM, (byte) 0);
                newStatus = 1;
            } else {
                query = new ItemQuery(PRODUCT_ITEM, (byte) 1);
                newStatus = 0;
            }
            query.addParameterEqualsCriteria(VENDOR_PARAM, brandMask).setLimit(50);
            List<Item> products;
            DelayedTransaction transaction = new DelayedTransaction(context.getInitiator());
            do {
                products = query.loadItems();
                for (Item product : products) {
                    transaction.addCommandUnit(new ItemStatusDBUnit(newStatus, -1, product));
                }
                transaction.execute();
            } while (products.size() > 0);
            brand.clearValue("change_status");
            executeCommand(SaveItemDBUnit.get(brand).noTriggerExtra().noFulltextIndex());
        }
    }

    @Override
    public PersistenceCommandUnit createCommand(Item item) throws Exception {
        return new Command(item);
    }
}
