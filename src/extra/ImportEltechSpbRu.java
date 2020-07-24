package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.*;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Price_catalog;
import extra._generated.Product;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by E on 8/6/2019.
 */
public class ImportEltechSpbRu extends IntegrateBase implements ItemNames {

	private static final String ELTECH_RU = "eltech.spb.ru";
	private static final String FTP_URL = "price.eltech.spb.ru";
	private static final String FTP_FILE_URL = "Gold.xlsx";
	private static final int FTP_PORT = 2111;
	private static final String FTP_LOGIN = "K001317";
	private static final String FTP_PASS = "Mn^hv67#jdg9qa";
	private static final String INTEGRATE_DIR = "integrate";
	private static final String ELT_FILE_NAME = "eltech.csv";
	private static final String ELT_FILE_PATH = INTEGRATE_DIR + '/' + ELT_FILE_NAME;


	private static final String CODE_HEADER = "Код номенклатуры";
	private static final String NAME_HEADER = "Артикул";
	private static final String DESCRIPTION_HEADER = "Описание";
	//private static final String DELAY_HEADER = "срок поставки";
	private static final String QTY_HEADER = "Остатки ЦС";
	//private static final String MIN_QTY_HEADER = "mpq";
	private static final String PRICE_1_HEADER = "Цена без НДС, USD";
	private static final String PRICE_2_HEADER = "Распродажа Цена без НДС USD";
	private static final String VENDOR_HEADER = "Производитель";
	//private static final String EXPORTCODE_HEADER = "Exportcode";
	//private static final String UNIT_HEADER = "единица измерения";

	private static final String ELT_CODE_SUFFIX = "_elt";

	private TableDataSource price;
	private Item section;
	private Item catalog;
	private ItemType productType;
	private ItemType sectionType;
	private Byte defaultDelay;

	@Override
	protected boolean makePreparations() throws Exception {
		catalog = ItemUtils.ensuteSingleRootAnonymousItem(PLAIN_CATALOG, getInitiator());
		productType = ItemTypeRegistry.getItemType(PRODUCT);
		sectionType = ItemTypeRegistry.getItemType(PLAIN_SECTION);
		Item eltechMeta = ItemQuery.loadSingleItemByParamValue(PRICE_CATALOG, Price_catalog.NAME, ELTECH_RU);
		if (eltechMeta == null) {
			Item catalogMeta = ItemUtils.ensureSingleRootItem(CATALOG_META, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
			Item priceCatalogs = ItemUtils.ensureSingleItem(PRICE_CATALOGS, getInitiator(), catalogMeta.getId(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
			eltechMeta = Item.newChildItem(ItemTypeRegistry.getItemType(PRICE_CATALOG), priceCatalogs);
			eltechMeta.setValue(Price_catalog.NAME, ELTECH_RU);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(eltechMeta));
		}
		defaultDelay = eltechMeta.getByteValue(Price_catalog.DEFAULT_SHIP_TIME);
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		FTPClient client = new FTPClient();
		try {
			client.connect(FTP_URL, FTP_PORT);
			client.login(FTP_LOGIN, FTP_PASS);
			client.enterLocalPassiveMode();
			client.setFileType(FTP.BINARY_FILE_TYPE);
			File localFile = new File(AppContext.getRealPath(ELT_FILE_PATH));
			if (localFile.exists())
				FileUtils.deleteQuietly(localFile);
//			localFile.createNewFile();
			OutputStream os = new BufferedOutputStream(new FileOutputStream(localFile));
			boolean success = client.retrieveFile(FTP_FILE_URL, os);
			os.close();
			if (!success) {
				ServerLogger.error("Не возмножно скачать URL " + FTP_URL + '/' + FTP_FILE_URL);
				info.addError("Не доступен URL " + FTP_URL + '/' + FTP_FILE_URL, 0, 0);
				info.setOperation("Фатальная ошибка, интеграция не возможна");
			}
		} catch (Exception e) {
			ServerLogger.error("Не возмножно скачать URL", e);
			info.addError("Не доступен URL " + FTP_URL + '/' + FTP_FILE_URL, 0, 0);
			info.setOperation("Фатальная ошибка, интеграция не возможна");
			return;
		}

		// Создание самих товаров
		info.pushLog("Создание товаров");
		info.setOperation("Создание товаров");
		info.setProcessed(0);
		final CurrencyRates currencyRates = new CurrencyRates();

		// Загрузка раздела
		section = ItemQuery.loadSingleItemByParamValue(ItemNames.PLAIN_SECTION, plain_section_.NAME, ELTECH_RU);
		if (section != null) {
			executeAndCommitCommandUnits(ItemStatusDBUnit.delete(section));
		}
		section = Item.newChildItem(sectionType, catalog);
		section.setValue(plain_section_.NAME, ELTECH_RU);
		section.setValue(plain_section_.DATE, DateTime.now(DateTimeZone.UTC).getMillis());
		executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex().noTriggerExtra());

		// Разбор прайс-листа
		try {
			price = new ExcelTableData(AppContext.getRealPath(ELT_FILE_PATH), NAME_HEADER, CODE_HEADER, PRICE_1_HEADER, PRICE_2_HEADER);
			TableDataRowProcessor proc = src -> {
				String code = null;
				try {
					code = src.getValue(CODE_HEADER);
					if (StringUtils.isNotBlank(code)) {
						code = (info.getProcessed() + 1) + "_" + code + ELT_CODE_SUFFIX;
						Product prod = Product.get(ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, product_.CODE, code));
						if (prod == null) {
							prod = Product.get(Item.newChildItem(productType, section));
							prod.set_code(code);
						}
						prod.set_name(removeQuotes(src.getValue(NAME_HEADER)));
						prod.set_description(removeQuotes(src.getValue(DESCRIPTION_HEADER)));
						String qtyStr = StringUtils.replaceChars(src.getValue(QTY_HEADER), ",.", "");
						prod.set_qty(DecimalDataType.parse(qtyStr, 0));
						prod.set_available(prod.get_qty().compareTo(new BigDecimal(0.1)) < 0 ? -1 : defaultDelay);
						prod.set_vendor(removeQuotes(src.getValue(VENDOR_HEADER)));

						BigDecimal price1 = DecimalDataType.parse(src.getValue(PRICE_1_HEADER), 4);
						BigDecimal price2 = DecimalDataType.parse(src.getValue(PRICE_2_HEADER), 4);
						BigDecimal price = price1 != null ? price1 : price2;
						if (price == null)
							return;
						BigDecimal minQty = new BigDecimal(1);
						BigDecimal quotient = getQtyQuotient(price);
						price = price.multiply(quotient).setScale(2, RoundingMode.CEILING);
						minQty = minQty.divide(quotient, RoundingMode.HALF_EVEN).setScale(0, RoundingMode.HALF_EVEN);
						String unit = quotient.compareTo(new BigDecimal(1.5)) > 0 ? "упк(" + quotient + ")" : "шт.";
						prod.set_min_qty(minQty);
						prod.set_unit(unit);
						currencyRates.setAllPrices(prod, price, "USD");
						executeAndCommitCommandUnits(SaveItemDBUnit.get(prod).noFulltextIndex().noTriggerExtra());
						info.increaseProcessed();
					}
				} catch (Exception e) {
					ServerLogger.error("line process error", e);
					info.addError("Ошибка формата строки (" + code + ")", src.getRowNum(), 0);
				}
			};
			price.iterate(proc);
			price.close();
		} catch (Exception e) {
			ServerLogger.error("File parse error", e);
			info.addError("Ошибка формата файла", ELTECH_RU);
		}


		info.pushLog("Создание товаров завершено");
//		info.pushLog("Индексация");
//		info.setOperation("Индексация");
//
//		LuceneIndexMapper.getSingleton().reindexAll();
//
//		info.pushLog("Индексация завершена");
//		info.pushLog("Интеграция успешно завершена");
		info.setOperation("Интеграция завершена");
	}

	@Override
	protected void terminate() throws Exception {

	}

	private static String removeQuotes(String str) {
		str = StringUtils.trim(str);
		if (StringUtils.startsWith(str, "\"") && StringUtils.endsWith(str, "\""))
			return str.substring(1, str.length() - 1);
		return str;
	}

	private static final BigDecimal D_0003 = new BigDecimal(0.003);
	private static final BigDecimal D_003 = new BigDecimal(0.03);
	private static final BigDecimal D_10 = new BigDecimal(10);
	private static final BigDecimal D_100 = new BigDecimal(100);

	private static BigDecimal getQtyQuotient(BigDecimal price) {
		if (price.compareTo(D_0003) < 0) {
			return D_100;
		} else if (price.compareTo(D_003) < 0) {
			return D_10;
		}
		return new BigDecimal(1);
	}
}
