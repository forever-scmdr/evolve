package ecommander.persistence.common;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class PreparedValueFacroty {
	/* *******************************************************************************************
	 * *******************************************************************************************
	 * 
	 * 							КЛАССЫ ДЛЯ ЗНАЧЕНИЙ
	 * 
	 */
	private static class ByteValue implements PreparedValue {
		private byte value;
		
		private ByteValue(byte value) { this.value = value; }
		
		public final void set(PreparedStatement pstmt, int index) throws SQLException { pstmt.setByte(index, value); }

		@Override
		public String toString() { return ((Byte)value).toString(); }

		public PreparedValue createClone() { return new ByteValue(value); }

		public String simpleSql() { return toString(); }
	}
	
	private static class IntValue implements PreparedValue {
		private int value;
		
		private IntValue(int value) { this.value = value; }
		
		public final void set(PreparedStatement pstmt, int index) throws SQLException { pstmt.setInt(index, value); }
		
		@Override
		public String toString() { return ((Integer)value).toString(); }
		
		public PreparedValue createClone() { return new IntValue(value); }
		
		public String simpleSql() { return toString(); }
	}
	
	private static class LongValue implements PreparedValue {
		private long value;
		
		private LongValue(long value) { this.value = value; }
		
		public final void set(PreparedStatement pstmt, int index) throws SQLException { pstmt.setLong(index, value); }
		
		@Override
		public String toString() { return ((Long)value).toString(); }
		
		public PreparedValue createClone() { return new LongValue(value); }
		
		public String simpleSql() { return toString(); }
	}

	private static class DoubleValue implements PreparedValue {
		private double value;
		
		private DoubleValue(double value) { this.value = value; }
		
		public final void set(PreparedStatement pstmt, int index) throws SQLException { pstmt.setDouble(index, value); }
		
		@Override
		public String toString() { return ((Double)value).toString(); }
		
		public PreparedValue createClone() { return new DoubleValue(value); }
		
		public String simpleSql() { return toString(); }
	}

	private static class DecimalValue implements PreparedValue {
		private BigDecimal value;

		private DecimalValue(BigDecimal value) { this.value = value; }

		public final void set(PreparedStatement pstmt, int index) throws SQLException { pstmt.setBigDecimal(index, value); }

		@Override
		public String toString() { return ((BigDecimal)value).toString(); }

		public PreparedValue createClone() { return new DecimalValue(value); }

		public String simpleSql() { return toString(); }
	}
	
	private static class StringValue implements PreparedValue {
		private String value;
		
		private StringValue(String value) { this.value = value; }
		
		public final void set(PreparedStatement pstmt, int index) throws SQLException { pstmt.setString(index, value); }
		
		@Override
		public String toString() { return value; }
		
		public PreparedValue createClone() { return new StringValue(value); }
		
		public String simpleSql() { return "'" + value + "'"; }
	}
	
	static PreparedValue create(byte value) {
		return new ByteValue(value);
	}
	
	static PreparedValue create(int value) {
		return new IntValue(value);
	}
	
	static PreparedValue create(long value) {
		return new LongValue(value);
	}
	
	static PreparedValue create(double value) {
		return new DoubleValue(value);
	}

	static PreparedValue create(BigDecimal value) {
		return new DecimalValue(value);
	}
	
	static PreparedValue create(String value) {
		return new StringValue(value);
	}
}
