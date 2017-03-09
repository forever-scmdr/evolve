package ecommander.persistence.common;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

class ValueArrayQueryPart implements QueryPart {


	private PreparedValue[] values;
	
	private ValueArrayQueryPart(PreparedValue[] values) {
		this.values = new PreparedValue[values.length];
		for (int i = 0; i < values.length; i++)
			this.values[i] = values[i].createClone();
	}
	
	ValueArrayQueryPart(Integer[] array) {
		values = new PreparedValue[array.length];
		for (int i = 0; i < array.length; i++) {
			values[i] = PreparedValueFacroty.create(array[i]);
		}
	}
	
	ValueArrayQueryPart(Long[] array) {
		values = new PreparedValue[array.length];
		for (int i = 0; i < array.length; i++) {
			values[i] = PreparedValueFacroty.create(array[i]);
		}
	}
	
	ValueArrayQueryPart(Double[] array) {
		values = new PreparedValue[array.length];
		for (int i = 0; i < array.length; i++) {
			values[i] = PreparedValueFacroty.create(array[i]);
		}
	}

	ValueArrayQueryPart(BigDecimal[] array) {
		values = new PreparedValue[array.length];
		for (int i = 0; i < array.length; i++) {
			values[i] = PreparedValueFacroty.create(array[i]);
		}
	}
	
	ValueArrayQueryPart(String[] array) {
		values = new PreparedValue[array.length];
		for (int i = 0; i < array.length; i++) {
			values[i] = PreparedValueFacroty.create(array[i]);
		}
	}
	
	public void appendForPrepared(StringBuilder sql) {
		if (values.length == 0) {
			sql.append("NULL");
			return;
		}
		for (int i = 0; i < values.length; i++)
			sql.append("?,");
		sql.deleteCharAt(sql.length() - 1);
	}

	public void appendSimple(StringBuilder sql) {
		if (values.length == 0) {
			sql.append("NULL");
			return;
		}
		for (PreparedValue val : values) {
			sql.append(val.simpleSql() + ',');			
		}
		sql.deleteCharAt(sql.length() - 1);
	}
	
	public int setPrepared(PreparedStatement pstmt, int startIndex) throws SQLException {
		for (PreparedValue value : values) {
			value.set(pstmt, startIndex);
			startIndex++;
		}
		return startIndex;
	}

	@Override
	public String toString() {
		return StringUtils.join(values, ',');
	}

	public QueryPart createClone() {
		return new ValueArrayQueryPart(values);
	}

}
