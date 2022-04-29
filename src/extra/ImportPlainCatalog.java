package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.SynchronousTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.ItemNames;
import extra._generated.Price_catalog;
import extra._generated.Product;
import extra.belchip.CatalogCreationHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by E on 30/11/2018.
 */
public class ImportPlainCatalog extends IntegrateBase implements ItemNames {

	String ALL_HEADERS = "Наименование;Производитель;Свободное количество;в резерве;Ед. изм.;Норма упаковки;" +
			"Интервал1;Цена1($ с НДС);Интервал2;Цена2;Интервал3;Цена3;Интервал4;Цена4;Интервал5;Цена5;Склад;" +
			"Срок доставки при отгрузке из Москвы (дн.);Срок доставки при отгрузке из Минска (дн.);Аналоги;Раздел;" +
			"Примечание;Ссылка на документацию;Ссылка на изображение;Артикул;Общее наименование;Ссылка на сайт;ТНВЭД";

	private static final String CODE_HEADER = "Артикул";                // -
	private static final String VENDOR_CODE_HEADER = "Наименование";    // +
	private static final String QTY_HEADER = "Свободное количество";    // +
	private static final String VENDOR_HEADER = "Производитель";        // +
	private static final String UNIT_HEADER = "Ед. изм.";               // +
	private static final String UNIT_EXTRA_HEADER = "Норма упаковки";   // +
	private static final String PRICE_HEADER = "Цена1($ с НДС)";        // +
	private static final String STORE_HEADER = "Склад";                 // +
	private static final String DELAY_MOSCOW_HEADER = "Срок доставки при отгрузке из Москвы (дн.)";     // +
	private static final String DELAY_MINSK_HEADER = "Срок доставки при отгрузке из Минска (дн.)";      // +
	private static final String ANALOG_HEADER = "Аналоги";              // +
	private static final String SECTION_HEADER = "Раздел";              // +
	private static final String DOC_HEADER = "Ссылка на документацию";  // +
	private static final String PIC_HEADER = "Ссылка на изображение";   // +
	private static final String NAME_HEADER = "Общее наименование";     // +


	private TableDataSource price;
	private Item section;
	private Item catalog;
	private HashMap<String, Item> priceSettings;
	private ItemType productType;
	private ItemType sectionType;
	private SynchronousTransaction transaction = new SynchronousTransaction(User.getDefaultUser());

	@Override
	protected boolean makePreparations() throws Exception {
		catalog = ItemUtils.ensureSingleRootAnonymousItem(PLAIN_CATALOG, getInitiator());
		productType = ItemTypeRegistry.getItemType(PRODUCT);
		sectionType = ItemTypeRegistry.getItemType(PLAIN_SECTION);
		priceSettings = new HashMap<>();
		for (Item settings : new ItemQuery(PRICE_CATALOG).loadItems()) {
			priceSettings.put(settings.getStringValue(price_catalog_.NAME), settings);
		}
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		final String INTEGRATION_DIR = getVarSingleValueDefault("dir", "integrate_manual");
		File integrationDir = new File(AppContext.getRealPath(INTEGRATION_DIR));
		if (!integrationDir.exists()) {
			info.addError("Не найдена директория интеграции " + INTEGRATION_DIR, "init");
			return;
		}

		// Загрузить файлы
		info.pushLog("Скачивание каталогов");
		info.setOperation("Скачивание каталогов");
		for (String name : priceSettings.keySet()) {
			Price_catalog settings = Price_catalog.get(priceSettings.get(name));
			String url = settings.get_url();
			try {
				String ext = StringUtils.substringAfterLast(url, ".");
				String fileName = StringUtils.endsWith(settings.get_name(), ext) ? settings.get_name() : settings.get_name() + '.' + ext;
				File oldFile = FileUtils.getFile( integrationDir, fileName);
				if (oldFile.exists()) {
					boolean deleted = FileUtils.deleteQuietly(oldFile);
					if (!deleted) {
						info.addError("Can't delete file " + oldFile, url);
						info.pushLog("Can't delete file " + oldFile + " Skipping");
						continue;
					}
				}
				WebClient.saveFile(url, integrationDir.getAbsolutePath(), fileName);
				info.pushLog("Скачан файл " + url);
			} catch (Exception e) {
				ServerLogger.error("Can't download url " + url, e);
				info.addError(e);
			}
		}

		// Найти все файлы
		Collection<File> excels = FileUtils.listFiles(integrationDir, null, true);
		if (excels.size() == 0) {
			info.addError("Не найдены файлы в директории " + INTEGRATION_DIR, "init");
			return;
		}
		info.setToProcess(excels.size());

		// Создание самих товаров
		info.pushLog("Создание товаров");
		info.setOperation("Создание товаров");
		info.setProcessed(0);
		final CurrencyRates currencyRates = new CurrencyRates();
		for (File excel : excels) {
			if (!StringUtils.endsWithAny(excel.getName(), "xls", "xlsx", "txt", "csv"))
				continue;
			// Загрузка раздела
			String catalogName = excel.getName();
			List<Item> sections = ItemQuery.loadByParamValue(ItemNames.PLAIN_SECTION, plain_section_.NAME, catalogName);
			for (Item sec : sections) {
				executeAndCommitCommandUnits(ItemStatusDBUnit.delete(sec));
			}
			section = Item.newChildItem(sectionType, catalog);
			section.setValue(plain_section_.NAME, excel.getName());
			section.setValue(plain_section_.DATE, DateTime.now(DateTimeZone.UTC).getMillis());
			//addDebug(section);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex().noTriggerExtra());
			// Разбор прайс-листа
			try {
				if (StringUtils.endsWithIgnoreCase(excel.getName(), "txt")) {
					price = new TabTxtTableData(excel, StandardCharsets.UTF_8, NAME_HEADER, CODE_HEADER);
				} else if (StringUtils.endsWithIgnoreCase(excel.getName(), "csv")) {
					price = new TabTxtTableData(excel, StandardCharsets.UTF_8, true, NAME_HEADER, CODE_HEADER);
				} else {
					price = new ExcelTableData(excel, NAME_HEADER, CODE_HEADER);
				}
				String suffix = StringUtils.substringBeforeLast(excel.getName(), ".");
				final String codeSuffix = StringUtils.substring(suffix, 0, 5);
				//final String codeSuffix = "";
				HashMap<String, Integer> codeMap = new HashMap<>();
				TableDataRowProcessor proc = src -> {
					String code = null;
					String originalCode = null;
					try {
						code = originalCode = src.getValue(CODE_HEADER);
						if (StringUtils.isNotBlank(code)) {
							code = codeSuffix + "_" + originalCode;
							Integer codeCount = codeMap.get(code);
							if (codeCount == null) {
								codeMap.put(code, 0);
							} else {
								codeMap.put(code, ++codeCount);
								code = codeSuffix + codeCount + "_" + originalCode;
							}

							// Создание товара
							//Product prod = Product.get(ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, product_.CODE, code));
							Product prod = null;
							if (prod == null) {
								prod = Product.get(Item.newChildItem(productType, section));
							}

							// Основные прямые параметры
							prod.set_name(src.getValue(NAME_HEADER));
							prod.set_vendor_code(src.getValue(VENDOR_CODE_HEADER));
							prod.set_vendor(src.getValue(VENDOR_HEADER));
							prod.set_unit(src.getValue(UNIT_HEADER));
							String analogs = src.getValue(ANALOG_HEADER);
							if (StringUtils.isNotBlank(analogs)) {
								for (String analog : StringUtils.split(analogs, ",")) {
									prod.add_analog_code(StringUtils.normalizeSpace(analog));
								}
							}
							prod.set_type(src.getValue(SECTION_HEADER));
							prod.add_file(src.getValue(DOC_HEADER));
							prod.add_extra_pic(src.getValue(PIC_HEADER));
							prod.set_store(src.getValue(STORE_HEADER));
							prod.set_group_id(catalogName);
							if (StringUtils.equalsIgnoreCase(prod.get_store(), "Минск")) {
								prod.set_next_delivery(src.getValue(DELAY_MINSK_HEADER));
							}
							if (StringUtils.equalsIgnoreCase(prod.get_store(), "Москва")) {
								prod.set_next_delivery(src.getValue(DELAY_MOSCOW_HEADER));
							}
							String boxQty = src.getValue(UNIT_EXTRA_HEADER);
							if (DoubleDataType.parse(boxQty) > 0.01) {
								prod.set_unit(src.getValue("упк(" + boxQty + ")"));
							}
							prod.set_qty(src.getDoubleValue(QTY_HEADER));
							prod.set_is_service((byte) 0);
							prod.set_available((byte) -1);
							prod.set_min_qty((double) 1);

							// цена
							BigDecimal extraQuotient = BigDecimal.ONE;
							if (priceSettings.containsKey(catalogName)) {
								extraQuotient = priceSettings.get(catalogName).getDecimalValue(price_catalog_.QUOTIENT, BigDecimal.ONE);
							}
							if (extraQuotient.compareTo(BigDecimal.ONE) != 0)
								currencyRates.setPrice(prod, src.getCurrencyValue(PRICE_HEADER, BigDecimal.ZERO), "USD", 2, extraQuotient);
							else
								currencyRates.setPrice(prod, src.getCurrencyValue(PRICE_HEADER, BigDecimal.ZERO), "USD", 2);

							// параметры для поиска
							// Код не использовать для поиска (для этого он устанавливается после поисковых переметров)
							CatalogCreationHandler.fillSearchParams(prod);
							prod.set_code(code);
							prod.set_extra_code(originalCode);


							// Сохранение
							transaction.executeCommandUnit(SaveItemDBUnit.get(prod).noTriggerExtra());
							transactionExecute();
							info.increaseProcessed();
						}
					} catch (Exception e) {
						ServerLogger.error("line process error", e);
						info.addError("Ошибка формата строки (" + code + ")", src.getRowNum(), 0);
					}
				};
				price.iterate(proc);
				transaction.commit();
				LuceneIndexMapper.getSingleton().commit();
				price.close();
			} catch (Exception e) {
				ServerLogger.error("File parse error", e);
				info.addError("Ошибка формата файла", excel.getName());
			}

			// Сохранить время окончания разбора
			if (priceSettings.containsKey(catalogName)) {
				Price_catalog settingCatalog = Price_catalog.get(priceSettings.get(catalogName));
				settingCatalog.set_last_updated(LocalDateTime.now().toDateTime(DateTimeZone.forOffsetHours(3)).getMillis());
				executeAndCommitCommandUnits(SaveItemDBUnit.get(settingCatalog));
			}
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

	private void transactionExecute() throws Exception {
		if (transaction.getUncommitedCount() >= 200) {
			transaction.commit();
			LuceneIndexMapper.getSingleton().commit();
		}
	}

	@Override
	protected void terminate() throws Exception {

	}
}
