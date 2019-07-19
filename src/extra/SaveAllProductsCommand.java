package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.MysqlConnector;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.mappers.ItemMapper;

import java.sql.Connection;
import java.util.ArrayList;

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
		ArrayList<Item> products;
		info.setProcessed(0);
		try(Connection conn = MysqlConnector.getConnection()) {
			while ((products = ItemMapper.loadByName(PRODUCT_ITEM, 500, startID)).size() > 0) {
				for (Item product : products) {
					startID = product.getId();
					product.forceInitialInconsistent();
					executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noTriggerExtra().noFulltextIndex().noFulltextIndex());
					info.increaseProcessed();
				}
			}
		}
	}

	@Override
	protected void terminate() throws Exception {

	}
}
