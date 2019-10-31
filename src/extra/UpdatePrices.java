package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Product;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.math.BigDecimal;

/**
 * Разбор файла с ценой
 * Created by E on 1/3/2018.
 */
public class UpdatePrices extends IntegrateBase implements CatalogConst {
	private static final String QTY_HEADER = "кол-во";
	private static final String PRICE_HEADER = "с НДС";
	private static final String UNIT_HEADER = "ед";

	private ExcelPriceList price;

	@Override
	protected boolean makePreparations() throws Exception {
		Item catalog = ItemQuery.loadSingleItemByName(ItemNames.CATALOG);
		if (catalog == null)
			return false;
		File priceFile = catalog.getFileValue(INTEGRATION_PARAM, AppContext.getFilesDirPath(false));
		if (priceFile.exists()) {
			price = new ExcelPriceList(priceFile, QTY_HEADER, PRICE_HEADER) {
				@Override
				protected void processRow() throws Exception {
					String code = StringUtils.replace(getValue(0), " ", "");
					if (StringUtils.isNotBlank(code)) {
						Product prod = Product.get(ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, CODE_PARAM, code));
						if (prod != null) {
							Double qty = getDoubleValue(QTY_HEADER);
							if (qty == null)
								qty = 0d;
							prod.set_qty(new BigDecimal(qty));
							prod.set_price(getCurrencyValue(PRICE_HEADER));
							prod.set_unit(getValue(UNIT_HEADER));
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
			return price != null;
		}
		return false;
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
