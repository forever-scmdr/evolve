package ecommander.persistence.mappers;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormatter;

import ecommander.common.Strings;
import ecommander.model.datatypes.DataType.Type;
import ecommander.model.datatypes.DateDataType;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.model.datatypes.FileDataType;
import ecommander.persistence.common.TemplateQuery;

/**
 * Класс, который делает различные зависимые от типа данных операции
 * @author EEEE
 */
public class DataTypeMapper {
	
	abstract class TypeMapper {
	
		protected void setPreparedStatementRequestValue(TemplateQuery pstmt, String value, String pattern) {
			if (value == null) {
				pstmt.sql("NULL");
				return;
			}
			setPreparedStatementRequestValueFine(pstmt, value, pattern);
		}
		
		protected abstract void setPreparedStatementRequestValueFine(TemplateQuery pstmt, String value, String pattern);
		
		protected abstract void setPreparedStatementRequestValues(TemplateQuery pstmt, Collection<String> values);
		
		protected abstract Object createValueFromResultSet(ResultSet rs) throws SQLException;
		
		protected abstract Object createValueFromResultSet(ResultSet rs, int col) throws SQLException;
		
		protected abstract String getTableName();
		
		protected abstract void setPreparedStatementInsertValue(TemplateQuery pstmt, Object value) throws SQLException;
		
		protected abstract Object defaultValue();
	}
	
	private class StringMapper extends TypeMapper {

		@Override
		protected void setPreparedStatementRequestValueFine(TemplateQuery pstmt, String value, String pattern) {
			if (!StringUtils.isBlank(pattern))
				value = pattern.replaceAll("v", value);
			pstmt.setString(value);
		}

		@Override
		protected final String getTableName() {
			return DBConstants.ItemIndexes.STRING_TABLE_NAME;
		}

		@Override
		protected Object createValueFromResultSet(ResultSet rs) throws SQLException {
			return rs.getString(DBConstants.ItemIndexes.VALUE);
		}

		@Override
		protected void setPreparedStatementInsertValue(TemplateQuery pstmt, Object value) throws SQLException {
			pstmt.setString(StringUtils.substring((String)value, 0, 100));
		}

		@Override
		protected Object defaultValue() {
			return Strings.EMPTY;
		}

		@Override
		protected Object createValueFromResultSet(ResultSet rs, int col) throws SQLException {
			return rs.getString(col);
		}

		@Override
		protected void setPreparedStatementRequestValues(TemplateQuery pstmt, Collection<String> values) {
			pstmt.setStringArray(values.toArray(new String[0]));
		}
	}
	
	private class FileMapper extends StringMapper {
		@Override
		protected void setPreparedStatementInsertValue(TemplateQuery pstmt, Object value) throws SQLException {
			if (value instanceof String)
				pstmt.setString(StringUtils.substring((String)value, 0, 49));
			else if (value instanceof FileItem) {
				// Если название файла содержит путь - удалить этот путь
				String fileName = FileDataType.getFileName((FileItem)value);
				pstmt.setString(StringUtils.substring(fileName, 0, 49));
			} else if (value instanceof File) {
				pstmt.setString(StringUtils.substring(((File) value).getName(), 0, 49));
			}
		}
	}

	private class IntMapper extends TypeMapper {

		private Integer createValue(String string) {
			return Integer.parseInt(string);
		}

		@Override
		protected void setPreparedStatementRequestValueFine(TemplateQuery pstmt, String value, String pattern) {
			pstmt.setInt(createValue(value));
		}

		@Override
		protected String getTableName() {
			return DBConstants.ItemIndexes.INT_TABLE_NAME;
		}

		@Override
		protected Object createValueFromResultSet(ResultSet rs) throws SQLException {
			return rs.getInt(DBConstants.ItemIndexes.VALUE);
		}

		@Override
		protected void setPreparedStatementInsertValue(TemplateQuery pstmt, Object value) throws SQLException {
			pstmt.setLong(((Integer)value).longValue());
		}

		@Override
		protected Object defaultValue() {
			return 0;
		}

		@Override
		protected Object createValueFromResultSet(ResultSet rs, int col) throws SQLException {
			return rs.getInt(col);
		}

		@Override
		protected void setPreparedStatementRequestValues(TemplateQuery pstmt, Collection<String> values) {
			Integer[] array = new Integer[values.size()];
			int i = 0;
			for (String val : values) {
				array[i++] = createValue(val);
			}
			pstmt.setIntArray(array);
		}
	}
	
	private class ByteMapper extends IntMapper {

		private Byte createValue(String string) {
			return Byte.parseByte(string);
		}

		@Override
		protected void setPreparedStatementRequestValue(TemplateQuery pstmt, String value, String pattern) {
			pstmt.setByte(createValue(value));
		}
		
		@Override
		protected Object createValueFromResultSet(ResultSet rs) throws SQLException {
			return (byte)rs.getInt(DBConstants.ItemIndexes.VALUE);
		}
		
		@Override
		protected Object createValueFromResultSet(ResultSet rs, int col) throws SQLException {
			return rs.getByte(col);
		}
		
		@Override
		protected void setPreparedStatementInsertValue(TemplateQuery pstmt, Object value) throws SQLException {
			pstmt.setLong(((Byte)value).longValue());
		}
		
		@Override
		protected Object defaultValue() {
			return (byte)0;
		}
	}

	private class LongMapper extends IntMapper {

		private Long createValue(String string) {
			return Long.parseLong(string);
		}
		
		@Override
		protected void setPreparedStatementRequestValue(TemplateQuery pstmt, String value, String pattern) {
			pstmt.setLong(createValue(value));
		}
		
		@Override
		protected Object createValueFromResultSet(ResultSet rs) throws SQLException {
			return rs.getLong(DBConstants.ItemIndexes.VALUE);
		}
		
		@Override
		protected Object createValueFromResultSet(ResultSet rs, int col) throws SQLException {
			return rs.getLong(col);
		}
		
		@Override
		protected void setPreparedStatementInsertValue(TemplateQuery pstmt, Object value) throws SQLException {
			pstmt.setLong(((Long)value).longValue());
		}
		
		@Override
		protected Object defaultValue() {
			return (long)0;
		}

		@Override
		protected void setPreparedStatementRequestValues(TemplateQuery pstmt, Collection<String> values) {
			Long[] array = new Long[values.size()];
			int i = 0;
			for (String val : values) {
				array[i++] = createValue(val);
			}
			pstmt.setLongArray(array);
		}
	}
	
	private class DateMapper extends LongMapper {
		
		private Long createValue(String string, String pattern) {
			Long date = null;
			if (!StringUtils.isBlank(pattern)) {
				DateTimeFormatter format = DateDataType.getFormatter(pattern);
				date = DateDataType.parseDate(string, format);
				if (date == null)
					throw new IllegalArgumentException("Date format pattern '" + pattern + "' does not match value '" + string + "'");
			} else {
				date = DateDataType.parseDate(string);
				if (date == null)
					throw new IllegalArgumentException("Can not parse string '" + string + "' representing Long value for a date value");
			}
			return date;
		}
		
		@Override
		protected void setPreparedStatementRequestValue(TemplateQuery pstmt, String value, String pattern) {
			pstmt.setLong(createValue(value, pattern));
		}
	}
	
	private class DoubleMapper extends TypeMapper {

		protected Double createValue(String string) {
			return DoubleDataType.parse(string);
		}

		@Override
		protected void setPreparedStatementRequestValueFine(TemplateQuery pstmt, String value, String pattern) {
			pstmt.setDouble(createValue(value));
		}

		@Override
		protected final String getTableName() {
			return DBConstants.ItemIndexes.DOUBLE_TABLE_NAME;
		}

		@Override
		protected Object createValueFromResultSet(ResultSet rs) throws SQLException {
			return rs.getDouble(DBConstants.ItemIndexes.VALUE);
		}

		@Override
		protected void setPreparedStatementInsertValue(TemplateQuery pstmt, Object value) throws SQLException {
			pstmt.setDouble((Double)value);
		}

		@Override
		protected Object defaultValue() {
			return (double)0;
		}

		@Override
		protected Object createValueFromResultSet(ResultSet rs, int col) throws SQLException {
			return rs.getDouble(col);
		}

		@Override
		protected void setPreparedStatementRequestValues(TemplateQuery pstmt, Collection<String> values) {
			Double[] array = new Double[values.size()];
			int i = 0;
			for (String val : values) {
				array[i++] = createValue(val);
			}
			pstmt.setDoubleArray(array);
		}
	}	
	
	private class AssociatedMapper extends LongMapper {
		@Override
		protected String getTableName() {
			return DBConstants.ItemIndexes.ASSOCIATED_TABLE_NAME;
		}
	}
	
	private static DataTypeMapper instance = new DataTypeMapper();
	
	private HashMap<Type, TypeMapper> typeMappers = null;
	
	private DataTypeMapper() {
		typeMappers = new HashMap<Type, TypeMapper>();
		StringMapper stringMapper = new StringMapper();
		FileMapper fileMapper = new FileMapper();
		typeMappers.put(Type.BYTE, new ByteMapper());
		typeMappers.put(Type.DATE, new DateMapper());
		typeMappers.put(Type.DOUBLE, new DoubleMapper());
		typeMappers.put(Type.FILE, fileMapper);
		typeMappers.put(Type.INTEGER, new IntMapper());
		typeMappers.put(Type.LONG, new LongMapper());
		typeMappers.put(Type.PICTURE, fileMapper);
		typeMappers.put(Type.STRING, stringMapper);
		typeMappers.put(Type.TINY_TEXT, stringMapper);
		typeMappers.put(Type.SHORT_TEXT, stringMapper);
		typeMappers.put(Type.TEXT, stringMapper);
		typeMappers.put(Type.PLAIN_TEXT, stringMapper);
		typeMappers.put(Type.FILTER, stringMapper);
		typeMappers.put(Type.ASSOCIATED, new AssociatedMapper());
		typeMappers.put(Type.XML, stringMapper);
	}
	/**
	 * Генерирует SQL для инсертов и апдейтов 
	 * @param typeName
	 * @param value
	 * @return
	 * @throws SQLException 
	 */
	public static void appendPreparedStatementInsertValue(Type type, TemplateQuery builder, Object value) throws SQLException {
		getMapper().typeMappers.get(type).setPreparedStatementInsertValue(builder, value);
	}
	/**
	 * Генерирует SQL для поисковых запросов. Правильно оформляет искомое значение (в формате, который соответствует типу данных занчения)
	 * @param typeName
	 * @param value
	 * @param requestSign
	 * @param pattern
	 * @return
	 * @throws SQLException 
	 */
	public static void appendPreparedStatementRequestValue(Type type, TemplateQuery builder, String value, String pattern) {
		getMapper().typeMappers.get(type).setPreparedStatementRequestValue(builder, value, pattern);
	}
	/**
	 * Генерирует SQL для поисковых запросов, аналогично appendPreparedStatementRequestValue, но работает с массивами значений
	 * @param typeName
	 * @param builder
	 * @param value
	 */
	public static void appendPreparedStatementRequestValues(Type type, TemplateQuery builder, Collection<String> value) {
		getMapper().typeMappers.get(type).setPreparedStatementRequestValues(builder, value);
	}
	/**
	 * Получить название колонки для определенного типа данных
	 * @param typeName
	 * @return
	 */
	public static String getTableName(Type type) {
		return getMapper().typeMappers.get(type).getTableName();
	}
	/**
	 * Создать значение из резалт сета
	 * @param typeName
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static Object createValue(Type type, ResultSet rs) throws SQLException {
		return getMapper().typeMappers.get(type).createValueFromResultSet(rs);
	}
	/**
	 * Создать значение из резалт сета
	 * @param typeName
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static Object createValue(Type type, ResultSet rs, int col) throws SQLException {
		return getMapper().typeMappers.get(type).createValueFromResultSet(rs, col);
	}
	
	private static DataTypeMapper getMapper() {
		return instance;
	}
}