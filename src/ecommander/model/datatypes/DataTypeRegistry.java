package ecommander.model.datatypes;

import java.util.HashMap;

import ecommander.model.datatypes.DataType.Type;

/**
 * Возвращает объекты-типы данных по названию типа даных
 * Существует только по одному обхекту типа данных на каждый тип, всего 9 объектов для всех параметров всех айтемов.
 * Т. е. объекты типы данных это по сути синглетоны
 * @author EEEE
 */
public class DataTypeRegistry {
	
	private static DataTypeRegistry singleton;
	
	private HashMap<Type, DataType> dataTypes = null;
	
	private DataTypeRegistry() {
		dataTypes = new HashMap<Type, DataType>();
		dataTypes.put(Type.BYTE, new ByteDataType(Type.BYTE));
		dataTypes.put(Type.INTEGER, new IntegerDataType(Type.INTEGER));
		dataTypes.put(Type.LONG, new LongDataType(Type.LONG));
		dataTypes.put(Type.DATE, new DateDataType(Type.DATE));
		dataTypes.put(Type.DOUBLE, new DoubleDataType(Type.DOUBLE));
		dataTypes.put(Type.STRING, new StringDataType(Type.STRING));
		dataTypes.put(Type.TINY_TEXT, new StringDataType(Type.TINY_TEXT));
		dataTypes.put(Type.SHORT_TEXT, new StringDataType(Type.SHORT_TEXT));
		dataTypes.put(Type.TEXT, new StringDataType(Type.TEXT));
		dataTypes.put(Type.PLAIN_TEXT, new StringDataType(Type.PLAIN_TEXT));
		dataTypes.put(Type.FILE, new FileDataType(Type.FILE));
		dataTypes.put(Type.PICTURE, new FileDataType(Type.PICTURE));
		dataTypes.put(Type.FILTER, new StringDataType(Type.FILTER));
		dataTypes.put(Type.XML, new StringDataType(Type.XML));
		dataTypes.put(Type.ASSOCIATED, new AssociatedDataType(Type.ASSOCIATED));
	}
	
	private static DataTypeRegistry getRegistry() {
		if (singleton == null) singleton = new DataTypeRegistry();
		return singleton;
	}
	
	public static DataType getType(Type type) {
		return getRegistry().dataTypes.get(type);
	}
	
	public static boolean isTypeNameValid(String typeName) {
		try {
			Type.fromString(typeName);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}