package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.model.*;
import ecommander.persistence.commandunits.CleanAllDeletedItemsDBUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.Agent;
import extra._generated.ItemNames;
import extra._generated.Sale;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.File;
import java.util.Collection;
import java.util.List;

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


	private static class Report extends ExcelPriceList {

		private String dealerCode;
		private Integer year;
		private Integer quartal;
		private Long reportMillis;
		private String reportCode;
		private Agent agent;
		private ReadReports command;

		public Report(File file, ReadReports command, String... mandatoryCols) {
			super(file, mandatoryCols);
			this.command = command;
			String normalName = StringUtils.replaceChars(file.getName(), " ", "");
			reportCode = StringUtils.substringBeforeLast(normalName, ".");
			String[] parts = StringUtils.split(normalName, '.');
			try {
				dealerCode = parts[0];
				quartal = Integer.parseInt(parts[1]);
				year = Integer.parseInt(parts[2]);
				DateTime reportTime = new DateTime(year, quartal * 3, 1, 0, 0, DateTimeZone.UTC);
				reportMillis = reportTime.getMillis();
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
			if (StringUtils.containsIgnoreCase(city, "г."))
				city = StringUtils.trim(StringUtils.substringAfter(city, "."));
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
			String desc = getValue(DESC_HEADER);
			String code = getValue(CODE_HEADER);

			// Создание или загрузка контрагента
			if (StringUtils.isNotBlank(num)) {
				Item agentItem;
				if (StringUtils.isNotBlank(code)) {
					agentItem = new ItemQuery(AGENT).addParameterEqualsCriteria(agent_.CODE, code).loadFirstItem();
				} else if (StringUtils.isNotBlank(plainName)) {
					agentItem = new ItemQuery(AGENT).addParameterEqualsCriteria(agent_.PLAIN_NAME, plainName).loadFirstItem();
				} else {
					info.addError(getFileName() + ": Не заданы название и код контрагента", getRowNum(), 0);
					return;
				}
				agent = Agent.get(agentItem);
				if (agent == null) {
					agentItem = Item.newChildItem(command.agentType, command.agentCatalog);
					agent = Agent.get(agentItem);
					agent.set_code(code);
				}

				// обновление (или создание) данных агента
				if (StringUtils.isNotBlank(organization))
					agent.set_organization(organization);
				if (StringUtils.isNotBlank(plainName))
					agent.set_plain_name(plainName);
				if (StringUtils.isNotBlank(address))
					agent.set_address(address);
				if (StringUtils.isNotBlank(city))
					agent.set_city(city);
				if (StringUtils.isNotBlank(region))
					agent.set_region(region);
				if (StringUtils.isNotBlank(country))
					agent.set_country(country);
				if (StringUtils.isNotBlank(bossPosition))
					agent.set_boss_position(bossPosition);
				if (StringUtils.isNotBlank(bossName))
					agent.set_boss_name(bossName);
				if (StringUtils.isNotBlank(phone))
					agent.set_phone(phone);
				if (StringUtils.isNotBlank(orgEmail))
					agent.set_email_main(orgEmail);
				if (StringUtils.isNotBlank(contactName))
					agent.set_contact_name(contactName);
				if (StringUtils.isNotBlank(contactEmail))
					agent.set_email_contact(contactEmail);
				if (StringUtils.isNotBlank(site))
					agent.set_site(site);
				if (StringUtils.isNotBlank(branch))
					agent.set_branch(branch);
				if (StringUtils.isNotBlank(desc))
					agent.set_desc(desc);
				command.executeAndCommitCommandUnits(SaveItemDBUnit.get(agent));
			}

			// Добавление продажи (если она еще не добавлена ранее)
			if (StringUtils.isNotBlank(device)) {
				Sale sale = Sale.get(Item.newChildItem(command.saleType, command.saleCatalog));
				sale.set_report(reportCode);
				sale.set_device(device);
				sale.setUI_qty(qtyStr);
				sale.set_year(year);
				sale.set_quartal(quartal);
				sale.set_register_date(reportMillis);
				sale.set_agent_code(agent.get_code());
				sale.set_agent_name(agent.get_organization());
				sale.set_agent_plain_name(agent.get_plain_name());
				sale.set_dealer_code(dealerCode);
				command.executeAndCommitCommandUnits(SaveItemDBUnit.get(sale).noFulltextIndex());

				info.increaseProcessed();
			}
		}

		@Override
		protected void processSheet() throws Exception {

		}

		protected String getDealerCode() {
			return dealerCode;
		}

		protected String getReportCode() {
			return reportCode;
		}
	}

	private Collection<File> xls;
	private Item dealerCatalog;
	private Item agentCatalog;
	private Item saleCatalog;
	private ItemType dealerType;
	private ItemType agentType;
	private ItemType saleType;


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
		File archiveDir = new File(AppContext.getRealPath(ARCHIVE_DIR));
		for (File excel : xls) {
			Report report = new Report(excel, this, NUM_HEADER, DEVICE_HEADER, QTY_HEADER, ORGANIZATION_HEADER);
			// Загрузить или создать дилера
			Item dealerItem = new ItemQuery(DEALER).addParameterEqualsCriteria(dealer_.CODE, report.getDealerCode()).loadFirstItem();
			if (dealerItem == null) {
				dealerItem = Item.newChildItem(dealerType, dealerCatalog);
				dealerItem.setValueUI(dealer_.CODE, report.getDealerCode());
				dealerItem.setValueUI(dealer_.ORGANIZATION, report.getDealerCode());
				executeAndCommitCommandUnits(SaveItemDBUnit.get(dealerItem));
			}
			// Удалить продажи более ранней версии отчета
			List<Item> reportSales = new ItemQuery(SALE).addParameterEqualsCriteria(sale_.REPORT, report.getReportCode()).loadItems();
			for (Item reportSale : reportSales) {
				executeAndCommitCommandUnits(ItemStatusDBUnit.delete(reportSale));
			}
			report.iterate();
			report.close();
			executeAndCommitCommandUnits(new CleanAllDeletedItemsDBUnit(100, null));
			// Заархивировать файл - переместить в директорию _archive
			FileUtils.copyFileToDirectory(excel, archiveDir);
			FileUtils.deleteQuietly(excel);
		}
		info.addLog("Загрузка отчетов завершена");

		new AssociateSales(this).integrate();
		new TagSales(this).integrate();
	}

	@Override
	protected void terminate() throws Exception {

	}
}
