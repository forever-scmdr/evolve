package ecommander.fwk.integration;

import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.persistence.itemquery.ItemQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 21.02.2019.
 */
public class CreateSeeAlso extends IntegrateBase implements CatalogConst {

	private static final int PACKET_SIZE = 40;

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		Item catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
		processSubsections(catalog);
	}

	private void processSubsections(Item catalog) throws Exception {
		ItemQuery q = new ItemQuery(SECTION_ITEM).setParentId(catalog.getId(), false);
		List<Item> subs = q.loadItems();
		for(Item sub : subs){
			processProducts(sub);
			processSubsections(sub);
		}
	}

	private void processProducts(Item sub) throws Exception {
		ItemQuery q = new ItemQuery(PRODUCT_ITEM);
		q.setParentId(sub.getId(),false);
		q.addSorting(CODE_PARAM,"ASC");
		ArrayList<Item> products = new ArrayList<>();
		products.addAll(q.loadItems());

		for(int i = 0; i<products.size(); i++){

		}
	}

	@Override
	protected void terminate() throws Exception {

	}
}
