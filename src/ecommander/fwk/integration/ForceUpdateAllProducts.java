package ecommander.fwk.integration;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.MysqlConnector;
import ecommander.model.Item;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.mappers.ItemMapper;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * Created by user on 30.05.2019.
 */
public class ForceUpdateAllProducts  extends IntegrateBase implements CatalogConst {
	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		long startFrom = -1;
		int step = 1000;
		Connection conn = MysqlConnector.getConnection();
		ArrayList<Item> products;
		setProcessed(0);
//		setOperation("force updating products");
//		while((products = ItemMapper.loadByName(PRODUCT_ITEM, step, startFrom, conn)).size() > 0){
//			startFrom = processProducts(products);
//		}
		setOperation("force updating LINE products");
		while((products = ItemMapper.loadByName(LINE_PRODUCT_ITEM, step, startFrom, conn)).size() > 0){
				startFrom = processProducts(products);
		}

	}

	private long processProducts(ArrayList<Item> products) throws Exception {
		for(Item product : products){
			executeAndCommitCommandUnits(SaveItemDBUnit.forceUpdate(product).noFulltextIndex());
			info.increaseProcessed();
		}
		if(products.size() > 0) return products.get(products.size() -1).getId();
		return -1;
	}

	@Override
	protected void terminate() throws Exception {

	}
}
