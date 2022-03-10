package ecommander.fwk.external_shops;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractShopImport extends IntegrateBase implements CatalogConst {
	protected Item shop;
	protected Item currency;
	protected Item catalog;
	protected String SHOP_NAME;
	private static final int LOAD_BATCH_SIZE = 1000;
	private static final int STATUS_BATCH_SIZE = 500;

	protected abstract boolean downloadData() throws Exception;
	protected abstract void processData() throws Exception;
	protected abstract String getShopName();

	@Override
	protected boolean makePreparations() throws Exception {
		boolean shopLoaded;
		try{
			shopLoaded = loadShop();
		}catch (Exception e){
			ServerLogger.error("Integeration error", e);
			addError(e);
			return false;
		}
		if(!shopLoaded){ return false;}
		boolean dataDownloaded;
		try {
			dataDownloaded = downloadData();
		}catch (Exception e){
			ServerLogger.error("Integeration error", e);
			addError(e);
			return false;
		}
		return dataDownloaded;
	}

	private boolean loadShop() throws Exception {
		SHOP_NAME = getShopName();
		shop = ItemQuery.loadSingleItemByParamValue("shop", "name", SHOP_NAME);
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
		currency = currencies.get(0);
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
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		hideAllProducts();
		setOperation("Разбор кталога " + SHOP_NAME);
		processData();
		pushLog("Разбор кталога " + SHOP_NAME + " завершен");

		info.setOperation("Индексация названий товаров");
		info.indexsationStarted();
		LuceneIndexMapper.getSingleton().reindexAll();
	}

	private void hideAllProducts() throws Exception {
		setOperation("Скрываем товары с \"" + SHOP_NAME + "\"");
		info.setProcessed(0);
		ItemQuery q = new ItemQuery(PRODUCT_ITEM, Item.STATUS_NORMAL);
		q.setParentId(catalog.getId(), false);
		int page = 1;
		q.setLimit(LOAD_BATCH_SIZE, page);
		List<Item> products;
		int counter = 0;
		HashSet<String> codes = new HashSet<>();
		while ((products = q.loadItems()).size() >= LOAD_BATCH_SIZE) {
			for (Item product : products) {
				String code = product.getStringValue(CODE_PARAM);
				if(!codes.contains(code)) {
					executeCommandUnit(ItemStatusDBUnit.hide(product).ignoreUser().noFulltextIndex());
					codes.add(code);
				}else {
					executeCommandUnit(ItemStatusDBUnit.delete(product).ignoreUser().noFulltextIndex());
				}
				counter++;
				if (counter >= STATUS_BATCH_SIZE) {
					counter = 0;
					commitCommandUnits();
				}
				info.increaseProcessed();
			}
			commitCommandUnits();
		}
	}

	@Override
	protected void terminate() throws Exception {

	}
}
