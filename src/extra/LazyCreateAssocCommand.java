package extra;

import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Заполняет параметр assoc_code товара случайными кодами товаров из того же раздела.
 * Нужно для того, чтобы у товаров всегда был одинаковый список сопустсвующих
 */
public class LazyCreateAssocCommand extends Command {

	private int MAX_ASSOC_COUNT = 6;
	private int SECTION_PRODUCTS_LIMIT = 50;

	@Override
	public ResultPE execute() throws Exception {
		LinkedHashMap<Long, Item> prods = getLoadedItems("prod");
		Item prod = null;
		if (prods.size() > 0) {
			prod = prods.values().iterator().next();
		} else {
			return null;
		}
		if (prod.isValueNotEmpty("assoc_code")) {
			return null;
		}
		Item section = new ItemQuery("section").setChildId(prod.getId(), false).loadFirstItem();
		if (section == null) {
			return null;
		}
		ArrayList<Item> assocs = (ArrayList<Item>) new ItemQuery("product")
				.setParentId(section.getId(), true).setLimit(SECTION_PRODUCTS_LIMIT).loadItems();

		Random rand = new Random();
		LinkedHashSet<String> assocCodes = new LinkedHashSet<>();
		if (assocs.size() == 1) {
			return null;
		}
		for (int i = 0; i < MAX_ASSOC_COUNT; i++) {
			Item assoc = assocs.get(rand.nextInt(assocs.size()));
			assocCodes.add(assoc.getStringValue("code"));
		}
		assocCodes.remove(prod.getStringValue("code"));
		for (String assocCode : assocCodes) {
			prod.setValueUI("assoc_code", assocCode);
		}
		executeAndCommitCommandUnits(SaveItemDBUnit.get(prod).noTriggerExtra().noFulltextIndex());
		return null;
	}
}
