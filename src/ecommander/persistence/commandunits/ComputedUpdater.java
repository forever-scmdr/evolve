package ecommander.persistence.commandunits;

import ecommander.fwk.MysqlConnector;
import ecommander.model.User;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Запускает обновление всех computed айтемов из таблицы лога обновлений
 * Created by E on 30/6/2017.
 */
public class ComputedUpdater implements DBConstants.ComputedLog {
	private static final Object SEMAPHORE = new Object();
	private static volatile boolean inProgress = false;

	public static void update() throws Exception {
		if (!inProgress) {
			synchronized (SEMAPHORE) {
				inProgress = true;
				boolean hasFound = true;
				while (hasFound) {
					hasFound = false;
					TemplateQuery select = new TemplateQuery("Select from computed log");
					select.SELECT(L_ITEM).FROM(COMPUTED_LOG_TBL).LIMIT(50);
					ArrayList<Long> itemsToUpdate = new ArrayList<>();
					try (Connection conn = MysqlConnector.getConnection();
					     PreparedStatement pstmt = select.prepareQuery(conn)) {
						ResultSet rs = pstmt.executeQuery();
						while (rs.next()) {
							itemsToUpdate.add(rs.getLong(1));
							hasFound = true;
						}
					}
					for (Long itemId : itemsToUpdate) {
						DelayedTransaction tr = new DelayedTransaction(User.getDefaultUser());
						tr.addCommandUnit(new UpdateComputedDBUnit(itemId));
						tr.execute();
					}
				}
				inProgress = false;
			}
		}
	}
}
