package ecommander.fwk.integration;

import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

import java.util.List;

/**
 * Created by E on 15/10/2018.
 */
public class ResetPriceCommand extends Command {
	@Override
	public ResultPE execute() throws Exception {
		ItemQuery prodQuery = new ItemQuery("product").addParameterCriteria("price", "0.001", ">", null, Compare.SOME).setLimit(10);
		List<Item> prods = prodQuery.loadItems();
		while (prods.size() > 0) {
			for (Item prod : prods) {
				prod.setValueUI("price", "0");
				executeCommandUnit(SaveItemDBUnit.get(prod).noFulltextIndex());
			}
			commitCommandUnits();
		}
		return null;
	}
}
