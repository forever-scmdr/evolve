package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.model.*;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.Agent;
import extra._generated.ItemNames;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.File;
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


	private class Report extends ExcelPriceList {

		private String dealerCode;
		private Integer year;
		private Integer quartal;
		private Long reportMillis;

		public Report(File file, String... mandatoryCols) {
			super(file, mandatoryCols);
			String[] parts = StringUtils.split(file.getName(), '.');
			try {
				this.dealerCode = parts[0];
				this.quartal = Integer.parseInt(parts[1]);
				this.year = Integer.parseInt(parts[2]);
				DateTime reportTime = new DateTime(year, quartal * 3, 1, 0, 0, DateTimeZone.UTC);
				this.reportMillis = reportTime.plusMonths(1).getMillis();
			} catch (Exception e) {
				throw new IllegalArgumentException("Неверный формат названия файла отчета: " + file.getName());
			}
		}

		@Override
		protected void processRow() throws Exception {
			String num = getValue(NUM_HEADER);
			String organization = getValue(ORGANIZATION_HEADER);
			String plainName = AssociateSales.createOrganizationName(organization);
			String device = getValue(DEVICE_HEADER);
			String qtyStr = getValue(QTY_HEADER);
			String address = getValue(ADDRESS_HEADER);
			String city = getValue(CITY_HEADER);
			String region = getValue(REGION_HEADER);
			String country = getValue(COUNTRY_HEADER);
			String bossPosition = getValue(BOSS_POSITION_HEADER);
			String bossName = getValue(BOSS_NAME_HEADER);
			String phone = getValue(PHONE_HEADER);
			String orgEmail = getValue(EMAIL_MAIN_HEADER);
			String contactName = getValue(CONTACT_NAME_HEADER);
			String contactEmail = getValue(EMAIL_CONTACT_HEADER);
			String site = getValue(SITE_HEADER);
			String branch = getValue(BRANCH_HEADER);
			String desc = getValue(DEVICE_HEADER);
			String code = getValue(CODE_HEADER);

			Item agentItem = null;
			if (StringUtils.isNotBlank(code)) {
				agentItem = new ItemQuery(AGENT).addParameterEqualsCriteria(agent_.CODE, code).loadFirstItem();
			} else if (StringUtils.isNotBlank(normalizedName)) {
				agentItem = new ItemQuery(AGENT).addParameterEqualsCriteria(agent_.PLAIN_NAME, normalizedName).loadFirstItem();
			} else {

			}


		}

		@Override
		protected void processSheet() throws Exception {

		}

		protected String getDealerCode() {
			return dealerCode;
		}
	}

	private Collection<File> xls;
	private Item dealerCatalog;
	private Item agentCatalog;
	private Item saleCatalog;
	private ItemType dealerType;
	private ItemType agentType;
	private ItemType saleType;

	private Item currentDealer;


	@Override
	protected boolean makePreparations() throws Exception {
		File integrationDir = new File(AppContext.getRealPath(INTEGRATION_DIR));
		if (!integrationDir.exists()) {
			info.addError("Не найдена директория интеграции " + INTEGRATION_DIR, "init");
			return false;
		}
		xls = FileUtils.listFiles(integrationDir, new String[] {"xlsx", "xls"}, true);
		if (xls.size() == 0) {
			info.addError("Не найдены XML файлы в директории " + INTEGRATION_DIR, "init");
			return false;
		}
		info.setToProcess(xls.size());
		dealerCatalog = ItemUtils.ensureSingleRootItem(DEALER_CATALOG, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		agentCatalog = ItemUtils.ensureSingleRootItem(AGENT_CATALOG, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		saleCatalog = ItemUtils.ensureSingleRootItem(SALE_CATALOG, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		dealerType = ItemTypeRegistry.getItemType(DEALER);
		agentType = ItemTypeRegistry.getItemType(AGENT);
		saleType = ItemTypeRegistry.getItemType(SALE);
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		info.setOperation("Загрузка отчетов");
		info.setProcessed(0);
		info.setLineNumber(0);
		info.setToProcess(xls.size());
		info.limitLog(500);
		for (File excel : xls) {
			Report report = new Report(excel, NUM_HEADER, DEVICE_HEADER, QTY_HEADER, ORGANIZATION_HEADER);
			// Загрузить или создать дилера
			currentDealer = new ItemQuery(DEALER).addParameterEqualsCriteria(CODE_HEADER, report.getDealerCode()).loadFirstItem();
			if (currentDealer == null) {
				currentDealer = Item.newChildItem(dealerType, dealerCatalog);
				currentDealer.setValueUI(dealer_.CODE, report.getDealerCode());
				executeAndCommitCommandUnits(SaveItemDBUnit.get(currentDealer));
			}
			report.iterate();
			report.close();
		}
		info.setOperation("Загрузка отчетов завершена");

	}

	@Override
	protected void terminate() throws Exception {

	}
}
