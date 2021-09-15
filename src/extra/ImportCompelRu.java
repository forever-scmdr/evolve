package extra;

import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFRow;
import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class ImportCompelRu extends IntegrateBase implements CatalogConst {
	private Item shop;
	private Item currency;
	private Item catalog;

	private static final String CHARSET = "Cp866";
	private static final String SHOP_NAME = "compel.ru";
	private static final String INTEGRATION_DIR = "upload/compel";
	private static final String DOWNLOAD_URL = "http://www.compel.ru/stockfiles2/16490a663724947a3fb8a63db37048c2/";
	private static final String ZIP_FILE_NAME = "COMPELDISTI2.zip";
	private static final String FILE_NAME = "COMPELDISTI2.dbf";

	private static final String CODE_HEADER = "CODE";
	private static final String NAME_HEADER = "NAME";
	private static final String DELAY_HEADER = "срок поставки";
	private static final String QTY_HEADER = "QTY";
	private static final String MIN_QTY_HEADER = "MOQ";
	private static final String PRICE_HEADER = "PRICE_%d";
	private static final String PRICE_QTY_HEADER = "QTY_%d";
	private static final String VENDOR_HEADER = "PRODUCER";
	private static final String NAME_EXTRA_HEADER = "CLASS_NAME";
	//private static final String UNIT_HEADER = "единица измерения";
	private static final String CODE_PREFIX = "cmp-";


	@Override
	protected boolean makePreparations() throws Exception {
		shop = ItemQuery.loadSingleItemByParamValue("shop", NAME, SHOP_NAME);
		if(shop == null){
			info.addError(new Exception("No shop \"compel.ru\""));
			return false;
		}
		ArrayList<Item> currencies = ItemQuery.loadByParentId(shop.getId(), new Byte[]{ItemTypeRegistry.getAssocId("general")});
		if(currencies.size() == 0){
			info.addError(new Exception("No currency selected for shop \"compel.ru\""));
			return false;
		}
		if(currencies.size() > 1){
			info.addError(new Exception("More than one currency selected for shop \"compel.ru\""));
			return false;
		}
		currency = currencies.get(0);
		ArrayList<Item> catalogs = ItemQuery.loadByParentId(shop.getId(), new Byte[]{ItemTypeRegistry.getPrimaryAssoc().getId()});
		for(Item c : catalogs){
			if(c.getTypeId() == ItemTypeRegistry.getItemTypeId("shop_catalog") && c.getStatus() != Item.STATUS_DELETED) {
				catalog = c;
				break;
			}
		}
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		if(!downloadZip()) return;

		File compelFile = decompress();
		if(compelFile == null || !compelFile.isFile()){
			info.addError("Некорректный файл каталога", -1, -1);
			return;
		}

		if(catalog != null) {
			setOperation("Удаление старого каталога");
			executeAndCommitCommandUnits(ItemStatusDBUnit.delete(catalog.getId()).ignoreUser(true).noFulltextIndex());
		}

		setOperation("Создание нового каталога");
		catalog = ItemUtils.newChildItem("shop_catalog", shop);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).ignoreUser().noFulltextIndex());
		info.setProcessed(0);

		setOperation("Заполнение каталога");
		Charset charset = Charset.forName(CHARSET);
		try (DBFReader reader = new DBFReader(new FileInputStream(compelFile), charset)){
			DBFRow row;
			int n = 0;
			while ((row = reader.nextRow()) != null){
				processRow(row);
				info.increaseProcessed();
				n++;
				info.setLineNumber(n);
			}
		}catch (Exception e){
			throw e;
		}
		info.pushLog("Создание товаров завершено");
		info.setOperation("Индексация названий товаров");
		info.indexsationStarted();
		LuceneIndexMapper.getSingleton().reindexAll();
	}

	private void processRow(DBFRow row) throws Exception {
		Item product = ItemUtils.newChildItem(PRODUCT_ITEM, catalog);
		String code = CODE_PREFIX + row.getString(CODE_HEADER);
		String name = row.getString(NAME_HEADER);
		String qty = row.getString(QTY_HEADER);
		String minQty = row.getString(MIN_QTY_HEADER);
		String nameExtra = row.getString(NAME_EXTRA_HEADER);
		String vendor = row.getString(VENDOR_HEADER);
		String price = row.getString(String.format(PRICE_HEADER,1));

		Item oldProduct = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM,CODE_PARAM, code, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
		if(oldProduct != null){
			executeAndCommitCommandUnits(ItemStatusDBUnit.delete(oldProduct.getId()).ignoreUser(true).noFulltextIndex());
		}

		product.setValueUI(CODE_PARAM, code);
		product.setValueUI(NAME_PARAM, name);
		product.setValueUI(QTY_PARAM, qty);
		product.setValueUI(MIN_QTY_PARAM, minQty);
		product.setValueUI(NAME_EXTRA_PARAM, nameExtra);
		product.setValueUI("search", name+" "+nameExtra+" "+code);
		product.setValueUI(VENDOR_PARAM, vendor);
		BigDecimal bynPrice = convertToByn(price);
		if(bynPrice.compareTo(BigDecimal.ZERO) > 0){
			product.setValue(PRICE_PARAM, bynPrice);
		}

		product.setValue(QTY_PARAM, product.getDoubleValue(QTY_PARAM, 0d));
		product.setValue(MIN_QTY_PARAM, product.getDoubleValue(MIN_QTY_PARAM, 1d));
		product.setValueUI(TAG_PARAM, "external_shop");
		product.setValueUI(TAG_PARAM, SHOP_NAME);
		product.setValueUI(PRICE_ORIGINAL_PARAM, price);
		String keyUnique = Strings.translit(code+" "+name);
		product.setKeyUnique(keyUnique);


		for(int i=1; i<6; i++){
			String p = row.getString(String.format(PRICE_HEADER, i));
			String q = row.getString(String.format(PRICE_QTY_HEADER, i));
			if(isValidPriceAndQuantity(p, q)){
				product.setValue("spec_price", convertToByn(p));
				product.setValueUI("spec_qty", q);
			}
		}
		executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex().noTriggerExtra());
	}

	private BigDecimal convertToByn(String price) {
		if(StringUtils.isBlank(price)) return  BigDecimal.ZERO;
		BigDecimal originalPrice = DecimalDataType.parse(price, DecimalDataType.CURRENCY_PRECISE);

		BigDecimal scale =  new BigDecimal(currency.getIntValue("scale",1));
		BigDecimal currencyRatio = new BigDecimal(currency.getDoubleValue("ratio"));
		BigDecimal currencyQ = new BigDecimal(1 + currency.getDoubleValue("q",0));
		BigDecimal shopQ = new BigDecimal(1 + shop.getDoubleValue("q", 0));

		BigDecimal bynPrice = originalPrice.multiply(currencyRatio).multiply(currencyQ).multiply(shopQ).divide(scale);
		bynPrice.setScale(DecimalDataType.CURRENCY, RoundingMode.HALF_EVEN);
		return bynPrice;
	}



	private boolean isValidPriceAndQuantity(String price, String quantity){
		if(StringUtils.isBlank(price) || StringUtils.isBlank(quantity)) return false;
		return DecimalDataType.parse(price, DecimalDataType.CURRENCY_PRECISE).compareTo(BigDecimal.ZERO) > 0 && DecimalDataType.parse(quantity, DecimalDataType.CURRENCY_PRECISE).compareTo(BigDecimal.ZERO) > 0;
	}

	private boolean downloadZip(){
		try{
			WebClient.saveFile(DOWNLOAD_URL, AppContext.getRealPath(INTEGRATION_DIR), ZIP_FILE_NAME);
		} catch (Exception e){
			ServerLogger.error("Не удлось скачать каталог compel.ru");
			info.addError("Не удалось скачать каталог compel.ru", -1,-1);
			info.setOperation("Фатальная ошибка");
			return false;
		}
		return true;
	}

	private File decompress(){
		File compelFile = new File(AppContext.getRealPath(FILE_NAME));
		try {
			File zipFile = new File(AppContext.getRealPath(INTEGRATION_DIR +'/'+ ZIP_FILE_NAME));
			if (compelFile.exists())
				FileUtils.deleteQuietly(compelFile);
			Compression.decompress(zipFile, new FileOutputStream(compelFile));
		} catch (Exception e) {
			ServerLogger.error("Не возмножно расархивировать файл", e);
			info.addError("Не возмножно расархивировать файл " + DOWNLOAD_URL, 0, 0);
			info.setOperation("Фатальная ошибка, интеграция не возможна");
			return null;
		}
		return compelFile;
	}

	@Override
	protected void terminate() throws Exception {

	}
}
