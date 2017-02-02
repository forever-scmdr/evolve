package ecommander.model.datatypes;

import java.util.HashMap;

import ecommander.common.Strings;

public class AssociatedDataType extends DataType {

	AssociatedDataType(Type type) {
		super(type);
	}

	@Override
	public Object createValue(String stringValue, Object formatter) {
		try	{
			return new Long(Long.parseLong(stringValue));			
		}
		catch (Exception e)	{
			return new Long(0);
		}
	}

	@Override
	public String outputValue(Object value, Object formatter) {
		if (value == null)
			return Strings.EMPTY;
		return value.toString();
	}

	@Override
	public boolean hasMeta() {
		return false; // Нет метаданных
	}

	@Override
	public HashMap<String, String> getMeta(Object value, Object... extraParams) {
		return null; // Нет метаданных
	}

}
