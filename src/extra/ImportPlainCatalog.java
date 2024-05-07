package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import ecommander.special.portal.outer.currency.CurrencyRates;
import extra._generated.ItemNames;
import extra._generated.Price_catalog;
import extra._generated.Price_catalogs;
import extra._generated.Product;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

/**
 * Created by E on 30/11/2018.
 */
public class ImportPlainCatalog extends IntegrateBase implements ItemNames {
	private static final String INTEGRATION_DIR = "integrate_manual";

	private static final String CODE_HEADER = "код";
	private static final String NAME_HEADER = "название";
	private static final String DELAY_HEADER = "срок поставки";
	private static final String QTY_HEADER = "остаток";
	private static final String MIN_QTY_HEADER = "минимальная партия заказа";
	private static final String STEP_HEADER = "шаг заказа";
	private static final String PRICE_HEADER = "цена";
	private static final String VENDOR_HEADER = "производитель";
	private static final String NAME_EXTRA_HEADER = "описание";
	private static final String UNIT_HEADER = "единица измерения";

	private TableDataSource price;
	private Item section;
	private Item catalog;
	private ItemType productType;
	private ItemType sectionType;
	private ItemType sectionSettingsType;
	private Item plainCatalogSettings;
	private int count = 0;


	@Override
	protected boolean makePreparations() throws Exception {
		catalog = ItemUtils.ensureSingleRootAnonymousItem(PLAIN_CATALOG, getInitiator());
		productType = ItemTypeRegistry.getItemType(PRODUCT);
		sectionType = ItemTypeRegistry.getItemType(PLAIN_SECTION);
		sectionSettingsType = ItemTypeRegistry.getItemType(PRICE_CATALOG);
		plainCatalogSettings = ItemQuery.loadSingleItemByName(PRICE_CATALOGS);
		if (plainCatalogSettings == null) {
			Item catalogMeta = ItemUtils.ensureSingleRootAnonymousItem(CATALOG_META, getInitiator());
			plainCatalogSettings = ItemUtils.ensureSingleAnonymousItem(PRICE_CATALOGS, getInitiator(), catalogMeta.getId());
		}
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		File integrationDir = new File(AppContext.getRealPath(INTEGRATION_DIR));
		if (!integrationDir.exists()) {
			info.pushError("Не найдена директория интеграции " + INTEGRATION_DIR, "init");
			return;
		}
		Collection<File> excels = FileUtils.listFiles(integrationDir, null, true);
		if (excels.size() == 0) {
			info.pushError("Не найдены файлы в директории " + INTEGRATION_DIR, "init");
			return;
		}
		info.setToProcess(excels.size());
		final boolean addCodeSuffix = StringUtils.equalsIgnoreCase("да", plainCatalogSettings.getStringValue(Price_catalogs.PRODUCT_CODE_EXTRA));

		// Создание самих товаров
		info.pushLog("Создание товаров");
		info.setOperation("Создание товаров");
		info.setProcessed(0);
		final CurrencyRates currencyRates = new CurrencyRates();
		for (File excel : excels) {
			if (!StringUtils.endsWithAny(excel.getName(), "xls", "xlsx", "txt"))
				continue;
			final String filePrefix = StringUtils.substringBeforeLast(excel.getName(), ".");
			// Загрузка раздела
			List<Item> sections = ItemQuery.loadByParamValue(ItemNames.PLAIN_SECTION, plain_section_.NAME, filePrefix);
			for (Item sec : sections) {
				executeAndCommitCommandUnits(ItemStatusDBUnit.delete(sec));
			}
			section = Item.newChildItem(sectionType, catalog);
			section.setValue(plain_section_.NAME, filePrefix);
			section.setValue(plain_section_.DATE, DateTime.now(DateTimeZone.UTC).getMillis());
			addDebug(section);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex().noTriggerExtra());
			int productCount = 0;
			// Разбор прайс-листа
			try {
				if (StringUtils.endsWithIgnoreCase(excel.getName(), "txt")) {
					price = new CharSeparatedTxtTableData(excel, StandardCharsets.UTF_16, NAME_HEADER, CODE_HEADER);
				} else {
					price = new ExcelTableData(excel, NAME_HEADER, CODE_HEADER);
				}
				// Загрузка настроек
				Item sectionSettings = new ItemQuery(PRICE_CATALOG).addParameterEqualsCriteria(Price_catalog.NAME, filePrefix).loadFirstItem();
				if (sectionSettings == null) {
					sectionSettings = Item.newChildItem(sectionSettingsType, plainCatalogSettings);
					sectionSettings.setValueUI(Price_catalog.NAME, filePrefix);
					executeAndCommitCommandUnits(SaveItemDBUnit.get(sectionSettings));
				}
				final Price_catalog settings = Price_catalog.get(sectionSettings);
				// префикс (суффикс) кода товара
				final String codeSuffix = StringUtils.substring(filePrefix, 0, 5);
				count = 0;
				TableDataRowProcessor proc = src -> {
					String code = null;
					try {
						code = src.getValue(CODE_HEADER);
						if (StringUtils.isBlank(code))
							return;
						if (addCodeSuffix) {
							code = codeSuffix + code;
						}
						code = Strings.translit(code);
						Product prod = Product.get(ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, product_.CODE, code));
						if (prod != null) {
							executeCommandUnit(ItemStatusDBUnit.delete(prod));
						}
						prod = Product.get(Item.newChildItem(productType, section));
						prod.set_code(code);
						prod.set_name(src.getValue(NAME_HEADER));
						String nextDelivery = src.getValue(DELAY_HEADER);
						if (StringUtils.isBlank(nextDelivery))
							nextDelivery = settings.get_default_ship_time();
						prod.set_next_delivery(nextDelivery);
						/*
						Byte available = NumberUtils.toByte(src.getValue(DELAY_HEADER), (byte) -1);
						if (available < 0)
							available = settings.get_default_ship_time();
						prod.set_available(available);
						 */
						prod.set_qty(src.getDoubleValue(QTY_HEADER));
						prod.set_available(prod.getDefault_qty((double) 0) > 0.01 ? (byte) 1 : (byte) 0);
						BigDecimal filePrice = DecimalDataType.parse(src.getValue(PRICE_HEADER), 4);
						//BigDecimal price = filePrice.multiply(settings.get_quotient());
						currencyRates.setAllPrices(prod, filePrice, settings.getDefault_currency("RUB"));
						Double fileMinQty = src.getDoubleValue(MIN_QTY_HEADER);
						Double minQty = (fileMinQty == null || Math.abs(fileMinQty) < 0.01) ? getQtyQuotientDouble(prod.get_price()) : fileMinQty;
						prod.set_min_qty(minQty);
						Double fileStep = src.getDoubleValue(STEP_HEADER);
						Double step = (fileStep == null || Math.abs(fileStep) < 0.01) ? minQty : fileStep;
						prod.set_step(step);
						prod.set_section_name(filePrefix);
						prod.set_vendor(src.getValue(VENDOR_HEADER));
						prod.set_name_extra(src.getValue(NAME_EXTRA_HEADER));
						prod.set_description(src.getValue(NAME_EXTRA_HEADER));
						prod.set_unit(src.getValue(UNIT_HEADER));
						executeCommandUnit(SaveItemDBUnit.new_(prod, section, (count++) * 64));
						if (count >= 100) {
							commitCommandUnits();
							count = 0;
						}
						count++;
						info.increaseProcessed();
					} catch (Exception e) {
						ServerLogger.error("line process error", e);
						info.pushError("Ошибка формата строки (" + code + ")", src.getRowNum(), 0);
					}
				};
				price.iterate(proc);
				price.close();
				commitCommandUnits();
			} catch (Exception e) {
				ServerLogger.error("File parse error", e);
				info.pushError("Ошибка формата файла", excel.getName());
			}
		}

		info.pushLog("Создание товаров завершено");
		info.pushLog("Индексация");
		info.indexsationStarted();

		LuceneIndexMapper.getSingleton().reindexAll(catalog.getId());

		info.pushLog("Индексация завершена");
		info.setOperation("Интеграция завершена");
	}

	@Override
	protected void terminate() throws Exception {

	}


	private static final BigDecimal D_0003 = new BigDecimal(0.003);
	private static final BigDecimal D_003 = new BigDecimal(0.03);
	private static final BigDecimal D_10 = new BigDecimal(10);
	private static final BigDecimal D_100 = new BigDecimal(100);

	private static BigDecimal getQtyQuotient(BigDecimal price) {

		if (price != null) {
			if (price.compareTo(D_0003) < 0) {
				return D_100;
			} else if (price.compareTo(D_003) < 0) {
				return D_10;
			}
		}
		return new BigDecimal(1);
	}

	private static Double getQtyQuotientDouble(BigDecimal price) {
		if (price != null) {
			if (price.compareTo(D_0003) < 0) {
				return 100.0;
			} else if (price.compareTo(D_003) < 0) {
				return 10.0;
			}
		}
		return 1.0;
	}


}
