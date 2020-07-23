package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CheckProductDates extends IntegrateBase implements CatalogConst {
	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		setOperation("Проверка дат актуальнсти.");
		SimpleDateFormat format = new SimpleDateFormat("YYYY");
		long defaultDate = format.parse("2100").getTime();
		long startID = 0;
		long day = 24 * 60 * 60 * 1000;
		long now = new Date().getTime();
		now -= now % day;
		info.setProcessed(0);
		ItemQuery q = new ItemQuery(PRODUCT_ITEM, Item.STATUS_HIDDEN, Item.STATUS_NORMAL);
		q.setIdSequential(0);
		q.setLimit(1000);
		List<Item> products = q.loadItems();
		while (products != null && products.size() > 0){
			long id = 0;
			for(Item product : products){
				id = product.getId();
				long date = product.getLongValue(DATE_PARAM, defaultDate);
				if(date < now){
					executeAndCommitCommandUnits(ItemStatusDBUnit.hide(id).noTriggerExtra().noFulltextIndex());
				}else{
					if(product.getValue(DATE_PARAM) == null) {
						product.setValue(DATE_PARAM, defaultDate);
						executeCommandUnit(SaveItemDBUnit.get(product).noFulltextIndex().noTriggerExtra().ignoreFileErrors(true));
					}
					executeCommandUnit(ItemStatusDBUnit.restore(id).noTriggerExtra().noFulltextIndex());
					commitCommandUnits();
				}
				info.increaseProcessed();
			}
			q.setIdSequential(id);
			products = q.loadItems();
		}
		info.setOperation("Индексация названий товаров");
		LuceneIndexMapper.getSingleton().reindexAll();
		setOperation("Интеграция завершена");
	}
}
