package ecommander.persistence.commandunits;

import java.sql.Statement;

import ecommander.common.ServerLogger;

public class SQLCommandUnit extends DBPersistenceCommandUnit {
	
	private String sql;
	
	public SQLCommandUnit(String sql) {
		this.sql = sql;
	}
	
	public void execute() throws Exception {
		Statement stmt = getTransactionContext().getConnection().createStatement();
		try {
			ServerLogger.debug(sql);
			stmt.executeUpdate(sql);
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

}
