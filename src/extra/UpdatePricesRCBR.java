package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.integration.CatalogConst;
import ecommander.fwk.integration.CreateExcelPriceList;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Обновить цену для RCBR
 */
public class UpdatePricesRCBR extends IntegrateBase implements CatalogConst {
	private ExcelPriceList priceWB;
	private Item catalog;

	public static final String NOMENCLATURE_COL = "Номенклатура";
	public static final String NOMENCLATURE_PRINT_COL = "Номенклатура.Наименование для печати";
	public static final String ARTICUL_COL = "Артикул";
	public static final String NOMENCLATURE_CODE_COL = "Номенклатура.Код";
	public static final String ROZ_COL = "Розница";
	public static final String OPT_COL = "Опт";
	public static final String QTY_COL = "Остаток";
	public static final String STATUS_COL = "Наличие";


	@Override
	protected boolean makePreparations() throws Exception {
		catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
		String repository = AppContext.getFilesDirPath(catalog.isFileProtected());
		File priceList = catalog.getFileValue(INTEGRATION_PARAM, repository);
		priceWB = new ExcelPriceList(priceList, NOMENCLATURE_CODE_COL, ROZ_COL) {
			private int rowNum = 0;
			@Override
			protected void processRow() throws Exception {
				String code = getValue(NOMENCLATURE_CODE_COL);
				if (StringUtils.isBlank(code))
					return;
				String price = getValue(ROZ_COL);
				String priceOpt = getValue(OPT_COL);
				String qty = getValue(QTY_COL);
				String status = getValue(STATUS_COL);
				Item product = ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, CODE_PARAM, code);
				if (product != null) {
					product.setValueUI(PRICE_PARAM, price.replaceAll("[^\\d,.]",""));
					product.setValueUI(PRICE_OPT_PARAM, priceOpt.replaceAll("[^\\d,.]",""));
					product.setValueUI(QTY_PARAM, qty);
					product.setValueUI(STATUS_PARAM, status);
					DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(product).noFulltextIndex().noTriggerExtra());
					setProcessed(++rowNum);
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



	@Override
	protected void terminate() throws Exception {

	}

}
