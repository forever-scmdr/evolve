package ecommander.persistence.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;

class ValueQueryPart implements QueryPart {


	private PreparedValue value;
	
	private ValueQueryPart(PreparedValue value) {
		this.value = value.createClone();
	}
	
	ValueQueryPart(byte value) {
		this.value = PreparedValueFacroty.create(value);
	}

	ValueQueryPart(int value) {
		this.value = PreparedValueFacroty.create(value);
	}
	
	ValueQueryPart(long value) {
		this.value = PreparedValueFacroty.create(value);
	}
	
	ValueQueryPart(double value) {
		this.value = PreparedValueFacroty.create(value);
	}
	
	ValueQueryPart(String value) {
		this.value = PreparedValueFacroty.create(value);
	}
	
	public void appendForPrepared(StringBuilder sql) {
		sql.append('?');
	}

	public void appendSimple(StringBuilder sql) {
		sql.append(value.simpleSql());
	}
	
	public int setPrepared(PreparedStatement pstmt, int startIndex) throws SQLException {
		value.set(pstmt, startIndex);
		return startIndex + 1;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	public QueryPart createClone() {
		return new ValueQueryPart(value);
	}

	
}
