package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Product;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
	private static final String PRICE_HEADER = "цена";
	private static final String VENDOR_HEADER = "производитель";
	private static final String NAME_EXTRA_HEADER = "описание";
	private static final String UNIT_HEADER = "единица измерения";

	private TableDataSource price;
	private Item section;
	private Item catalog;
	private ItemType productType;
	private ItemType sectionType;

	@Override
	protected boolean makePreparations() throws Exception {
		catalog = ItemUtils.ensuteSingleRootAnonymousItem(PLAIN_CATALOG, getInitiator());
		productType = ItemTypeRegistry.getItemType(PRODUCT);
		sectionType = ItemTypeRegistry.getItemType(PLAIN_SECTION);
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		File integrationDir = new File(AppContext.getRealPath(INTEGRATION_DIR));
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
		info.pushLog("Создание товаров");
		info.setOperation("Создание товаров");
		info.setProcessed(0);
		final CurrencyRates currencyRates = new CurrencyRates();
		for (File excel : excels) {
			if (!StringUtils.endsWithAny(excel.getName(), "xls", "xlsx", "txt"))
				continue;
			// Загрузка раздела
			List<Item> sections = ItemQuery.loadByParamValue(ItemNames.PLAIN_SECTION, plain_section_.NAME, excel.getName());
			for (Item sec : sections) {
				executeAndCommitCommandUnits(ItemStatusDBUnit.delete(sec));
			}
			section = Item.newChildItem(sectionType, catalog);
			section.setValue(plain_section_.NAME, excel.getName());
			section.setValue(plain_section_.DATE, DateTime.now(DateTimeZone.UTC).getMillis());
			addDebug(section);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex().noTriggerExtra());
			// Разбор прайс-листа
			try {
				if (StringUtils.endsWithIgnoreCase(excel.getName(), "txt")) {
					price = new TabTxtTableData(excel, StandardCharsets.UTF_16, NAME_HEADER, CODE_HEADER);
				} else {
					price = new ExcelTableData(excel, NAME_HEADER, CODE_HEADER);
				}
				String suffix = StringUtils.substringBeforeLast(excel.getName(), ".");
				final String codeSuffix = StringUtils.substring(suffix, 0, 5);
				TableDataRowProcessor proc = src -> {
					String code = null;
					try {
						code = src.getValue(CODE_HEADER);
						if (StringUtils.isNotBlank(code)) {
							code += codeSuffix;
							Product prod = Product.get(ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, product_.CODE, code));
							if (prod == null) {
								prod = Product.get(Item.newChildItem(productType, section));
								prod.set_code(code);
							}
							prod.set_name(src.getValue(NAME_HEADER));
							prod.set_available(NumberUtils.toByte(src.getValue(DELAY_HEADER), (byte) 0));
							prod.set_qty(src.getCurrencyValue(QTY_HEADER, new BigDecimal(0)));
							//prod.set_price(src.getCurrencyValue(PRICE_HEADER, new BigDecimal(0)));
							currencyRates.setAllPrices(prod, src.getValue(PRICE_HEADER));
							BigDecimal fileMinQty = src.getCurrencyValue(MIN_QTY_HEADER, BigDecimal.ZERO);
							BigDecimal minQty = fileMinQty.equals(BigDecimal.ZERO) ? getQtyQuotient(currencyRates.getPrice(prod, "USD")) : fileMinQty;
							prod.set_min_qty(minQty);
							prod.set_vendor(src.getValue(VENDOR_HEADER));
							prod.set_name_extra(src.getValue(NAME_EXTRA_HEADER));
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
				info.addError("Ошибка формата файла", excel.getName());
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
}
