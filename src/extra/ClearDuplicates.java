package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ClearDuplicates extends IntegrateBase implements CatalogConst {
	private static final int LIMIT = 1000;
	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception
	{
		setOperation("Deleting duplicates");
		setProcessed(0);
		setLineNumber(0);
		Item garbageSection = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, NAME_PARAM, "Прочее");
		ItemQuery q = new ItemQuery(PRODUCT_ITEM, Item.STATUS_HIDDEN, Item.STATUS_NORMAL);
		q.setParentId(garbageSection.getId(), false, ItemTypeRegistry.getPrimaryAssoc().getName());
		q.setLimit(LIMIT, 1);
		List<Item> products;
		int n = 0;
		int page = 1;
		while ((products = q.loadItems()).size() == LIMIT){
			for(Item product : products){
				String code = product.getStringValue(CODE_PARAM);
				if(StringUtils.isBlank(code)){
					executeAndCommitCommandUnits(ItemStatusDBUnit.delete(product.getId()).ignoreUser(true).noFulltextIndex());
					info.increaseProcessed();
					continue;
				}
				List<Item> duplicates = ItemQuery.loadByParamValue(PRODUCT_ITEM, CODE_PARAM, code, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
				if(duplicates.size() > 1){
					executeAndCommitCommandUnits(ItemStatusDBUnit.delete(product.getId()).ignoreUser(true).noFulltextIndex());
					pushLog("Deleted duplicate: "+ code);
					info.increaseProcessed();
				}
				setLineNumber(++n);
			}
			page++;
			q.setLimit(LIMIT, page);
		}
	}

	@Override
	protected void terminate() throws Exception {}
}
