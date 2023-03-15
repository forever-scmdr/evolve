package extra;

import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.UserGroupRegistry;
import ecommander.model.datatypes.DateDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.ChangeItemOwnerDBUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ManageHistoryCommand extends Command {

	private Item bought, purchase, payment;

	@Override
	public ResultPE execute() throws Exception {
		try{
		String action = getVarSingleValue("action");

		loadItems();

		if("update_bought".equals(action)){
			bought.setValueUI("status", "updated_by_dealer");

			String date =  getVarSingleValue("date");

			if(StringUtils.isBlank(date)){
				bought.clearValue("proposed_dealer_date");
			}else{
				bought.setValue("proposed_dealer_date",parseDate(date));
			}
			purchase.setValueUI("status", "updated_by_dealer");

			executeCommandUnit(SaveItemDBUnit.get(bought).ignoreUser());
			executeCommandUnit(SaveItemDBUnit.get(purchase).ignoreUser());
			commitCommandUnits();
		}
		else if("confirm_date".equals(action)){
			bought.setValueUI("status", "date_confirmed");
			bought.clearValue("proposed_dealer_date");
			purchase.setValueUI("status", "updated_by_dealer");
			executeCommandUnit(SaveItemDBUnit.get(bought).ignoreUser());
			executeCommandUnit(SaveItemDBUnit.get(purchase).ignoreUser());
			commitCommandUnits();
		}
		else if("update_payment".equals(action)){
			purchase.setValueUI("status", "updated_by_dealer");
			executeCommandUnit(SaveItemDBUnit.get(purchase).ignoreUser());

			updatePayment();

			executeCommandUnit(SaveItemDBUnit.get(payment).ignoreUser());
			commitCommandUnits();
		}
		else if("create_payment".equals(action)){
			purchase.setValueUI("status", "updated_by_dealer");
			executeCommandUnit(SaveItemDBUnit.get(purchase).ignoreUser());
			payment = Item.newChildItem(ItemTypeRegistry.getItemType("payment_stage"), purchase);

			updatePayment();

			executeCommandUnit(SaveItemDBUnit.get(payment).ignoreUser());
			commitCommandUnits();
			executeAndCommitCommandUnits(ChangeItemOwnerDBUnit.newUser(payment, getInitiator().getUserId(), UserGroupRegistry.getGroup("registered")).ignoreUser());
		}
		else if("delete".equals(action)){
			purchase.setValueUI("status", "updated_by_dealer");
			executeCommandUnit(ItemStatusDBUnit.delete(payment).ignoreUser());
			executeCommandUnit(SaveItemDBUnit.get(purchase).ignoreUser());
			commitCommandUnits();
		}
		return getResult("ajax");
		}catch (Exception e){
			ServerLogger.error(e);
			return getResult("error");
		}
	}

	private void updatePayment() throws Exception {
		String date =  getVarSingleValue("date");
		if(StringUtils.isBlank(date)){
			payment.clearValue("date");
		}
		else{
			payment.setValue("date",parseDate(date));
		}
		payment.setValueUI("sum", getVarSingleValueDefault("sum", ""));

		BigDecimal percent = payment.getDecimalValue("sum").divide(purchase.getDecimalValue("sum_discount"),2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));

		payment.setValue("percent", percent);
	}

	private Long parseDate(String date){
		final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC();
		return DateDataType.parseDate(date, DATE_FORMATTER);
	}

	private void loadItems() throws Exception {
		String boughtS = getVarSingleValue("bought");
		String purS = getVarSingleValue("purchase");
		String payS = getVarSingleValue("payment");

		if(StringUtils.isNotBlank(boughtS)){
			bought = ItemQuery.loadById(Long.parseLong(boughtS));
		}
		if(StringUtils.isNotBlank(purS)){
			purchase = ItemQuery.loadById(Long.parseLong(purS));
		}
		if(StringUtils.isNotBlank(payS)){
			payment = ItemQuery.loadById(Long.parseLong(payS));
		}
	}
}
