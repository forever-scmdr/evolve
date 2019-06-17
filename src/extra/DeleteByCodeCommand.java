package extra;

import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.Catalog;
import extra._generated.ItemNames;
import extra._generated.Product;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by E on 17/6/2019.
 */
public class DeleteByCodeCommand extends Command implements ItemNames {
	@Override
	public ResultPE execute() throws Exception {
		Item catalog = ItemQuery.loadSingleItemByName(CATALOG);
		if (catalog == null)
			return null;
		String[] codes = StringUtils.split(catalog.getStringValue(Catalog.DELETE_LIST), "\r\n ,");
		for (String code : codes) {
			Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT, Product.CODE, code);
			if (product != null)
				executeAndCommitCommandUnits(ItemStatusDBUnit.delete(product));
		}
		return null;
	}
}
