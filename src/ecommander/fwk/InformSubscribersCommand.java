package ecommander.fwk;

import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

import java.util.List;

public class InformSubscribersCommand extends Command {

	protected static final String OBSERVER_ITEM = "observer";
	protected static final String PRODUCT_ITEM = "product";

	protected static final String CODE_PARAM = "code";
	protected static final String OBSERVER_PARAM = "observer";
	protected static final String OBSERVABLE_PARAM = "observable";

	@Override
	public ResultPE execute() throws Exception {
		List<Item> subscribers = new ItemQuery(OBSERVER_ITEM).loadItems();
		for (Item subscriber : subscribers) {
			Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, subscriber.getStringValue(OBSERVABLE_PARAM));
			if (product != null) {
				if (product.getDecimalValue())
			}
			executeAndCommitCommandUnits(ItemStatusDBUnit.delete(subscriber));
		}
		return null;
	}
}
