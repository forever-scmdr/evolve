package ecommander.fwk.external_shops.rct;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.WebClient;
import ecommander.fwk.external_shops.AbstractShopImport;
import ecommander.fwk.external_shops.ExternalShopPriceCalculator;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.HashMap;

public class ImportRctCommand extends AbstractShopImport implements CatalogConst {
	private static final String ENDPOINT = "http://www.rct.ru/price/all";
	private static final String FILE_NAME = "rct.xlsx";
	private static final String SHOP_NAME = "rct.ru";
	private static final String CODE_PREFIX = "";

	ExcelPriceList priceWB;


	@Override
	protected boolean downloadData() throws Exception {
		File file = Paths.get(AppContext.getFilesDirPath(false), FILE_NAME).toFile();
		if (file.exists()) {
			FileUtils.deleteQuietly(file);
		}
		WebClient.saveFile(ENDPOINT, AppContext.getCommonFilesDirPath(), FILE_NAME);
		priceWB = new RctPriceList(file, catalog, currency);
		return true;
	}

	@Override
	protected void processData() throws Exception {
		priceWB.iterate();
	}


	@Override
	protected void terminate() throws Exception {

	}

	@Override
	protected String getShopName() {
		return ImportRctCommand.SHOP_NAME;
	}

	private final class RctPriceList extends ExcelPriceList implements CatalogConst {
		private Item catalog;
		private Item currency;
		//private Info info;

		private final HashMap<String, String> HEADER_PARAMS = new HashMap<>();


		public RctPriceList(File file, Item catalog, Item currency) {
			super(file, "Категории", "Номенклатура", "Код", "Норма отгрузки", "Опт", "Свободный остаток");
			this.catalog = catalog;
			this.currency = currency;
			//this.info = info;
			HEADER_PARAMS.put("Номенклатура", NAME);
			HEADER_PARAMS.put("Код", CODE_PARAM);
			HEADER_PARAMS.put("Свободный остаток", QTY_PARAM);
			HEADER_PARAMS.put("Опт", PRICE_ORIGINAL_PARAM);
			HEADER_PARAMS.put("Норма отгрузки", STEP_PARAM);
			HEADER_PARAMS.put("Описание", DESCRIPTION_PARAM);
			HEADER_PARAMS.put("Производитель", VENDOR_PARAM);
			//info.setProcessed(0);
		}

		@Override
		protected void processRow() throws Exception {
			info.setLineNumber(getRowNum() + 1);

			String code = getValue("Код");
			if (StringUtils.isBlank(code) || "Код".equalsIgnoreCase(code)) return;
			Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, CODE_PREFIX + code, Item.STATUS_HIDDEN, Item.STATUS_NORMAL);
			if (product != null) {
				executeAndCommitCommandUnits(ItemStatusDBUnit.restore(product).ignoreUser().noFulltextIndex());
			}
			product = product == null ? ItemUtils.newChildItem(PRODUCT_ITEM, catalog) : product;
			for (String header : HEADER_PARAMS.keySet()) {
				product.setValueUI(HEADER_PARAMS.get(header), getValue(header));
			}

			//Fix code param
			product.setValueUI(CODE_PARAM, CODE_PREFIX + product.getValue(CODE_PARAM));

			//Create search param value
			String sec = getValue("Категории");
			String cover = getValue("Тип корпуса");
			String analogs = getValue("Аналоги");

			if (StringUtils.isNotBlank(cover)) {
				String description = product.getStringValue(DESCRIPTION_PARAM);
				product.setValueUI(DESCRIPTION_PARAM, description + "<br/><strong>Тип корпуса: </strong>" + cover);
			}
			String search = StringUtils.join(new String[]{sec, product.getStringValue(NAME), product.getStringValue(CODE_PARAM), cover, product.getStringValue(VENDOR_PARAM), analogs}, ' ');
			product.setValueUI(SEARCH_PARAM, search);

			//Calculate price
			BigDecimal bynPrice = ExternalShopPriceCalculator.convertToByn(product.getDecimalValue(PRICE_ORIGINAL_PARAM, BigDecimal.ZERO), currency, catalog);
			if (bynPrice.compareTo(BigDecimal.ZERO) > 0) {
				product.setValue(PRICE_PARAM, bynPrice);
			}

			product.setValueUI(TAG_PARAM, "external_shop");
			product.setValueUI(TAG_PARAM, SHOP_NAME);
			String q =  getValue("Норма отгрузки");
			q = StringUtils.isBlank(q)? "1" : q;
			product.setValueUI(MIN_QTY_PARAM, q);
			product.setValueUI(CURRENCY_ID_PARAM, currency.getStringValue(NAME_PARAM));


			executeAndCommitCommandUnits(SaveItemDBUnit.get(product).ignoreUser().noFulltextIndex());
			info.increaseProcessed();
		}

		@Override
		protected void processSheet() throws Exception {
			info.setProcessed(0);
		}

	}
}
