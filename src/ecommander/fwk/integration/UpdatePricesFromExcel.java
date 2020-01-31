package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Created by anton on 06.12.2018.
 */
public class UpdatePricesFromExcel extends IntegrateBase implements CatalogConst {
	ExcelPriceList priceWB;
	Item catalog;

	@Override
	protected boolean makePreparations() throws Exception {
		catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
		String repository = AppContext.getFilesDirPath(catalog.isFileProtected());
		File priceList = catalog.getFileValue(INTEGRATION_PARAM, repository);
		priceWB = new ExcelPriceList(priceList, CreateExcelPriceList.CODE_FILE, CreateExcelPriceList.PRICE_FILE) {
			private int rowNum = 0;
			@Override
			protected void processRow() throws Exception {
				String code = getValue(CreateExcelPriceList.CODE_FILE);
				if(StringUtils.isBlank(code) || CreateExcelPriceList.CODE_FILE.equalsIgnoreCase(code) || StringUtils.startsWith(code,"разд:")) return;
				String price = getValue(CreateExcelPriceList.PRICE_FILE);
				String qty = getValue(CreateExcelPriceList.QTY_FILE);
				String av = getValue(CreateExcelPriceList.AVAILABLE_FILE);
				String oldPrice = getValue(CreateExcelPriceList.PRICE_OLD_FILE);
				String origPrice = getValue(CreateExcelPriceList.PRICE_ORIGINAL_FILE);
				String currency = getValue(CreateExcelPriceList.CURRENCY_ID_FILE);
				String unit = getValue(CreateExcelPriceList.UNIT_FILE);
				Item product = ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, CODE_PARAM, code);
				if(product != null){
					product.setValueUI(PRICE_PARAM, price.replaceAll("[^\\d,.]",""));
					if(qty != null) {
						product.setValueUI(QTY_PARAM, qty);
					}
					if(av != null) {
						product.setValueUI(AVAILABLE_PARAM, av);
					}
					if(oldPrice != null){
						product.setValueUI(PRICE_OLD_PARAM, oldPrice.replaceAll("[^\\d,.]",""));
					}
					if(origPrice != null){
						product.setValueUI(PRICE_ORIGINAL_PARAM, origPrice.replaceAll("[^\\d,.]",""));
					}
					if(currency != null) {
						product.setValueUI(CURRENCY_ID_PARAM, currency);
					}
					if(unit != null) {
						product.setValueUI(CURRENCY_ID_PARAM, currency);
					}
					DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(product).noFulltextIndex().noTriggerExtra());
					setProcessed(rowNum++);
				}
			}

			@Override
			protected void processSheet() throws Exception {}
		};
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		//catalog.setValue(INTEGRATION_PENDING_PARAM, (byte)1);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).noFulltextIndex());
		info.setOperation("Обновлние цен");
		info.setProcessed(0);
		info.setLineNumber(0);
		info.setToProcess(priceWB.getLinesCount());
		priceWB.iterate();
		info.setOperation("Интеграция завершена");
		priceWB.close();
	//	catalog.setValue(INTEGRATION_PENDING_PARAM, (byte)0);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).noFulltextIndex());
	}


}
