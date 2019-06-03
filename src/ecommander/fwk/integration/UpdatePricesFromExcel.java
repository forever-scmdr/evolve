package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.MysqlConnector;
import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.ItemMapper;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.Connection;
import java.util.LinkedList;

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

				Item product = ItemQuery.loadSingleItemByParamValue(ItemNames.LINE_PRODUCT, CODE_PARAM, code);
				product = product == null? ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, CODE_PARAM, code) : product;

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
					DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(product).noFulltextIndex());
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
		checkAvailable();
		info.setOperation("Интеграция завершена");
		priceWB.close();
	//	catalog.setValue(INTEGRATION_PENDING_PARAM, (byte)0);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).noFulltextIndex());
	}

	private void checkAvailable() throws Exception {
		info.setOperation("Проверка наличия товаров");
		long startFrom = -1;
		int step = 1000;
		Connection conn = MysqlConnector.getConnection();
		setProcessed(0);
		LinkedList<Item> allProducts = new LinkedList<>();
		allProducts.addAll(ItemMapper.loadByName(PRODUCT_ITEM, step, startFrom, conn));
		while (allProducts.size() > 0){
			Item product;
			while ((product = allProducts.poll()) != null){
				startFrom = product.getId();
				if(product.getByteValue(ItemNames.product_.HAS_LINES, (byte)0) == 0){info.increaseProcessed(); continue;}
				ItemQuery q = new ItemQuery(LINE_PRODUCT_ITEM);
				q.setParentId(product.getId(), false);
				q.addParameterCriteria(ItemNames.line_product_.AVAILABLE, "1", "=", null, Compare.SOME);
				byte av = q.loadFirstItem() != null? (byte) 1 : (byte)0;
				product.setValue(ItemNames.product_.AVAILABLE, av);
				DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(product).noFulltextIndex().noTriggerExtra());
				info.increaseProcessed();
			}
			allProducts.addAll(ItemMapper.loadByName(PRODUCT_ITEM, step, startFrom, conn));
		}
	}


	@Override
	protected void terminate() throws Exception {

	}

}
