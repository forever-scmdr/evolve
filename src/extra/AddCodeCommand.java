package extra;

import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

import java.util.List;

public class AddCodeCommand extends Command {
	@Override
	public ResultPE execute() throws Exception {
		List<Item> allProducts = new ItemQuery("product").loadItems();
		for (Item product : allProducts) {
			if (product.isValueEmpty("code")) {
				product.setValueUI("code", product.getId() + "");
				executeAndCommitCommandUnits(SaveItemDBUnit.get(product));
			}
		}
		return null;
	}
}
