package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashSet;

/**
 * Created by user on 07.08.2018.
 */
public class AddNewProductsFromExcelCommand extends IntegrateBase{
	private static final String CODE = "Код";
	private static final String NAME = "Наименование";
	private static final String DESCRIPTION = "Описание";
	private static final String PRICE = "Цена";
	private static final String PIC_FOLDER = "/pic_folder/";
	private ExcelPriceList price;
	private Item temporarySec;

	@Override
	protected boolean makePreparations() throws Exception {
		temporarySec = ItemQuery.loadSingleItemByName("temporary_section");
		if(temporarySec == null) return false;
		File priceFile = temporarySec.getFileValue(ItemNames.catalog.INTEGRATION, AppContext.getFilesDirPath(false));
		price = new ExcelPriceList(priceFile, CODE,NAME, DESCRIPTION, PRICE) {
			private int rowNum = 0;
			@Override
			protected void processRow() throws Exception {
				String code = getValue(CODE);
				if(StringUtils.isBlank(code))return;
				String name = getValue(NAME);
				String description = getValue(DESCRIPTION);
				BigDecimal price = getCurrencyValue(PRICE);
				Item existingProduct = ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, ItemNames.product.CODE, code);
				if(existingProduct == null){
					existingProduct = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.PRODUCT), temporarySec);
					existingProduct.setValue(ItemNames.product.CODE, code);
					existingProduct.setValue(ItemNames.product.NAME, name);
					if(StringUtils.isNotBlank(description))existingProduct.setValue(ItemNames.product.TEXT, description);
					existingProduct.setValue(ItemNames.product.PRICE, price);
					File pic = new File(AppContext.getContextPath()+PIC_FOLDER+code+".jpg");
					if(pic.exists()){
						existingProduct.setValue(ItemNames.product.MAIN_PIC, pic);
					}
				}else{
					existingProduct.setValue(ItemNames.product.PRICE, price);
				}
				DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(existingProduct).noFulltextIndex().ingoreComputed());
				setProcessed(rowNum++);
			}
		};
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		info.setOperation("Создание прайс-листа");
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
