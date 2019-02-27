package extra;

import ecommander.fwk.MysqlConnector;
import ecommander.model.Item;
import ecommander.model.ItemBasics;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by E on 27/2/2019.
 */
public class RemoveInvisibleCommand extends Command implements DBConstants.ItemTbl {
	@Override
	public ResultPE execute() throws Exception {
		List<ItemBasics> invisible;
		do {
			invisible = new ArrayList<>();
			TemplateQuery query = new TemplateQuery("Load inivisible");
			try (Connection conn = MysqlConnector.getConnection()) {
				invisible = ItemMapper.loadStatusItemBasics(Item.STATUS_HIDDEN, 50, conn);
			}
			for (ItemBasics itemBasics : invisible) {
				DelayedTransaction.executeSingle(getInitiator(), ItemStatusDBUnit.delete(itemBasics));
			}
		} while (invisible.size() > 0);
		return null;
	}
}
