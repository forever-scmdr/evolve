package ecommander.fwk.integration;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.model.Item;
import ecommander.persistence.common.PersistenceCommandUnit;

/**
 * Created by E on 23/11/2018.
 */
public class UpdateProductMinPriceDeletedFactory implements ItemEventCommandFactory {

	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new UpdateProductMinPriceFactory.UpdateMinPrice(item, true);
	}
}
