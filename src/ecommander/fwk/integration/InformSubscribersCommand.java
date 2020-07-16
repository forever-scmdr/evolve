package ecommander.fwk.integration;

import ecommander.fwk.integration.EmailQueueSender;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.LinkPE;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

import java.math.BigDecimal;
import java.util.List;

public class InformSubscribersCommand extends Command {

	protected static final String OBSERVER_ITEM = "observer";
	protected static final String PRODUCT_ITEM = "product";

	protected static final String CODE_PARAM = "code";
	protected static final String OBSERVER_PARAM = "observer";
	protected static final String OBSERVABLE_PARAM = "observable";
	protected static final String PRICE_PARAM = "price";
	protected static final String AVAILABLE_PARAM = "available";


	@Override
	public ResultPE execute() throws Exception {
		List<Item> subscribers = new ItemQuery(OBSERVER_ITEM).loadItems();
		for (Item subscriber : subscribers) {
			Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, subscriber.getStringValue(OBSERVABLE_PARAM));
			if (product != null) {
				BigDecimal price = product.getDecimalValue(PRICE_PARAM);
				Byte available = product.getByteValue(AVAILABLE_PARAM);
				if (price != null && available != null && price.compareTo(BigDecimal.ZERO) > 0 && available > 0) {
					LinkPE link = LinkPE.newDirectLink("email", "subscribe_inform_email", false);
					link.addStaticVariable("prod", subscriber.getStringValue(OBSERVABLE_PARAM));
					EmailQueueSender.addEmailToQueue(this, subscriber.getStringValue(OBSERVER_PARAM), link.serialize());
					executeAndCommitCommandUnits(ItemStatusDBUnit.delete(subscriber).ignoreUser());
				}
			}
		}
		return null;
	}
}
