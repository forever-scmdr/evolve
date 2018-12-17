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
public class UpdatePrices extends IntegrateBase implements ItemNames {
	private static final String CODE_HEADER = "Код";
	private static final String PRICE_HEADER = "Цена";
	private static final String QTY_HEADER = "Количество";
	private static final String AVAILABLE_HEADER = "Наличие";

	private ExcelPriceList price;

	@Override
	protected boolean makePreparations() throws Exception {
		Item cat = ItemQuery.loadSingleItemByName(CATALOG);
		if (cat == null)
			return false;
		File priceFile = cat.getFileValue(catalog_.INTEGRATION, AppContext.getFilesDirPath(false));
		price = new ExcelPriceList(priceFile, CODE_HEADER, PRICE_HEADER, QTY_HEADER, AVAILABLE_HEADER) {
			@Override
			protected void processRow() throws Exception {
				String code =getValue(CODE_HEADER).trim();
				if (StringUtils.isNotBlank(code)) {
					Product prod = Product.get(ItemQuery.loadSingleItemByParamValue(PRODUCT, product_.CODE, code));
					if (prod != null) {
						String price = getValue(PRICE_HEADER).trim();
						String qty = getValue(QTY_HEADER).trim();
						String avlb = getValue(AVAILABLE_HEADER).trim();

						prod.setValueUI("qty", qty);
						prod.setValueUI("price", price);
						prod.setValueUI("available", avlb);
						DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(prod).noFulltextIndex().ingoreComputed());
						info.increaseProcessed();
					} else {
						info.increaseLineNumber();
						info.pushLog("Товар с кодом {} и названием {} не найден в каталоге", code, getValue("Название"));
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
