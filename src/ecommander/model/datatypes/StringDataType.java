package ecommander.model.datatypes;

import java.text.Format;
import java.text.MessageFormat;

import ecommander.common.Strings;

public class StringDataType extends FormatDataType {

	StringDataType(Type type) {
		super(type);
	}

	@Override
	public Object createValue(String stringValue, Object formatter) {
		return stringValue;
	}

	@Override
	public String outputValue(Object value, Object formatter) {
		if (value == null) return Strings.EMPTY;
		return (String)value;
	}

	@Override
	public Format createFormatter(String format) {
		return new MessageFormat(format);
	}

}
