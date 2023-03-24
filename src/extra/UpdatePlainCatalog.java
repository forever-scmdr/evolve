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
public class UpdatePlainCatalog extends IntegrateBase implements ItemNames {
	private static final String INTEGRATION_DIR = "integrate_manual";

	private static final String CODE_HEADER = "код";
	private static final String NAME_HEADER = "название";
	private static final String QTY_HEADER = "остаток";
	private static final String PRICE_HEADER = "цена";

	private TableDataSource price;
	private Item section;
	private ItemType sectionSettingsType;
	private Item plainCatalogSettings;
	private int count = 0;


	@Override
	protected boolean makePreparations() throws Exception {
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
		String integrationDirParam = getVarSingleValueDefault("dir", INTEGRATION_DIR);
		File integrationDir = new File(AppContext.getRealPath(integrationDirParam));
		if (!integrationDir.exists()) {
			info.addError("Не найдена директория интеграции " + INTEGRATION_DIR, "init");
			return;
		}
		Collection<File> excels = FileUtils.listFiles(integrationDir, null, true);
		if (excels.size() == 0) {
			info.addError("Не найдены файлы в директории " + INTEGRATION_DIR, "init");
			return;
		}
		info.setToProcess(excels.size());

		// Создание самих товаров
		info.pushLog("Обновление товаров");
		info.setOperation("Обновление товаров");
		info.setProcessed(0);
		final CurrencyRates currencyRates = new CurrencyRates();
		for (File excel : excels) {
			if (!StringUtils.endsWithAny(excel.getName(), "xls", "xlsx", "txt"))
				continue;
			String filePrefix = StringUtils.substringBeforeLast(excel.getName(), ".");
			// Разбор прайс-листа
			try {
				if (StringUtils.endsWithIgnoreCase(excel.getName(), "txt")) {
					price = new CharSeparatedTxtTableData(excel, StandardCharsets.UTF_16, PRICE_HEADER);
				} else {
					price = new ExcelTableData(excel, PRICE_HEADER);
				}
				// Загрузка настроек
				Item sectionSettings = new ItemQuery(PRICE_CATALOG).addParameterEqualsCriteria(Price_catalog.NAME, filePrefix).loadFirstItem();
				if (sectionSettings == null) {
					sectionSettings = Item.newChildItem(sectionSettingsType, plainCatalogSettings);
					sectionSettings.setValueUI(Price_catalog.NAME, filePrefix);
					executeAndCommitCommandUnits(SaveItemDBUnit.get(sectionSettings));
				}
				final Price_catalog settings = Price_catalog.get(sectionSettings);
				count = 0;
				TableDataRowProcessor proc = src -> {
					String code = null;
					String name = null;
					try {
						code = src.getValue(CODE_HEADER);
						name = src.getValue(NAME_HEADER);
						if (StringUtils.isNotBlank(code) || StringUtils.isNotBlank(name)) {
							Product prod = null;
							if (StringUtils.isNotBlank(code)) {
								prod = Product.get(ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, product_.CODE, code));
							} else if (StringUtils.isNotBlank(name)) {
								prod = Product.get(ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, product_.NAME, name));
							}
							if (prod == null) {
								return;
							}
							prod.set_section_name(filePrefix);
							String qtyStr = src.getValue(QTY_HEADER);
							if (StringUtils.isNotBlank(qtyStr)) {
								prod.setUI_qty(qtyStr);
								prod.set_available(prod.getDefault_qty((double) 0) > 0.01 ? (byte) 1 : (byte) 0);
							}
							BigDecimal filePrice = DecimalDataType.parse(src.getValue(PRICE_HEADER), 4);
							currencyRates.setAllPrices(prod, filePrice, settings.get_currency());
							executeCommandUnit(SaveItemDBUnit.get(prod).noFulltextIndex().noTriggerExtra());
							if (count >= 100) {
								commitCommandUnits();
								count = 0;
							}
							count++;
							info.increaseProcessed();
						}
					} catch (Exception e) {
						ServerLogger.error("line process error", e);
						info.addError("Ошибка формата строки (" + code + ", " + name + ")", src.getRowNum(), 0);
					}
				};
				price.iterate(proc);
				price.close();
				commitCommandUnits();
			} catch (Exception e) {
				ServerLogger.error("File parse error", e);
				info.addError("Ошибка формата файла", excel.getName());
			}
		}

		info.pushLog("Обновление товаров завершено");
		info.setOperation("Интеграция завершена");
	}

	@Override
	protected void terminate() throws Exception {

	}


}