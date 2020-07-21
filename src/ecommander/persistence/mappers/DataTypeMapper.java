package ecommander.persistence.mappers;

import ecommander.fwk.Pair;
import ecommander.fwk.Strings;
import ecommander.model.datatypes.DataType.Type;
import ecommander.model.datatypes.DateDataType;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.model.datatypes.FileDataType;
import ecommander.persistence.common.TemplateQuery;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.*;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

/**
 * Класс, который делает различные зависимые от типа данных операции
 * @author EEEE
 */
public class DataTypeMapper {
	
	abstract static class TypeMapper {
	
		protected void setPreparedStatementRequestValue(TemplateQuery pstmt, String value, String pattern) {
			if (value == null) {
				pstmt.sql("NULL");
				return;
			}
			setPreparedStatementRequestValueFine(pstmt, value, pattern);
		}
		
		protected abstract void setPreparedStatementRequestValueFine(TemplateQuery pstmt, String value, String pattern);
		
		protected abstract void setPreparedStatementRequestValues(TemplateQuery pstmt, Collection<String> values);

		protected abstract void setLuceneDocumentField(Document itemDoc, String fieldName, Object value);
		
		protected abstract Object createValueFromResultSet(ResultSet rs) throws SQLException;
		
		protected abstract Object createValueFromResultSet(ResultSet rs, int col) throws SQLException;
		
		protected abstract String getTableName();
		
		protected abstract void setPreparedStatementInsertValue(TemplateQuery pstmt, Object value) throws SQLException;
	}
	
	private static class StringMapper extends TypeMapper {

		@Override
		protected void setPreparedStatementRequestValueFine(TemplateQuery pstmt, String value, String pattern) {
			if (!StringUtils.isBlank(pattern))
				value = pattern.replaceAll("v", value);
			pstmt.string(value);
		}

		@Override
		protected final String getTableName() {
			return DBConstants.ItemIndexes.STRING_INDEX_TBL;
		}

		@Override
		protected Object createValueFromResultSet(ResultSet rs) throws SQLException {
			return rs.getString(DBConstants.ItemIndexes.II_VALUE);
		}

		@Override
		protected void setPreparedStatementInsertValue(TemplateQuery pstmt, Object value) throws SQLException {
			pstmt.string(StringUtils.substring((String)value, 0, 100));
		}

		@Override
		protected Object createValueFromResultSet(ResultSet rs, int col) throws SQLException {
			return rs.getString(col);
		}

		@Override
		protected void setPreparedStatementRequestValues(TemplateQuery pstmt, Collection<String> values) {
			pstmt.stringArray(values.toArray(new String[0]));
		}

		@Override
		protected void setLuceneDocumentField(Document itemDoc, String fieldName, Object value) {
			itemDoc.add(new StringField(fieldName, (String) value, Field.Store.NO));
		}
	}
	
	private static class FileMapper extends StringMapper {
		@Override
		protected void setPreparedStatementInsertValue(TemplateQuery pstmt, Object value) throws SQLException {
			if (value instanceof String) {
				pstmt.string(StringUtils.substring((String) value, 0, 49));
			} else if (value instanceof FileItem) {
				// Если название файла содержит путь - удалить этот путь
				String fileName = FileDataType.getFileName((FileItem)value);
				pstmt.string(StringUtils.substring(fileName, 0, 49));
			} else if (value instanceof File) {
				pstmt.string(StringUtils.substring(((File) value).getName(), 0, 49));
			} else if (value instanceof URL) {
				pstmt.string(StringUtils.substring(Strings.getFileName(((URL) value).getFile()), 0, 49));
			}
		}
	}

	private static class IntMapper extends TypeMapper {

		private Integer createValue(String string) {
			return Integer.parseInt(string);
		}

		@Override
		protected void setPreparedStatementRequestValueFine(TemplateQuery pstmt, String value, String pattern) {
			pstmt.int_(createValue(value));
		}

		@Override
		protected String getTableName() {
			return DBConstants.ItemIndexes.INT_INDEX_TBL;
		}

		@Override
		protected Object createValueFromResultSet(ResultSet rs) throws SQLException {
			return rs.getInt(DBConstants.ItemIndexes.II_VALUE);
		}

		@Override
		protected void setPreparedStatementInsertValue(TemplateQuery pstmt, Object value) throws SQLException {
			pstmt.long_(((Integer)value).longValue());
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
			pstmt.intArray(array);
		}

		@Override
		protected void setLuceneDocumentField(Document itemDoc, String fieldName, Object value) {
			itemDoc.add(new IntPoint(fieldName, (Integer) value));
		}
	}
	
	private static class ByteMapper extends IntMapper {

		private Byte createValue(String string) {
			return Byte.parseByte(string);
		}

		@Override
		protected void setPreparedStatementRequestValue(TemplateQuery pstmt, String value, String pattern) {
			pstmt.byte_(createValue(value));
		}
		
		@Override
		protected Object createValueFromResultSet(ResultSet rs) throws SQLException {
			return (byte)rs.getInt(DBConstants.ItemIndexes.II_VALUE);
		}
		
		@Override
		protected Object createValueFromResultSet(ResultSet rs, int col) throws SQLException {
			return rs.getByte(col);
		}
		
		@Override
		protected void setPreparedStatementInsertValue(TemplateQuery pstmt, Object value) throws SQLException {
			pstmt.long_(((Byte)value).longValue());
		}
	}

	private static class LongMapper extends IntMapper {

		private Long createValue(String string) {
			return Long.parseLong(string);
		}
		
		@Override
		protected void setPreparedStatementRequestValue(TemplateQuery pstmt, String value, String pattern) {
			pstmt.long_(createValue(value));
		}
		
		@Override
		protected Object createValueFromResultSet(ResultSet rs) throws SQLException {
			return rs.getLong(DBConstants.ItemIndexes.II_VALUE);
		}
		
		@Override
		protected Object createValueFromResultSet(ResultSet rs, int col) throws SQLException {
			return rs.getLong(col);
		}
		
		@Override
		protected void setPreparedStatementInsertValue(TemplateQuery pstmt, Object value) throws SQLException {
			pstmt.long_(((Long)value).longValue());
		}

		@Override
		protected void setPreparedStatementRequestValues(TemplateQuery pstmt, Collection<String> values) {
			Long[] array = new Long[values.size()];
			int i = 0;
			for (String val : values) {
				array[i++] = createValue(val);
			}
			pstmt.longArray(array);
		}

		@Override
		protected void setLuceneDocumentField(Document itemDoc, String fieldName, Object value) {
			itemDoc.add(new LongPoint(fieldName, (Long) value));
		}
	}
	
	private static class DateMapper extends LongMapper {
		
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
			pstmt.long_(createValue(value, pattern));
		}
	}
	
	private static class DoubleMapper extends TypeMapper {

		protected Double createValue(String string) {
			return DoubleDataType.parse(string);
		}

		@Override
		protected void setPreparedStatementRequestValueFine(TemplateQuery pstmt, String value, String pattern) {
			pstmt.double_(createValue(value));
		}

		@Override
		protected final String getTableName() {
			return DBConstants.ItemIndexes.DOUBLE_INDEX_TBL;
		}

		@Override
		protected Object createValueFromResultSet(ResultSet rs) throws SQLException {
			return rs.getDouble(DBConstants.ItemIndexes.II_VALUE);
		}

		@Override
		protected void setPreparedStatementInsertValue(TemplateQuery pstmt, Object value) throws SQLException {
			pstmt.double_((Double)value);
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
			pstmt.doubleArray(array);
		}

		@Override
		protected void setLuceneDocumentField(Document itemDoc, String fieldName, Object value) {
			itemDoc.add(new DoublePoint(fieldName, (Double) value));
		}
	}

	private static class DecimalMapper extends TypeMapper {

		private final int scale;

		private DecimalMapper(int scale) {
			this.scale = scale;
		}

		protected BigDecimal createValue(String string) {
			return DecimalDataType.parse(string, scale);
		}

		@Override
		protected void setPreparedStatementRequestValueFine(TemplateQuery pstmt, String value, String pattern) {
			pstmt.decimal(createValue(value));
		}

		@Override
		protected final String getTableName() {
			return DBConstants.ItemIndexes.DECIMAL_INDEX_TBL;
		}

		@Override
		protected Object createValueFromResultSet(ResultSet rs) throws SQLException {
			return rs.getBigDecimal(DBConstants.ItemIndexes.II_VALUE);
		}

		@Override
		protected void setPreparedStatementInsertValue(TemplateQuery pstmt, Object value) throws SQLException {
			pstmt.decimal((BigDecimal) value);
		}

		@Override
		protected Object createValueFromResultSet(ResultSet rs, int col) throws SQLException {
			return rs.getBigDecimal(col);
		}

		@Override
		protected void setPreparedStatementRequestValues(TemplateQuery pstmt, Collection<String> values) {
			BigDecimal[] array = new BigDecimal[values.size()];
			int i = 0;
			for (String val : values) {
				array[i++] = createValue(val);
			}
			pstmt.decimalArray(array);
		}

		@Override
		protected void setLuceneDocumentField(Document itemDoc, String fieldName, Object value) {
			itemDoc.add(new DoublePoint(fieldName, ((BigDecimal) value).doubleValue()));
		}
	}

	private static class TupleMapper extends TypeMapper {

		@Override
		protected void setPreparedStatementRequestValueFine(TemplateQuery pstmt, String value, String pattern) {
			if (!StringUtils.isBlank(pattern))
				value = pattern.replaceAll("v", value);
			pstmt.string(value);
		}

		@Override
		protected final String getTableName() {
			return DBConstants.ItemIndexes.STRING_INDEX_TBL;
		}

		@Override
		protected Object createValueFromResultSet(ResultSet rs) throws SQLException {
			return rs.getString(DBConstants.ItemIndexes.II_VALUE);
		}

		@Override
		protected void setPreparedStatementInsertValue(TemplateQuery pstmt, Object value) throws SQLException {
			pstmt.string(StringUtils.substring(((Pair<String, String>)value).getLeft(), 0, 100));
		}

		@Override
		protected Object createValueFromResultSet(ResultSet rs, int col) throws SQLException {
			return rs.getString(col);
		}

		@Override
		protected void setPreparedStatementRequestValues(TemplateQuery pstmt, Collection<String> values) {
			pstmt.stringArray(values.toArray(new String[0]));
		}

		@Override
		protected void setLuceneDocumentField(Document itemDoc, String fieldName, Object value) {
			itemDoc.add(new StringField(fieldName, ((Pair<String, String>)value).getLeft(), Field.Store.NO));
		}
	}


	private static DataTypeMapper instance = new DataTypeMapper();
	
	private HashMap<Type, TypeMapper> typeMappers = null;
	
	private DataTypeMapper() {
		typeMappers = new HashMap<>();
		StringMapper stringMapper = new StringMapper();
		FileMapper fileMapper = new FileMapper();
		typeMappers.put(Type.BYTE, new ByteMapper());
		typeMappers.put(Type.DATE, new DateMapper());
		typeMappers.put(Type.DOUBLE, new DoubleMapper());
		typeMappers.put(Type.DECIMAL, new DecimalMapper(DecimalDataType.DECIMAL));
		typeMappers.put(Type.CURRENCY, new DecimalMapper(DecimalDataType.CURRENCY));
		typeMappers.put(Type.CURRENCY_PRECISE, new DecimalMapper(DecimalDataType.CURRENCY_PRECISE));
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
		typeMappers.put(Type.XML, stringMapper);
		typeMappers.put(Type.TUPLE, new TupleMapper());
	}

	/**
	 * Генерирует SQL для инсертов и апдейтов
	 * @param type
	 * @param builder
	 * @param value
	 * @throws SQLException
	 */
	public static void appendPreparedStatementInsertValue(Type type, TemplateQuery builder, Object value) throws SQLException {
		getMapper().typeMappers.get(type).setPreparedStatementInsertValue(builder, value);
	}

	/**
	 * Генерирует SQL для поисковых запросов. Правильно оформляет искомое значение (в формате, который соответствует типу данных занчения)
	 * @param type
	 * @param builder
	 * @param value
	 * @param pattern
	 */
	public static void appendPreparedStatementRequestValue(Type type, TemplateQuery builder, String value, String pattern) {
		getMapper().typeMappers.get(type).setPreparedStatementRequestValue(builder, value, pattern);
	}

	/**
	 * Генерирует SQL для поисковых запросов, аналогично appendPreparedStatementRequestValue, но работает с массивами значений
	 * @param type
	 * @param builder
	 * @param value
	 */
	public static void appendPreparedStatementRequestValues(Type type, TemplateQuery builder, Collection<String> value) {
		getMapper().typeMappers.get(type).setPreparedStatementRequestValues(builder, value);
	}

	/**
	 * Добавляет к документу Lucene айтема поле со значением в зависимости от типа параметра
	 * @param type
	 * @param doc
	 * @param fieldName
	 * @param value
	 */
	public static void setLuceneItemDocField(Type type, Document doc, String fieldName, Object value) {
		getMapper().typeMappers.get(type).setLuceneDocumentField(doc, fieldName, value);
	}

	/**
	 * Получить название колонки для определенного типа данных
	 * @param type
	 * @return
	 */
	public static String getTableName(Type type) {
		return getMapper().typeMappers.get(type).getTableName();
	}

	/**
	 * Создать значение из резалт сета
	 * @param type
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static Object createValue(Type type, ResultSet rs) throws SQLException {
		return getMapper().typeMappers.get(type).createValueFromResultSet(rs);
	}

	/**
	 * Создать значение из резалт сета
	 * @param type
	 * @param rs
	 * @param col
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