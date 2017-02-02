package ecommander.model.datatypes;

import java.text.DecimalFormat;

public class IntegerDataType extends FormatDataType {

	IntegerDataType(Type type) {
		super(type);
	}

	@Override
	public Object createValue(String stringValue, Object formatter) {
		try	{
			if (formatter != null) {
				String toParse = stringValue.replaceAll(" ", "");
				return new Integer(((DecimalFormat)formatter).parse(toParse).intValue());
			}
			else return new Integer(Integer.parseInt(stringValue));			
		}
		catch (Exception e) {
			return null;
		}
	}

}
