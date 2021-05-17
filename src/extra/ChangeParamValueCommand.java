package extra;

import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

import java.util.List;

public class ChangeParamValueCommand extends Command {
	private final static String OLD = "Упаковка";
	private final static String NEW = "Комплектация";
	private final static String PARAM_NAME = "name";
	private final static String ITEM_NAME = "product_extra";
	private final static int LIMIT = 500;
	private final static int TRANSACTION_BATCH_SIZE = 50;

	private int counter = 0;

	@Override
	public ResultPE execute() throws Exception {
		ItemQuery q = new ItemQuery(ITEM_NAME);
		q.addParameterCriteria(PARAM_NAME, OLD, "=", null, Compare.SOME);
		int page = 1;
		q.setLimit(LIMIT, page);
		List<Item> items = q.loadItems();
		while (items.size() > 0){
			for(Item item : items){
				//item.setValue(PARAM_NAME, NEW);
				//executeCommandUnit(SaveItemDBUnit.get(item).ignoreFileErrors().noFulltextIndex().noTriggerExtra());
				executeCommandUnit(ItemStatusDBUnit.delete(item.getId()).ignoreUser(true));
				counter++;
				if(counter >= TRANSACTION_BATCH_SIZE){
					commitCommandUnits();
					counter = 0;
				}
			}
			page++;
			q.setLimit(LIMIT, page);
			items = q.loadItems();
		}
		if(counter > 0){
			commitCommandUnits();
		}
		return null;
	}
}
