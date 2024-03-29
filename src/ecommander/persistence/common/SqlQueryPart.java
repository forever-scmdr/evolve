package ecommander.persistence.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;

class SqlQueryPart implements QueryPart {

	private CharSequence code;
	
	public SqlQueryPart(CharSequence sql) {
		code = sql;
	}
	
	public void appendForPrepared(StringBuilder sql) {
		sql.append(code);
	}

	public void appendSimple(StringBuilder sql) {
		sql.append(code);
	}
	
	public int setPrepared(PreparedStatement pstmt, int startIndex) throws SQLException {
		return startIndex;
	}

	@Override
	public String toString() {
		return code.toString();
	}

	public QueryPart createClone() {
		return new SqlQueryPart(code);
	}

}
