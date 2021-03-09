package extra.belchip;

import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.Purchase;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class OrderManageCommand extends Command {

	public static enum orderStatus{NEW, WAITING, PACKING, PACKED, DELIVERED, COMPLETE, CANCELLED};


	
	@Override
	public ResultPE execute() throws Exception {
		String payed = StringUtils.isBlank(getVarSingleValue("payed"))? "0" : "1";
		String status = getVarSingleValue("status");
		long id = Long.parseLong(getVarSingleValue("sel"));
		Item order = ItemQuery.loadById(id);
		order.setValueUI(Purchase.PAYED, payed);
		if(StringUtils.isNotBlank(status) && !order.outputValues(Purchase.STATUS).get(0).equals(status)) {
			order.setValueUI(Purchase.STATUS, status);
			order.setValueUI("status_log", status);
			order.setValue("status_date", new Date().getTime());
		}
		executeAndCommitCommandUnits(SaveItemDBUnit.get(order).ignoreUser(true).noFulltextIndex().ignoreFileErrors(true));
		return getResult("success");
	}


}
