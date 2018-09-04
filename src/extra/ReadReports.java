package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Product;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Разбор файла с ценой
 * Created by E on 1/3/2018.
 */
public class ReadReports extends IntegrateBase implements ItemNames {
	private static final String INTEGRATION_DIR = "reports";
	private static final String ARCHIVE_DIR = "_archive";

	private static final String NUM_HEADER = "№";
	private static final String ORGANIZATION_HEADER = "Организация";
	private static final String DEVICE_HEADER = "Марка изделия";
	private static final String QTY_HEADER = "Количество";
	private static final String ADDRESS_HEADER = "Адрес";
	private static final String CITY_HEADER = "Город";
	private static final String REGION_HEADER = "Область";
	private static final String COUNTRY_HEADER = "Страна";
	private static final String BOSS_POSITION_HEADER = "Должность руководителя";
	private static final String BOSS_NAME_HEADER = "Имя руководителя";
	private static final String PHONE_HEADER = "Телефон";
	private static final String EMAIL_MAIN_HEADER = "e-mail организации";
	private static final String CONTACT_NAME_HEADER = "Контактное лицо";
	private static final String EMAIL_CONTACT_HEADER = "e-mail контактного лица";
	private static final String SITE_HEADER = "Сайт";
	private static final String BRANCH_HEADER = "Род деятельности";
	private static final String DESC_HEADER = "Примечание";
	private static final String CODE_HEADER = "Код";




	private ArrayList<ExcelPriceList> reports;

	@Override
	protected boolean makePreparations() throws Exception {
		File integrationDir = new File(AppContext.getRealPath(INTEGRATION_DIR));
		if (!integrationDir.exists()) {
			info.addError("Не найдена директория интеграции " + INTEGRATION_DIR, "init");
			return false;
		}
		Collection<File> xls = FileUtils.listFiles(integrationDir, new String[] {"xlsx", "xls"}, true);
		if (xls.size() == 0) {
			info.addError("Не найдены XML файлы в директории " + INTEGRATION_DIR, "init");
			return false;
		}
		info.setToProcess(xls.size());
		reports = new ArrayList<>();
		for (File excel : xls) {
			reports.add(new ExcelPriceList(excel, NUM_HEADER, DEVICE_HEADER, QTY_HEADER, ORGANIZATION_HEADER) {
				@Override
				protected void processRow() throws Exception {
					String code = StringUtils.replace(getValue(0), " ", "");
					if (StringUtils.isNotBlank(code)) {
						Product prod = Product.get(ItemQuery.loadSingleItemByParamValue(PRODUCT, product_.CODE, code));
						if (prod != null) {
							String priceOld = getValue(1);
							String priceNew = getValue(2);
							boolean available = StringUtils.contains(getValue(3), "+");
							if (StringUtils.isNotBlank(priceNew)) {
								prod.setValueUI(product_.PRICE, priceNew);
								prod.setValueUI("price_old", priceOld);
							} else {
								prod.setValueUI(product_.PRICE, priceOld);
							}
							prod.setValue("available", available ? (byte)1 : (byte)0);
							DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(prod).noFulltextIndex().ingoreComputed());
							info.increaseProcessed();
						} else {
							info.increaseLineNumber();
							info.pushLog("Товар с кодом {} и названием {} не найден в каталоге", code, getValue(1));
						}
					}
				}

				@Override
				protected void processSheet() throws Exception {

				}
			});
		}

		return true;
	}

	@Override
	protected void integrate() throws Exception {
		info.setOperation("Загрузка отчетов");
		info.setProcessed(0);
		info.setLineNumber(0);
		info.setToProcess(reports.size());
		info.limitLog(500);
		for (ExcelPriceList report : reports) {
			report.iterate();
			report.close();
		}
		info.setOperation("Загрузка отчетов завершена");

	}

	@Override
	protected void terminate() throws Exception {

	}
}
