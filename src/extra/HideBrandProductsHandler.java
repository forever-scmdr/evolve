package extra;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.model.Item;
import ecommander.persistence.common.PersistenceCommandUnit;

public class HideBrandProductsHandler implements ItemEventCommandFactory {
    @Override
    public PersistenceCommandUnit createCommand(Item item) throws Exception {
        return null;
    }
}
