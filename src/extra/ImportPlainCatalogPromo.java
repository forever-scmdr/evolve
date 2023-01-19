package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.*;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Main_page;
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
public class ImportPlainCatalogPromo extends IntegrateBase implements ItemNames {
	private static final String INTEGRATION_DIR = "integrate_promo";

	private static final String CODE_HEADER = "код";
	private static final String NAME_HEADER = "название";
	private static final String PROMO_HEADER = "promo";

	private TableDataSource price;

	@Override
	protected boolean makePreparations() throws Exception {
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
		info.pushLog("Поиск промо-товаров");
		info.setOperation("Поиск промо-товаров");
		info.setProcessed(0);
		final CurrencyRates currencyRates = new CurrencyRates();
		Main_page main = Main_page.get(ItemUtils.ensureSingleRootItem(MAIN_PAGE, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID));
		main.clearValue(Main_page.PRODUCT);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(main).ignoreUser().noFulltextIndex().noTriggerExtra());
		for (File excel : excels) {
			if (!StringUtils.endsWithAny(excel.getName(), "xls", "xlsx", "txt"))
				continue;
			// Разбор прайс-листа
			try {
				if (StringUtils.endsWithIgnoreCase(excel.getName(), "txt")) {
					price = new TabTxtTableData(excel, StandardCharsets.UTF_16, CODE_HEADER, PROMO_HEADER);
				} else {
					price = new ExcelTableData(excel, CODE_HEADER, PROMO_HEADER);
				}
				String suffix = StringUtils.substringBeforeLast(excel.getName(), ".");
				final String codeSuffix = StringUtils.substring(suffix, 0, 5);
				TableDataRowProcessor proc = src -> {
					String code = null;
					try {
						code = src.getValue(CODE_HEADER);
						if (StringUtils.isNotBlank(code)) {
							code += codeSuffix;
							String promo = src.getValue(PROMO_HEADER);
							if (StringUtils.isNotBlank(promo)) {
								main.add_product(code);
							}
							info.increaseProcessed();
						}
					} catch (Exception e) {
						ServerLogger.error("line process error", e);
						info.addError("Ошибка формата строки (" + code + ")", src.getRowNum(), 0);
					}
				};
				price.iterate(proc);
				price.close();
				executeAndCommitCommandUnits(SaveItemDBUnit.get(main).ignoreUser().noFulltextIndex().noTriggerExtra());
			} catch (Exception e) {
				ServerLogger.error("File parse error", e);
				info.addError("Ошибка формата файла", excel.getName());
			}
		}

		executeAndCommitCommandUnits(SaveItemDBUnit.get(main).ignoreUser().noFulltextIndex().noTriggerExtra());
		info.pushLog("Поиск промо-товаров завершен");
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
