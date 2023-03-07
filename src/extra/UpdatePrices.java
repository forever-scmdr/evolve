package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Product;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

/**
 * Разбор файла с ценой
 * Created by E on 1/3/2018.
 */
public class UpdatePrices extends IntegrateBase {
	//private static final String QTY_HEADER = "кол-во";
	private static final String PRICE_HEADER = "Цена";

	private ExcelPriceList price;

	@Override
	protected boolean makePreparations() throws Exception {
		Item catalog = ItemQuery.loadSingleItemByName(ItemNames.CATALOG);
		if (catalog == null)
			return false;
		File priceFile = catalog.getFileValue(ItemNames.catalog.INTEGRATION, AppContext.getFilesDirPath(false));
		List<Item> sections = new ItemQuery(ItemNames.SECTION).loadItems();
		for (Item section : sections) {
			executeAndCommitCommandUnits(ItemStatusDBUnit.hide(section));
		}
		price = new ExcelPriceList(priceFile, PRICE_HEADER) {
			@Override
			protected void processRow() throws Exception {
				String code = StringUtils.replace(getValue(0), " ", "");
				if (StringUtils.isNotBlank(code)) {
					Item prod = ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, ItemNames.product.CODE, code, Item.STATUS_NORMAL, Item.STATUS_NIDDEN);
					if (prod != null) {
						DelayedTransaction.executeSingle(User.getDefaultUser(), ItemStatusDBUnit.restore(prod));
						prod.setValue(ItemNames.product.PRICE, getCurrencyValue(PRICE_HEADER));
						DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(prod).noFulltextIndex().ingoreComputed());
						info.increaseProcessed();
					} else {
						info.increaseLineNumber();
						info.pushLog("Товар с кодом {} и названием {} не найден в каталоге", code, getValue(1));
					}
				}
			}
		};
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		info.setOperation("Обновление прайс-листа");
		info.setProcessed(0);
		info.setLineNumber(0);
		info.setToProcess(price.getLinesCount());
		price.iterate();
		info.setOperation("Интеграция завершена");
		price.close();
	}

	@Override
	protected void terminate() throws Exception {

	}
}
