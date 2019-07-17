package extra;

import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFRow;
import com.linuxense.javadbf.DBFUtils;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by E on 8/6/2019.
 */
public class ImportCompelRu extends IntegrateBase implements ItemNames {
	private static final String COMPEL_RU = "compel.ru";
	private static final String INTEGRATION_DIR = "integrate/";
	private static final String ZIP_FILE = "compel.ru.dbf.zip";
	private static final String INTEGRATION_FILE = INTEGRATION_DIR + "compel.ru.dbf";
	private static final String COMPEL_RU_URL = "http://www.compel.ru/stockfiles2/8cac72a11de2f0872faeeb69ac34b832/";

	private static final String CODE_HEADER = "CODE";
	private static final String NAME_HEADER = "NAME";
	//private static final String DELAY_HEADER = "срок поставки";
	private static final String QTY_HEADER = "QTY";
	private static final String MIN_QTY_HEADER = "MOQ";
	private static final String PRICE_HEADER = "PRICE_3";
	private static final String VENDOR_HEADER = "PRODUCER";
	private static final String NAME_EXTRA_HEADER = "CLASS_NAME";
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
		Item compelMeta = ItemQuery.loadSingleItemByParamValue(PRICE_CATALOG, Price_catalog.NAME, COMPEL_RU);
		if (compelMeta == null) {
			Item catalogMeta = ItemUtils.ensureSingleRootItem(CATALOG_META, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
			Item priceCatalogs = ItemUtils.ensureSingleItem(PRICE_CATALOGS, getInitiator(), catalogMeta.getId(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
			compelMeta = Item.newChildItem(ItemTypeRegistry.getItemType(PRICE_CATALOG), priceCatalogs);
			compelMeta.setValue(Price_catalog.NAME, COMPEL_RU);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(compelMeta));
		}
		defaultDelay = compelMeta.getByteValue(Price_catalog.DEFAULT_SHIP_TIME);
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		try {
			WebClient.saveFile(COMPEL_RU_URL, AppContext.getRealPath(INTEGRATION_DIR), ZIP_FILE);
		} catch (Exception e) {
			ServerLogger.error("Не возмножно скачать URL", e);
			info.addError("Не доступен URL " + COMPEL_RU_URL, 0, 0);
			info.setOperation("Фатальная ошибка, интеграция не возможна");
			return;
		}
		File compelFile = new File(AppContext.getRealPath(INTEGRATION_FILE));
		try {
			File zipFile = new File(AppContext.getRealPath(INTEGRATION_DIR + ZIP_FILE));
			if (compelFile.exists())
				FileUtils.deleteQuietly(compelFile);
			Compression.decompress(zipFile, new FileOutputStream(compelFile));
		} catch (Exception e) {
			ServerLogger.error("Не возмножно расархивировать файл", e);
			info.addError("Не возмножно расархивировать файл " + COMPEL_RU_URL, 0, 0);
			info.setOperation("Фатальная ошибка, интеграция не возможна");
			return;
		}

		// Создание самих товаров
		info.pushLog("Создание товаров");
		info.setOperation("Создание товаров");
		info.setProcessed(0);
		final CurrencyRates currencyRates = new CurrencyRates();

		// Загрузка раздела
		section = ItemQuery.loadSingleItemByParamValue(ItemNames.PLAIN_SECTION, plain_section_.NAME, COMPEL_RU);
		if (section != null) {
			executeAndCommitCommandUnits(ItemStatusDBUnit.delete(section));
		}
		section = Item.newChildItem(sectionType, catalog);
		section.setValue(plain_section_.NAME, COMPEL_RU);
		section.setValue(plain_section_.DATE, DateTime.now(DateTimeZone.UTC).getMillis());
		executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex().noTriggerExtra());
		// Разбор прайс-листа
		DBFReader reader = null;
		try {
			Charset stringCharset = Charset.forName("Cp1251");
			reader = new DBFReader(new FileInputStream(compelFile), stringCharset);

			DBFRow row;
			while ((row = reader.nextRow()) != null) {
				String code = row.getString(CODE_HEADER);
				if (StringUtils.isNotBlank(code)) {
					Product prod = Product.get(ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, product_.CODE, code));
					if (prod == null) {
						prod = Product.get(Item.newChildItem(productType, section));
						prod.set_code(code);
					}
					prod.set_name(row.getString(NAME_HEADER));
					prod.set_available(defaultDelay);
					prod.setUI_qty(row.getString(QTY_HEADER));
					prod.setUI_min_qty(row.getString(MIN_QTY_HEADER));
					BigDecimal price = DecimalDataType.parse(row.getString(PRICE_HEADER), 2);
					currencyRates.setAllPrices(prod, price, "USD");
					prod.set_vendor(row.getString(VENDOR_HEADER));
					prod.set_name_extra(row.getString(NAME_EXTRA_HEADER));
					prod.set_unit("шт.");
					executeAndCommitCommandUnits(SaveItemDBUnit.get(prod).noFulltextIndex().noTriggerExtra());
					info.increaseProcessed();
				}
			}
		} catch (Exception e) {
			ServerLogger.error("File parse error", e);
			info.addError("Ошибка формата файла", COMPEL_RU);
		} finally {
			DBFUtils.close(reader);
		}


		info.pushLog("Создание товаров завершено");
		info.setOperation("Интеграция завершена");
	}

	@Override
	protected void terminate() throws Exception {

	}
}
