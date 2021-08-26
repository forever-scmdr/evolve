package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

import java.util.List;

/**
 * Created by user on 15.04.2019.
 */
public class SaveAllProductsCommand extends IntegrateBase implements CatalogConst{
	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception
	{
		setOperation("Пересохранение товаров");
		long startID = 0;
		List<Item> products;
		info.setProcessed(0);
		ItemQuery q = new ItemQuery(PRODUCT_ITEM, Item.STATUS_HIDDEN, Item.STATUS_NORMAL);
		int p =1;
		q.setLimit(500,p);

			while ((products = q.loadItems()).size() > 0) {
				for (Item product : products) {
					startID = product.getId();
					product.setValueUI("date", "31.12.2100 00:00");
					executeAndCommitCommandUnits(SaveItemDBUnit.forceUpdate(product).noFulltextIndex().noTriggerExtra());
					info.increaseProcessed();
				}
				p++;
				q.setLimit(500,p);
			}

	}

}
