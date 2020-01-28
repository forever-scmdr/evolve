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
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by E on 8/6/2019.
 */
public class ImportPlatanRu extends IntegrateBase implements ItemNames {

	private static final String PLATAN_LOGIN_INPUT = "login";
	private static final String PLATAN_PASSWORD_INPUT = "pass";
	private static final String PLATAN_LOGIN = "796491";
	private static final String PLATAN_PASSWORD = "2098045";

	private static final String PLATAN_RU = "platan.ru";
	private static final String PLATAN_RU_AUTH_URL = "https://platan.ru/cgi-bin/auth.pl";
	private static final String PLATAN_RU_SHOP_URL = "https://platan.ru/shop/";
	private static final String PLATAN_RU_URL = "https://platan.ru/shop/price_new.csv";
	private static final String INTEGRATE_DIR = "integrate";
	private static final String PLATAN_FILE_NAME = "platan.ru.txt";
	private static final String PLATAN_FILE_PATH = INTEGRATE_DIR + '/' + PLATAN_FILE_NAME;

	private static final String CODE_HEADER = "номенклатурный номер";
	private static final String NAME_HEADER = "part number";
	private static final String DESCRIPTION_HEADER = "название";
	//private static final String DELAY_HEADER = "срок поставки";
	private static final String QTY_HEADER = "наличие на складе";
	private static final String MIN_QTY_HEADER = "мин. заказ";
	private static final String PRICE_HEADER = "цена2";
	private static final String VENDOR_HEADER = "производитель";
	private static final String NAME_EXTRA_HEADER = "Описание";
	private static final String UNIT_HEADER = "единица измерения";

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
		Item platanMeta = ItemQuery.loadSingleItemByParamValue(PRICE_CATALOG, Price_catalog.NAME, PLATAN_RU);
		if (platanMeta == null) {
			Item catalogMeta = ItemUtils.ensureSingleRootItem(CATALOG_META, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
			Item priceCatalogs = ItemUtils.ensureSingleItem(PRICE_CATALOGS, getInitiator(), catalogMeta.getId(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
			platanMeta = Item.newChildItem(ItemTypeRegistry.getItemType(PRICE_CATALOG), priceCatalogs);
			platanMeta.setValue(Price_catalog.NAME, PLATAN_RU);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(platanMeta));
		}
		defaultDelay = platanMeta.getByteValue(Price_catalog.DEFAULT_SHIP_TIME);
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		try (WebClient client = WebClient.startClient()) {
			String authContents = client.postStringSession(PLATAN_RU_AUTH_URL, PLATAN_LOGIN_INPUT, PLATAN_LOGIN, PLATAN_PASSWORD_INPUT, PLATAN_PASSWORD);
			client.saveFileSession(PLATAN_RU_URL, AppContext.getRealPath(INTEGRATE_DIR), PLATAN_FILE_NAME, Charset.forName("Cp1251"));
			ServerLogger.debug(authContents);
		} catch (Exception e) {
			ServerLogger.error("Не возмножно скачать URL", e);
			info.addError("Не доступен URL " + PLATAN_RU, 0, 0);
			info.setOperation("Фатальная ошибка, интеграция не возможна");
			return;
		}

		// Создание самих товаров
		info.pushLog("Создание товаров");
		info.setOperation("Создание товаров");
		info.setProcessed(0);
		final CurrencyRates currencyRates = new CurrencyRates();

		// Загрузка раздела
		section = ItemQuery.loadSingleItemByParamValue(ItemNames.PLAIN_SECTION, ItemNames.plain_section_.NAME, PLATAN_RU);
		if (section != null) {
			executeAndCommitCommandUnits(ItemStatusDBUnit.delete(section));
		}
		section = Item.newChildItem(sectionType, catalog);
		section.setValue(ItemNames.plain_section_.NAME, PLATAN_RU);
		section.setValue(ItemNames.plain_section_.DATE, DateTime.now(DateTimeZone.UTC).getMillis());
		executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex().noTriggerExtra());
		// Разбор прайс-листа
		try {
			price = new TabTxtTableData(AppContext.getRealPath(PLATAN_FILE_PATH), StandardCharsets.UTF_8, true, NAME_HEADER, CODE_HEADER);
			TableDataRowProcessor proc = src -> {
				String code = null;
				try {
					code = src.getValue(CODE_HEADER);
					if (StringUtils.isNotBlank(code)) {
						Product prod = Product.get(ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, ItemNames.product_.CODE, code));
						if (prod == null) {
							prod = Product.get(Item.newChildItem(productType, section));
							prod.set_code(code);
						}
						prod.set_name(removeQuotes(src.getValue(NAME_HEADER)));
						prod.set_description(removeQuotes(src.getValue(DESCRIPTION_HEADER)));
						prod.set_available(defaultDelay);
						prod.set_qty(src.getCurrencyValue(QTY_HEADER, new BigDecimal(0)));
						prod.set_min_qty(src.getCurrencyValue(MIN_QTY_HEADER, new BigDecimal(1)));
						BigDecimal price = DecimalDataType.parse(src.getValue(PRICE_HEADER), 2);
						currencyRates.setAllPrices(prod, price, "RUB");
						prod.set_vendor(removeQuotes(src.getValue(VENDOR_HEADER)));
						prod.set_name_extra(removeQuotes(src.getValue(NAME_EXTRA_HEADER)));
						prod.set_unit(src.getValue(UNIT_HEADER));
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
			info.addError("Ошибка формата файла", PLATAN_RU);
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
}
