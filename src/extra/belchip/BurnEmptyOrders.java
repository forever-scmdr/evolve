package extra.belchip;

import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

import java.util.List;

public class BurnEmptyOrders extends IntegrateBase {

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		ItemQuery q = new ItemQuery("order");
		//q.addParameterCriteria("status", "0", "=", null, COMPARE_TYPE.SOME);

		int i= 0;
		int processed = 0;
		List<Item> megaShit = q.loadItems();
		info.setToProcess(megaShit.size());
		for(Item order : megaShit) {
				executeCommandUnit(ItemStatusDBUnit.delete(order).ignoreUser(true).noFulltextIndex());
				i++;
			if(i > 49) {
				processed += i;
				commitCommandUnits();
				i=0;
				info.setProcessed(processed);
			}
		}
		processed += i;
		commitCommandUnits();
		info.setProcessed(processed);
	}

	@Override
	protected void terminate() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
