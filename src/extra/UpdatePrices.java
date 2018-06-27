package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Product;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Разбор файла с ценой
 * Created by E on 1/3/2018.
 */
public class UpdatePrices extends IntegrateBase {
	private static final String CODE_HEADER = "Но#";
	private static final String PRICE_OLD_HEADER = "Цена без скидки";
	private static final String PRICE_NEW_HEADER = "Цена со скидкой";
	private static final String AVAILABLE_HEADER = "Наличие";

	private ExcelPriceList price;

	@Override
	protected boolean makePreparations() throws Exception {
		Item catalog = ItemQuery.loadSingleItemByName(ItemNames.CATALOG);
		if (catalog == null)
			return false;
		File priceFile = catalog.getFileValue(ItemNames.catalog.INTEGRATION, AppContext.getFilesDirPath(false));
		price = new ExcelPriceList(priceFile, CODE_HEADER, PRICE_OLD_HEADER, PRICE_NEW_HEADER, AVAILABLE_HEADER) {
			@Override
			protected void processRow() throws Exception {
				String code = StringUtils.replace(getValue(0), " ", "");
				if (StringUtils.isNotBlank(code)) {
					Product prod = Product.get(ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, ItemNames.product.CODE, code));
					if (prod != null) {
						String priceOld = getValue(1);
						String priceNew = getValue(2);
						boolean available = StringUtils.contains(getValue(3), "+");
						if (StringUtils.isNotBlank(priceNew)) {
							prod.setValueUI(ItemNames.product.PRICE, priceNew);
							prod.setValueUI("price_old", priceOld);
						} else {
							prod.setValueUI(ItemNames.product.PRICE, priceOld);
						}
						prod.setValue("available", available ? (byte)1 : (byte)0);
						DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(prod).noFulltextIndex().ingoreComputed());
						info.increaseProcessed();
					} else {
						info.increaseLineNumber();
						info.pushLog("Товар с кодом {} и названием {} не найден в каталоге", code, getValue(1));
					}
				}
			}

			@Override
			protected void processSheet() throws Exception {

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
		info.limitLog(500);
		price.iterate();
		info.setOperation("Интеграция завершена");
		price.close();
	}

	@Override
	protected void terminate() throws Exception {

	}
}
