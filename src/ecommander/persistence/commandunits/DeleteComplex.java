package ecommander.persistence.commandunits;

import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Планировщик с потоком для фонового физического удаления айтемов из БД (помеченных удаленными)
 * Created by E on 6/3/2019.
 */
public class DeleteComplex implements DBConstants.ItemTbl {

	private static final int AVERAGE = 50000;
	private static final int MANY = 500000;

	private static final int MAX_DELETE_SECONDS  = 60;

	private static final int FREQUENT_SECONDS = 120;
	private static final int NORMAL_SECONDS = 1200;
	private static final int RARE_SECONDS = 7200;

	private class DeleteThread implements Runnable {

		@Override
		public void run() {
			try {
				DelayedTransaction.executeSingle(User.getDefaultUser(),
						new PerformDeletedCleaningDBUnit(8, MAX_DELETE_SECONDS).ignoreUser());
				TemplateQuery countQuery = new TemplateQuery("Count query");
				countQuery.SELECT("count(*)").FROM(ITEM_TBL).WHERE().col(I_STATUS).byte_(Item.STATUS_DELETED);
				try (Connection conn = MysqlConnector.getConnection();
				     PreparedStatement pstmt = countQuery.prepareQuery(conn)) {
					ResultSet rs = pstmt.executeQuery();
					if (rs.next())
						remaining = rs.getInt(1);
				} catch (Exception e) {
					ServerLogger.error("Unable to schedule deletion", e);
				}
			} catch (Exception e) {
				ServerLogger.error("Unable to perform deletion", e);
			} finally {
				schedule();
			}
		}
	}

	private static DeleteComplex singleton;

	private int remaining = -1;

	private ScheduledExecutorService executor;

	private DeleteComplex() {
		executor = Executors.newSingleThreadScheduledExecutor();
	}

	public void schedule() {
		int delay;
		if (remaining < 0)
			delay = (int) (Math.random() * NORMAL_SECONDS);
		else if (remaining < AVERAGE)
			delay = RARE_SECONDS + (int)(Math.random() * RARE_SECONDS);
		else if (remaining < MANY)
			delay = NORMAL_SECONDS;
		else
			delay = FREQUENT_SECONDS;
		executor.schedule(new DeleteThread(), delay, TimeUnit.SECONDS);
	}

	public static synchronized void startDeletions() {
		if (singleton == null)
			singleton = new DeleteComplex();
		singleton.schedule();
	}

}
