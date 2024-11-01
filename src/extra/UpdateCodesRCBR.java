package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Обновлять уникальный артикул для товаров
 */
public class UpdateCodesRCBR extends IntegrateBase implements ItemNames {

	public static final String ARTICUL_COL = "Артикул";
	public static final String NOMENCLATURE_CODE_COL = "Номенклатура.Код";


	ExcelPriceList priceWB;
	Item catalog;

	@Override
	protected boolean makePreparations() throws Exception {
		catalog = ItemUtils.ensureSingleRootAnonymousItem(CATALOG, User.getDefaultUser());
		String repository = AppContext.getFilesDirPath(catalog.isFileProtected());
		File priceList = catalog.getFileValue(catalog_.INTEGRATION, repository);
		priceWB = new ExcelPriceList(priceList, ARTICUL_COL, NOMENCLATURE_CODE_COL) {
			private int rowNum = 0;
			@Override
			protected void processRow() throws Exception {
				String oldCode = getValue(ARTICUL_COL);
				String newCode = getValue(NOMENCLATURE_CODE_COL);
				if(StringUtils.isBlank(oldCode) || StringUtils.isBlank(newCode)) return;
				Item product = ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, product_.CODE, oldCode);
				if(product != null){
					product.setValueUI(product_.CODE, newCode);
					product.setValueUI(product_.VENDOR_CODE, oldCode);
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
		info.setOperation("Обновлние кодов товаров");
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
