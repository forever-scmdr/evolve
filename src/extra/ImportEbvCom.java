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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by E on 8/6/2019.
 */
public class ImportEbvCom extends IntegrateBase implements ItemNames {

	private static final String EBV_COM = "ebv.com";
	private static final String EBV_URL = "https://tech.ebv.com/FREEINVENTORY/6611217Nurgudax/ebvstock.csv";
	private static final String INTEGRATE_DIR = "integrate";
	private static final String EBV_FILE_NAME = "ebvstock.csv";
	private static final String EBV_FILE_PATH = INTEGRATE_DIR + '/' + EBV_FILE_NAME;
	private static final String EBV_CODES_FILE_NAME = "ebv.codes.txt";
	private static final String EBV_CODES_FILE_PATH = INTEGRATE_DIR + '/' + EBV_CODES_FILE_NAME;

	private static final String CODE_HEADER = "article_man";
	private static final String NAME_HEADER = "article";
	private static final String DESCRIPTION_HEADER = "TechDescr";
	//private static final String DELAY_HEADER = "срок поставки";
	private static final String QTY_HEADER = "stock";
	private static final String MIN_QTY_HEADER = "mpq";
	private static final String PRICE_HEADER = "price";
	private static final String VENDOR_HEADER = "manuf";
	private static final String EXPORTCODE_HEADER = "Exportcode";
	//private static final String UNIT_HEADER = "единица измерения";

	private static final String EBV_CODE_SUFFIX = "ebv";

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
		Item ebvMeta = ItemQuery.loadSingleItemByParamValue(PRICE_CATALOG, Price_catalog.NAME, EBV_COM);
		if (ebvMeta == null) {
			Item catalogMeta = ItemUtils.ensureSingleRootItem(CATALOG_META, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
			Item priceCatalogs = ItemUtils.ensureSingleItem(PRICE_CATALOGS, getInitiator(), catalogMeta.getId(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
			ebvMeta = Item.newChildItem(ItemTypeRegistry.getItemType(PRICE_CATALOG), priceCatalogs);
			ebvMeta.setValue(Price_catalog.NAME, EBV_COM);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(ebvMeta));
		}
		defaultDelay = ebvMeta.getByteValue(Price_catalog.DEFAULT_SHIP_TIME);
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		try (WebClient client = WebClient.startClient()) {
			client.saveFileSession(EBV_URL, AppContext.getRealPath(INTEGRATE_DIR), EBV_FILE_NAME, StandardCharsets.ISO_8859_1);
		} catch (Exception e) {
			ServerLogger.error("Не возмножно скачать URL", e);
			info.addError("Не доступен URL " + EBV_URL, 0, 0);
			info.setOperation("Фатальная ошибка, интеграция не возможна");
			return;
		}

		// Создание самих товаров
		info.pushLog("Создание товаров");
		info.setOperation("Создание товаров");
		info.setProcessed(0);
		final CurrencyRates currencyRates = new CurrencyRates();

		// Загрузка раздела
		List<Item> sections = ItemQuery.loadByParamValue(ItemNames.PLAIN_SECTION, plain_section_.NAME, EBV_COM);
		for (Item sec : sections) {
			executeAndCommitCommandUnits(ItemStatusDBUnit.delete(sec));
		}
		section = Item.newChildItem(sectionType, catalog);
		section.setValue(plain_section_.NAME, EBV_COM);
		section.setValue(plain_section_.DATE, DateTime.now(DateTimeZone.UTC).getMillis());
		executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex().noTriggerExtra());

		// Проверка файла нужных артикулов товаров
		final HashSet<String> codes = new HashSet<>();
		try {
			File codesFile = new File(AppContext.getRealPath(EBV_CODES_FILE_PATH));
			if (codesFile.exists()) {
				codes.add("mock_entry");
				String codesStr = FileUtils.readFileToString(codesFile, StandardCharsets.ISO_8859_1);
				String[] array = StringUtils.split(codesStr, "\r\n\t,; ");
				Collections.addAll(codes, array);
			}
		} catch (Exception e) {
			info.addError("Ошибка чтения файла кодов товаров", e.getLocalizedMessage());
		}

		// Разбор прайс-листа
		try {
			price = new TabTxtTableData(AppContext.getRealPath(EBV_FILE_PATH), StandardCharsets.UTF_8, true, NAME_HEADER, CODE_HEADER, PRICE_HEADER);
			TableDataRowProcessor proc = src -> {
				String code = null;
				try {
					code = src.getValue(CODE_HEADER);
					if (StringUtils.isNotBlank(code)) {
						String exportCode = src.getValue(EXPORTCODE_HEADER);
						if (StringUtils.isNotBlank(exportCode) && codes.size() > 0 && !codes.contains(exportCode))
							return;
						code += EBV_CODE_SUFFIX;
						Product prod = Product.get(ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, product_.CODE, code));
						if (prod == null) {
							prod = Product.get(Item.newChildItem(productType, section));
							prod.set_code(code);
						}
						prod.set_name(removeQuotes(src.getValue(NAME_HEADER)));
						prod.set_description(removeQuotes(src.getValue(DESCRIPTION_HEADER)));
						prod.set_qty(src.getCurrencyValue(QTY_HEADER, new BigDecimal(0)));
						prod.set_available(prod.get_qty().compareTo(new BigDecimal(0.1)) < 0 ? -1 : defaultDelay);
						prod.set_vendor(removeQuotes(src.getValue(VENDOR_HEADER)));

						BigDecimal price = DecimalDataType.parse(src.getValue(PRICE_HEADER), 4);
						BigDecimal minQty = src.getCurrencyValue(MIN_QTY_HEADER, new BigDecimal(1));
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
			info.addError("Ошибка формата файла", EBV_COM);
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
