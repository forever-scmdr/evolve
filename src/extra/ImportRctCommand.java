package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.fwk.external_shops.ExternalShopPriceCalculator;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ImportRctCommand extends IntegrateBase implements CatalogConst {
	private static final String ENDPOINT = "http://www.rct.ru/price/all";
	private static final String FILE_NAME = "rct.xlsx";
	private static final String SHOP_NAME = "rct.ru";
	private static final int LOAD_BATCH_SIZE = 1000;
	private static final int STATUS_BATCH_SIZE = 500;
	private static final String CODE_PREFIX = "rct-";
	private Item catalog;

	ExcelPriceList priceWB;


	@Override
	protected boolean makePreparations() throws Exception {
		File file;
		try {
			file = Paths.get(AppContext.getFilesDirPath(false), FILE_NAME).toFile();
			if (file.exists()) {
				FileUtils.deleteQuietly(file);
			}
			WebClient.saveFile(ENDPOINT, AppContext.getCommonFilesDirPath(), FILE_NAME);
		} catch (Exception e) {
			ServerLogger.error("Integeration error", e);
			addError(e);
			return false;
		}

		try {
			Item shop = ItemQuery.loadSingleItemByParamValue("shop", "name", SHOP_NAME);
			if (shop == null) {
				info.addError(new Exception("No shop \"" + SHOP_NAME + "\""));
				return false;
			}
			ArrayList<Item> currencies = ItemQuery.loadByParentId(shop.getId(), new Byte[]{ItemTypeRegistry.getAssocId("general")});
			if (currencies.size() == 0) {
				info.addError(new Exception("No currency selected for shop \"" + SHOP_NAME + "\""));
				return false;
			}
			if (currencies.size() > 1) {
				info.addError(new Exception("More than one currency selected for shop \"" + SHOP_NAME + "\""));
				return false;
			}
			Item currency = currencies.get(0);
			ArrayList<Item> catalogs = ItemQuery.loadByParentId(shop.getId(), new Byte[]{ItemTypeRegistry.getPrimaryAssoc().getId()});
			for (Item c : catalogs) {
				if (c.getTypeId() == ItemTypeRegistry.getItemTypeId("shop_catalog") && c.getStatus() != Item.STATUS_DELETED) {
					catalog = c;
					break;
				}
			}
			if (catalog == null) {
				catalog = ItemUtils.newChildItem("shop_catalog", shop);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).ignoreUser().noFulltextIndex());
			}
			priceWB = new RctPriceList(file, catalog, currency);
		} catch (Exception e) {
			ServerLogger.error("Integeration error", e);
			addError(e);
			return false;
		}

		return true;
	}

	@Override
	protected void integrate() throws Exception {
		hideAllProducts();
		setOperation("Разбор кталога " + SHOP_NAME);
		priceWB.iterate();
		pushLog("Разбор кталога " + SHOP_NAME + " завершен");

		info.setOperation("Индексация названий товаров");
		info.indexsationStarted();
		LuceneIndexMapper.getSingleton().reindexAll();
	}

	private void hideAllProducts() throws Exception {
		setOperation("Скрываем товары с \"" + SHOP_NAME + "\"");
		info.setProcessed(0);
		ItemQuery q = new ItemQuery(PRODUCT_ITEM);
		q.setParentId(catalog.getId(), false);
		int page = 1;
		q.setLimit(LOAD_BATCH_SIZE, page);
		List<Item> products = new LinkedList<>();
		int counter = 0;
		while ((products = q.loadItems()).size() >= LOAD_BATCH_SIZE) {
			for (Item product : products) {
				executeCommandUnit(ItemStatusDBUnit.hide(product).ignoreUser().noFulltextIndex());
				counter++;
				if (counter >= STATUS_BATCH_SIZE) {
					counter = 0;
					commitCommandUnits();
				}
				info.increaseProcessed();
			}
			q.setLimit(LOAD_BATCH_SIZE, ++page);
		}
		if (counter > 0) {
			commitCommandUnits();
		}
	}

	@Override
	protected void terminate() throws Exception {

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
				executeCommandUnit(ItemStatusDBUnit.restore(product).ignoreUser().noFulltextIndex());
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


			executeAndCommitCommandUnits(SaveItemDBUnit.get(product).ignoreUser().noFulltextIndex());
			info.increaseProcessed();
		}

		@Override
		protected void processSheet() throws Exception {
			info.setProcessed(0);
		}

	}
}
