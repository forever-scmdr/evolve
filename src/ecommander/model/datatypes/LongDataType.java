package ecommander.model.datatypes;

import java.text.DecimalFormat;

public class LongDataType extends FormatDataType {

	LongDataType(Type type) {
		super(type);
	}

	@Override
	public Object createValue(String stringValue, Object formatter) {
		try	{
			if (formatter != null) {
				String toParse = stringValue.replaceAll(" ", "");
				return new Long(((DecimalFormat)formatter).parse(toParse).longValue());
			}
			else return new Long(Long.parseLong(stringValue));			
		}
		catch (Exception e)	{
			return null;
		}
	}

}
