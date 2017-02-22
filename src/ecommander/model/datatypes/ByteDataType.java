package ecommander.model.datatypes;

import java.text.DecimalFormat;

public class ByteDataType extends FormatDataType {

	public ByteDataType(Type type) {
		super(type);
	}

	@Override
	public Object createValue(String stringValue, Object formatter) {
		try	{
			if (formatter != null) {
				String toParse = stringValue.replaceAll(" ", "");
				return new Byte(((DecimalFormat)formatter).parse(toParse).byteValue());
			}
			else return new Byte(Byte.parseByte(stringValue));			
		}
		catch (Exception e) {
			return null;
		}
	}
}
