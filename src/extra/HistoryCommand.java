package extra;

import ecommander.controllers.SessionContext;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.common.SynchronousTransaction;

import java.util.List;

/**
 * Created by user on 04.05.2018.
 */
public class HistoryCommand extends Command {
	@Override
	public ResultPE execute() throws Exception {
		return null;
	}

	public static void saveOrder(SessionContext sessionContext){
		SynchronousTransaction transaction = new SynchronousTransaction(sessionContext.getUser());
	}
}
