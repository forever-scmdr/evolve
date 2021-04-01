package extra;

import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.ItemMapper;
import extra._generated.ItemNames;

import java.util.List;

/**
 * Created by E on 30/4/2019.
 */
public class CountProducts extends Command {
	@Override
	public ResultPE execute() throws Exception {
		int count = 0;
		List<Item> products = ItemMapper.loadByName(ItemNames.PRODUCT, 100, 0);
		while (products.size() > 0) {
			for (Item product : products) {
				count++;
			}
			products = ItemMapper.loadByName(ItemNames.PRODUCT, 100, products.get(products.size() - 1).getId());

		}
		products = new ItemQuery(ItemNames.PRODUCT).loadItems();
		return getResult("success").setValue("<result><count>" + count + "</count><all>" + products.size() + "</all></result>");
	}
}
