package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.*;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import lunacrawler._generated.ItemNames;
import lunacrawler._generated.Price_catalog;
import lunacrawler._generated.Product;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.Charset;

/**
 * Created by E on 8/6/2019.
 */
public class ImportFerriteRu extends IntegrateBase implements ItemNames {
	private static final String FERRITE_RU = "ferrite.ru";
	private static final String INTEGRATION_FILE = "integrate/ferrite.ru.txt";
	private static final String FERRITE_RU_URL = "http://ferrite.ru/export/mailsent_spacer.php";

	private static final String CODE_HEADER = "Article";
	private static final String NAME_HEADER = "Part#";
	//private static final String DELAY_HEADER = "срок поставки";
	private static final String QTY_HEADER = "Наличие";
	//private static final String MIN_QTY_HEADER = "минимальная партия заказа";
	private static final String PRICE_HEADER = "Цена 3";
	private static final String VENDOR_HEADER = "Производитель";
	private static final String NAME_EXTRA_HEADER = "Описание";
	//private static final String UNIT_HEADER = "единица измерения";

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
		Item ferriteMeta = ItemQuery.loadSingleItemByParamValue(PRICE_CATALOG, Price_catalog.NAME, FERRITE_RU);
		if (ferriteMeta == null) {
			Item catalogMeta = ItemUtils.ensureSingleRootItem(CATALOG_META, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
			Item priceCatalogs = ItemUtils.ensureSingleItem(PRICE_CATALOGS, getInitiator(), catalogMeta.getId(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
			ferriteMeta = Item.newChildItem(ItemTypeRegistry.getItemType(PRICE_CATALOG), priceCatalogs);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(ferriteMeta));
		}
		defaultDelay = ferriteMeta.getByteValue(Price_catalog.DEFAULT_SHIP_TIME);
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		String contents;
		try {
			contents = WebClient.getString(FERRITE_RU_URL);
		} catch (Exception e) {
			info.addError("Не доступен URL " + FERRITE_RU_URL, 0, 0);
			info.setOperation("Фатальная ошибка, интеграция не возможна");
			return;
		}
		File ferriteFile = new File(AppContext.getRealPath(INTEGRATION_FILE));
		if (ferriteFile.exists())
			FileUtils.deleteQuietly(ferriteFile);
		FileUtils.write(ferriteFile, contents, Charset.forName("UTF-8"));

		// Создание самих товаров
		info.pushLog("Создание товаров");
		info.setOperation("Создание товаров");
		info.setProcessed(0);
		final CurrencyRates currencyRates = new CurrencyRates();

		// Загрузка раздела
		section = ItemQuery.loadSingleItemByParamValue(ItemNames.PLAIN_SECTION, ItemNames.plain_section_.NAME, FERRITE_RU);
		if (section != null) {
			executeAndCommitCommandUnits(ItemStatusDBUnit.delete(section));
		}
		section = Item.newChildItem(sectionType, catalog);
		section.setValue(ItemNames.plain_section_.NAME, FERRITE_RU);
		section.setValue(ItemNames.plain_section_.DATE, DateTime.now(DateTimeZone.UTC).getMillis());
		executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex().noTriggerExtra());
		// Разбор прайс-листа
		try {
			price = new TabTxtTableData(ferriteFile, NAME_HEADER, CODE_HEADER);
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
						prod.set_name(src.getValue(NAME_HEADER));
						prod.set_available(defaultDelay);
						prod.set_qty(src.getCurrencyValue(QTY_HEADER, new BigDecimal(0)));
						prod.set_min_qty(new BigDecimal(1));
						//prod.set_price(src.getCurrencyValue(PRICE_HEADER, new BigDecimal(0)));
						currencyRates.setAllPrices(prod, src.getValue(PRICE_HEADER));
						prod.set_vendor(src.getValue(VENDOR_HEADER));
						prod.set_name_extra(src.getValue(NAME_EXTRA_HEADER));
						prod.set_unit("шт.");
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
			info.addError("Ошибка формата файла", FERRITE_RU);
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
}
