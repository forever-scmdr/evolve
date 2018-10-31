package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.pages.Command;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
import ecommander.persistence.commandunits.DeleteAssocDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Sale;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by E on 4/9/2018.
 */
public class AssociateSales extends IntegrateBase implements ItemNames{

	public static final String RESET_ASSOC = "reset_assoc";

	public AssociateSales() {
	}

	public AssociateSales(Command outer) {
		super(outer);
	}

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		final String TRADE_ASSOC = "trade";
		final byte TRADE_ASSOC_ID = ItemTypeRegistry.getAssocId(TRADE_ASSOC);
		// Удалить все связи, которые были раньше
		boolean resetAssoc = Boolean.parseBoolean(getVarSingleValue(RESET_ASSOC));
		if (resetAssoc) {
			info.setOperation("Удаление связей продаж с дилерами и покупателями");
			info.setProcessed(0);
			ItemQuery saleQuery = new ItemQuery(SALE).addParameterEqualsCriteria(sale_.ASSIGNED, "1").setLimit(10);
			List<Item> sales = saleQuery.loadItems();
			while (sales.size() > 0) {
				for (Item sale : sales) {
					Item agent = new ItemQuery(AGENT).setChildId(sale.getId(), false, TRADE_ASSOC).loadFirstItem();
					Item dealer = new ItemQuery(DEALER).setChildId(sale.getId(), false, TRADE_ASSOC).loadFirstItem();
					if (agent != null)
						executeCommandUnit(new DeleteAssocDBUnit(sale, agent, TRADE_ASSOC_ID));
					if (dealer != null)
						executeAndCommitCommandUnits(new DeleteAssocDBUnit(sale, dealer, TRADE_ASSOC_ID));
					if (dealer != null && agent != null)
						executeCommandUnit(new DeleteAssocDBUnit(agent, dealer, TRADE_ASSOC_ID));
					sale.setValue(sale_.ASSIGNED, (byte) 0);
					executeCommandUnit(SaveItemDBUnit.get(sale).noFulltextIndex());
				}
				commitCommandUnits();
				info.setProcessed(info.getProcessed() + sales.size());
				sales = saleQuery.loadItems();
			}
		}

		// Создать связь контрагентов с продажами
		info.setOperation("Создание связи контрагентов и их покупок");
		info.setProcessed(0);
		ItemQuery saleQuery = new ItemQuery(SALE).addParameterEqualsCriteria(sale_.ASSIGNED, "0").setLimit(10);
		List<Item> sales = saleQuery.loadItems();
		while (sales.size() > 0) {
			for (Item saleItem : sales) {
				Sale sale = Sale.get(saleItem);
				String agentCode = sale.get_agent_code();
				Item agent = null;
				if (StringUtils.isNotBlank(agentCode)) {
					agent = new ItemQuery(AGENT).addParameterEqualsCriteria(agent_.CODE, agentCode).loadFirstItem();
				}
				if (agent == null) {
					String agentName = sale.get_agent_plain_name();
					agent = new ItemQuery(AGENT).addParameterEqualsCriteria(agent_.PLAIN_NAME, agentName).loadFirstItem();
				}
				if (agent != null) {
					executeCommandUnit(CreateAssocDBUnit.childExistsSoft(sale, agent, TRADE_ASSOC_ID));
				} else {
					info.addError("Контрагент с названием '" + sale.get_agent_plain_name() + "' не найден", "Отчеты");
				}
				String dealerCode = sale.getStringValue(sale_.DEALER_CODE);
				Item dealer = new ItemQuery(DEALER).addParameterEqualsCriteria(dealer_.CODE, dealerCode).loadFirstItem();
				if (dealer != null) {
					executeCommandUnit(CreateAssocDBUnit.childExistsSoft(sale, dealer, TRADE_ASSOC_ID));
					if (agent != null) {
						executeCommandUnit(CreateAssocDBUnit.childExistsSoft(agent, dealer, TRADE_ASSOC_ID));
					}
				} else {
					info.addError("Дилер с кодом '" + sale.get_dealer_code() + "' не найден", "Отчеты");
				}

				sale.setValue(sale_.ASSIGNED, (byte) 1);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(sale).noFulltextIndex());
				info.increaseProcessed();
			}

			sales = saleQuery.loadItems();
		}
		info.pushLog("Создание связей завершено");
	}

	@Override
	protected void terminate() throws Exception {

	}

	public static final String createOrganizationName(String organization) {
		organization = StringUtils.replaceChars(organization, "«»", "\"\"");
		if (StringUtils.contains(organization, "\"")) {
			organization = StringUtils.substringAfter(organization, "\"");
			organization = StringUtils.substringBefore(organization, "\"");
		}
		organization = StringUtils.normalizeSpace(organization);
		organization = StringUtils.lowerCase(organization, AppContext.getCurrentLocale());
		return organization;
	}
}
